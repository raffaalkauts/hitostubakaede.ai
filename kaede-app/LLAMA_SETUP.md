# llama.cpp Integration Guide

## Setup Instructions

### Step 1: Clone llama.cpp

The llama.cpp library must be cloned as a git submodule or manually placed.

**Option A: Git Submodule (Recommended)**

```bash
cd kaede-app
git submodule add https://github.com/ggerganov/llama.cpp.git app/src/main/cpp/llama.cpp
git submodule update --init --recursive
```

**Option B: Manual Download**

1. Download llama.cpp from: https://github.com/ggerganov/llama.cpp
2. Extract to: `app/src/main/cpp/llama.cpp`

### Step 2: Verify llama.cpp Version

Recommended commit: `bXXXXXX` or later (check for Android NDK compatibility)

```bash
cd app/src/main/cpp/llama.cpp
git log -1
```

### Step 3: Download Quantized Model

Download a GGUF quantized model (Q4_K_M recommended):

**Recommended Models:**

| Model | Size | Download |
|-------|------|----------|
| TinyLlama 1.1B Chat | ~650MB | TheBloke/TinyLlama-1.1B-Chat-v1.0-GGUF |
| Qwen2 1.5B Instruct | ~900MB | Qwen/Qwen2-1.5B-Instruct-GGUF |
| Phi-2 | ~1.7GB | TheBloke/phi-2-GGUF |

**Download Steps:**

1. Go to HuggingFace
2. Search for the model + GGUF
3. Download the `Q4_K_M` quantization file
4. Rename to `kaede.gguf`
5. Place in `model/` directory

### Step 4: Configure Model in Gradle

Add model file to APK (optional - increases APK size):

```kotlin
// app/build.gradle.kts
android {
    sourceSets {
        getByName("main") {
            assets.srcDirs("src/main/assets", "../../model")
        }
    }
}
```

Or copy model to assets manually:

```bash
cp model/kaede.gguf app/src/main/assets/
```

### Step 5: Build Native Libraries

**Prerequisites:**
- Android NDK r25c or later
- CMake 3.22.1 or later
- Ninja build system

**Build Command:**

```bash
./gradlew assembleDebug
```

Native libraries will be built automatically during the build process.

### Step 6: Verify Build

Check that native libraries are included:

```bash
unzip -l app/build/outputs/apk/debug/app-debug.apk | grep "\.so"
```

Expected output:
```
lib/arm64-v8a/libkaede-llama-jni.so
lib/armeabi-v7a/libkaede-llama-jni.so
lib/x86_64/libkaede-llama-jni.so
```

---

## Troubleshooting

### Error: "llama.cpp not found"

Ensure llama.cpp is cloned to the correct directory:
```bash
ls app/src/main/cpp/llama.cpp
```

### Error: "NDK not found"

Install NDK via Android Studio SDK Manager:
1. Open SDK Manager
2. Go to SDK Tools tab
3. Check "NDK (Side by side)"
4. Install version 25.2.9519653 or later

### Error: "Model not loaded"

Check:
1. Model file exists in assets or cache directory
2. Model file is valid GGUF format
3. Sufficient storage space available

### Performance Issues

Try:
1. Reduce context size (default: 2048)
2. Use smaller model (TinyLlama 1.1B)
3. Reduce threads (default: 4)
4. Enable model memory mapping

---

## Build Configuration

### CMake Options

| Option | Default | Description |
|--------|---------|-------------|
| GGML_NATIVE | OFF | Disable CPU-specific optimizations |
| GGML_OPENMP | OFF | Disable OpenMP (not supported on Android) |
| GGML_METAL | OFF | Disable Metal (iOS only) |
| GGML_CUDA | OFF | Disable CUDA (desktop only) |
| BUILD_SHARED_LIBS | ON | Build shared libraries |

### NDK ABI Filters

Supported ABIs:
- `arm64-v8a` (64-bit ARM - most Android devices)
- `armeabi-v7a` (32-bit ARM - older devices)
- `x86_64` (64-bit x86 - emulators)

---

## File Structure

```
app/src/main/cpp/
├── CMakeLists.txt          # CMake build configuration
├── llama-jni.cpp           # JNI bridge code
└── llama.cpp/              # llama.cpp library (submodule)
    ├── include/
    ├── src/
    ├── ggml/
    └── ...
```

---

## Next Steps

After completing llama.cpp integration:
- Step 4: Load Test Model & LLMEngine
- Step 5: Implement Prompt Builder
- Step 6: Add Memory Database

---

**Version:** 1.0  
**Last Updated:** Step 3
