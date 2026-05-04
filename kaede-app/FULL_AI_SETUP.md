# 🚀 KAEDE FULL AI SETUP GUIDE

## ✅ What's Been Done

### Step 1: Clone llama.cpp ✅
```bash
git submodule add https://github.com/ggerganov/llama.cpp.git app/src/main/cpp/llama.cpp
```
**Status:** Complete - llama.cpp cloned to `app/src/main/cpp/llama.cpp`

### Step 2: Enable Native Build ✅
**Files Modified:**
- `app/build.gradle.kts` - Enabled NDK and CMake configuration
- `app/src/main/cpp/CMakeLists.txt` - Fixed paths for llama.cpp

**Status:** Complete - Native build configured

### Step 3: Download AI Model ⏳
**Script:** `download-model.ps1`

**Model:** TinyLlama 1.1B Chat Q4_K_M (~650MB)

**Status:** Downloading in background...

**Manual Download (if script fails):**
1. Go to: https://huggingface.co/TheBloke/TinyLlama-1.1B-Chat-v1.0-GGUF
2. Download: `tinyllama-1.1b-chat-v1.0.Q4_K_M.gguf`
3. Rename to: `kaede.gguf`
4. Place in:
   - `model/kaede.gguf`
   - `app/src/main/assets/kaede.gguf`

### Step 4: Restore Full LlamaCpp Implementation ✅
**File:** `app/src/main/java/com/kaede/app/ai/LlamaCpp.kt`

**Status:** Complete - Full native library integration restored

---

## 🔧 Build Instructions

### Wait for Model Download to Complete

The model download script is running in the background. Wait for:
```
=== Model Setup Complete ===
Model ready at: E:\ai hitotsuba\kaede-app\app\src\main\assets\kaede.gguf
```

### Build Full AI APK

Once the model is downloaded, run:

```bash
cd "E:\ai hitotsuba\kaede-app"
.\gradlew.bat assembleDebug --no-daemon
```

**Build Time:** 5-10 minutes (first build with llama.cpp)

**Expected APK Size:** ~700-800 MB (includes model)

**Output Location:**
```
E:\ai hitotsuba\kaede-app\app\build\outputs\apk\debug\app-debug.apk
```

---

## 📱 Install & Test

### Install on Android Device

1. **Enable Unknown Sources:**
   - Settings → Security → Unknown Sources (enable)
   - Or Settings → Apps → Chrome → Install unknown apps (enable)

2. **Transfer APK:**
   - Copy `app-debug.apk` to your device
   - Or use ADB: `adb install app/build/outputs/apk/debug/app-debug.apk`

3. **Install:**
   - Open APK file on device
   - Tap "Install"

4. **First Launch:**
   - App will load the model (~30-60 seconds)
   - You'll see progress bar
   - When complete: "Kaede Ready"

---

## 🎯 Expected Performance

| Metric | Target | Device Dependent |
|--------|--------|-----------------|
| Model Load Time | 30-60s | Storage speed |
| First Token | < 5s | CPU speed |
| Token Generation | 3-8 tok/s | CPU cores |
| RAM Usage | < 2GB | - |
| Storage | ~700MB | - |

---

## 🐛 Troubleshooting

### Build Fails with "llama.cpp not found"
```bash
# Verify llama.cpp exists
dir app\src\main\cpp\llama.cpp

# If missing, reclone:
git submodule update --init --recursive
```

### Build Fails with "NDK not found"
1. Open Android Studio
2. Tools → SDK Manager → SDK Tools
3. Install "NDK (Side by side)" 25.1.8937393
4. Retry build

### App Crashes on Launch
1. Check logcat: `adb logcat | findstr "Kaede"`
2. Verify model file exists in assets
3. Ensure device has ≥2GB free RAM

### "Model not loaded" Error
1. Check if `kaede.gguf` exists in `app/src/main/assets/`
2. Verify file size (~650MB)
3. Re-download if corrupted

---

## 📊 Full AI vs Stub Mode

| Feature | Stub Mode | Full AI |
|---------|-----------|---------|
| APK Size | ~15 MB | ~700 MB |
| Responses | Pre-scripted | Real AI generated |
| Offline | ✅ | ✅ |
| Personality | Fixed | Dynamic |
| Memory | Basic | Full context |
| Build Time | 1 min | 5-10 min |

---

## 🎉 Success Criteria

Your full AI build is working when:

- ✅ Model loads on first launch (progress bar shows)
- ✅ "Kaede Ready" status appears
- ✅ Responses are unique (not pre-scripted)
- ✅ Kaede remembers conversation context
- ✅ Mood changes based on conversation
- ✅ Works in airplane mode

---

## 📝 Next Steps After Build

1. **Test conversation** - Chat with Kaede
2. **Monitor performance** - Check tokens/sec in logcat
3. **Optimize settings** - Adjust threads/context in settings
4. **Add features** - TTS, voice input, etc.

---

**Last Updated:** During full AI setup  
**Build Target:** Android API 26+  
**Model:** TinyLlama 1.1B Chat Q4_K_M
