#include <jni.h>
#include <android/log.h>
#include <string>
#include <vector>
#include <mutex>
#include <atomic>

#include "llama.h"

#define LOG_TAG "KaedeLLM"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

static llama_model* g_model = nullptr;
static llama_context* g_ctx = nullptr;
static std::mutex g_mutex;
static std::atomic<bool> g_is_generating(false);
static std::atomic<bool> g_should_cancel(false);

extern "C" {

JNIEXPORT jboolean JNICALL
Java_com_kaede_app_ai_LlamaCpp_nativeInit(JNIEnv* env, jobject thiz) {
    LOGD("Initializing llama.cpp backend");
    llama_backend_init();
    return JNI_TRUE;
}

JNIEXPORT jboolean JNICALL
Java_com_kaede_app_ai_LlamaCpp_nativeLoadModel(
    JNIEnv* env, jobject thiz,
    jstring model_path, jint n_ctx, jint n_threads) {
    
    std::lock_guard<std::mutex> lock(g_mutex);
    
    if (g_model) { llama_free_model(g_model); g_model = nullptr; }
    if (g_ctx) { llama_free(g_ctx); g_ctx = nullptr; }
    
    const char* path = env->GetStringUTFChars(model_path, nullptr);
    LOGD("Loading model: %s", path);
    
    struct llama_model_params mparams = llama_model_default_params();
    mparams.n_ctx = n_ctx;
    
    g_model = llama_load_model_from_file(path, mparams);
    env->ReleaseStringUTFChars(model_path, path);
    
    if (!g_model) {
        LOGE("Failed to load model");
        return JNI_FALSE;
    }
    
    struct llama_context_params cparams = llama_context_default_params();
    cparams.n_ctx = n_ctx;
    cparams.n_threads = n_threads;
    cparams.n_threads_batch = n_threads;
    
    g_ctx = llama_init_from_model(g_model, cparams);
    
    if (!g_ctx) {
        LOGE("Failed to create context");
        llama_free_model(g_model);
        g_model = nullptr;
        return JNI_FALSE;
    }
    
    LOGD("Model loaded successfully");
    return JNI_TRUE;
}

JNIEXPORT void JNICALL
Java_com_kaede_app_ai_LlamaCpp_nativeGenerate(
    JNIEnv* env, jobject thiz,
    jstring prompt, jint max_tokens,
    jfloat temperature, jint top_k,
    jfloat top_p, jfloat repeat_penalty,
    jobject callback) {
    
    std::lock_guard<std::mutex> lock(g_mutex);
    
    if (!g_ctx || !g_model) {
        LOGE("Model not loaded");
        return;
    }
    
    g_is_generating = true;
    g_should_cancel = false;
    
    const char* prompt_str = env->GetStringUTFChars(prompt, nullptr);
    std::string prompt_text(prompt_str);
    env->ReleaseStringUTFChars(prompt, prompt_str);
    
    LOGD("Generating for prompt: %s", prompt_text.substr(0, 50).c_str());
    
    // Tokenize
    std::vector<llama_token> tokens;
    tokens.resize(prompt_text.size() + 256);
    int n = llama_tokenize(
        llama_get_model(g_ctx),
        prompt_text.c_str(),
        prompt_text.size(),
        tokens.data(),
        tokens.size(),
        true, true
    );
    tokens.resize(n);
    
    jclass cb_class = env->GetObjectClass(callback);
    jmethodID on_token = env->GetMethodID(cb_class, "onToken", "(Ljava/lang/String;)V");
    
    if (!on_token) {
        LOGE("Callback method not found");
        g_is_generating = false;
        return;
    }
    
    // Decode prompt
    llama_batch batch = llama_batch_get_one(tokens.data(), tokens.size());
    if (llama_decode(g_ctx, batch) != 0) {
        LOGE("Failed to decode prompt");
        g_is_generating = false;
        return;
    }
    
    int n_predict = max_tokens;
    
    while (n_predict > 0 && !g_should_cancel) {
        llama_token new_token = llama_sampler_sample(
            llama_get_sampler(g_ctx, -1),
            g_ctx, -1
        );
        
        if (llama_vocab_is_eog(llama_get_model(g_ctx), new_token)) {
            LOGD("EOS detected");
            break;
        }
        
        std::string piece = llama_token_to_piece(llama_get_model(g_ctx), new_token);
        
        jstring jpiece = env->NewStringUTF(piece.c_str());
        env->CallVoidMethod(callback, on_token, jpiece);
        env->DeleteLocalRef(jpiece);
        
        n_predict--;
        
        batch = llama_batch_get_one(&new_token, 1);
        if (llama_decode(g_ctx, batch) != 0) {
            LOGE("Decode failed");
            break;
        }
    }
    
    g_is_generating = false;
    LOGD("Generation complete");
}

JNIEXPORT void JNICALL
Java_com_kaede_app_ai_LlamaCpp_nativeCancel(JNIEnv* env, jobject thiz) {
    g_should_cancel = true;
    LOGD("Cancel requested");
}

JNIEXPORT jboolean JNICALL
Java_com_kaede_app_ai_LlamaCpp_nativeIsGenerating(JNIEnv* env, jobject thiz) {
    return g_is_generating ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jstring JNICALL
Java_com_kaede_app_ai_LlamaCpp_nativeGetModelInfo(JNIEnv* env, jobject thiz) {
    if (!g_model) {
        return env->NewStringUTF("Model not loaded");
    }
    
    char info[256];
    snprintf(info, sizeof(info), "Model loaded | Context: %u", llama_n_ctx(g_ctx));
    return env->NewStringUTF(info);
}

JNIEXPORT void JNICALL
Java_com_kaede_app_ai_LlamaCpp_nativeRelease(JNIEnv* env, jobject thiz) {
    std::lock_guard<std::mutex> lock(g_mutex);
    
    LOGD("Releasing resources");
    
    if (g_ctx) { llama_free(g_ctx); g_ctx = nullptr; }
    if (g_model) { llama_free_model(g_model); g_model = nullptr; }
    
    llama_backend_free();
}

}
