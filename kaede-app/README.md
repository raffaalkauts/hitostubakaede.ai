# 🍁 Kaede - Android Offline Character AI

**Fully offline AI companion powered by local 1.5B LLM**

---

## 📋 Project Status

**Current Step:** Step 10 ✅ — Performance Optimization Complete (MVP COMPLETE!)

### Build Order Progress:
- [x] **Step 1:** Create Android Project Structure
- [x] **Step 2:** Setup Jetpack Compose UI
- [x] **Step 3:** Integrate llama.cpp Android Build
- [x] **Step 4:** Load Test Model & LLMEngine
- [x] **Step 5:** Implement Prompt Builder
- [x] **Step 6:** Add Memory Database (Room)
- [x] **Step 7:** Add Mood System
- [x] **Step 8:** Connect UI → AI
- [x] **Step 9:** Implement Streaming Output
- [x] **Step 10:** Performance Optimization

---

## 🏗️ Architecture

```
Android App (Kotlin + Jetpack Compose)
        ↓
Local AI Runtime Layer (llama.cpp JNI)
        ↓
1.5B Quantized Model (GGUF Q4_K_M)
        ↓
Local Memory Database (Room DB)
```

**NO CLOUD API. NO INTERNET REQUIRED.**

---

## 📁 Project Structure

```
kaede-app/
 ├── app/
 │   ├── src/main/
 │   │   ├── java/com/kaede/app/
 │   │   │   ├── ui/
 │   │   │   │   ├── chat/
 │   │   │   │   │   └── ChatScreen.kt
 │   │   │   │   └── theme/
 │   │   │   │       ├── Color.kt
 │   │   │   │       ├── Theme.kt
 │   │   │   │       └── Type.kt
 │   │   │   ├── ai/
 │   │   │   │   ├── LLMEngine.kt
 │   │   │   │   ├── PromptBuilder.kt
 │   │   │   │   └── PersonalityManager.kt
 │   │   │   ├── data/
 │   │   │   │   ├── db/
 │   │   │   │   │   ├── MemoryEntity.kt
 │   │   │   │   │   ├── MemoryDao.kt
 │   │   │   │   │   └── KaedeDatabase.kt
 │   │   │   │   └── memory/
 │   │   │   │       ├── MemoryRepository.kt
 │   │   │   │       └── MemoryManager.kt
 │   │   │   ├── viewmodel/
 │   │   │   │   └── ChatViewModel.kt
 │   │   │   └── MainActivity.kt
 │   │   ├── res/
 │   │   └── AndroidManifest.xml
 │   └── build.gradle.kts
 ├── model/
 │   └── README.md
 └── build.gradle.kts
```

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17
- Android SDK 34
- NDK r25c (for llama.cpp JNI)

### Build Instructions

1. **Open project** in Android Studio
2. **Sync Gradle** files
3. **Download model** (see model/README.md)
4. **Place model** in `model/kaede.gguf`
5. **Build APK**: `./gradlew assembleDebug`

---

## 🧠 Kaede Personality

Kaede is a refined, intelligent, elegant girl character with:

- Polite and well-mannered speech
- Academic excellence
- Confident but playful demeanor
- Emotional expressiveness
- Gentle teasing (respectful)

**No explicit content. No AI identity leakage.**

---

## 📊 Performance Targets

| Metric | Target |
|--------|--------|
| First token | < 2s |
| Token generation | 5-10 tok/s |
| RAM usage | < 2GB |
| APK size | < 1.2GB (with model) |
| Offline | 100% |

---

## 🔒 Safety Features

- Content filtering for explicit material
- No harmful content generation
- Conversation redirection for sensitive topics
- Age-appropriate interactions

---

## 📝 Next Steps

**Proceed to Step 2:** Setup Jetpack Compose UI components

Run the next instruction to continue implementation.

---

## 📄 License

This project is for educational purposes.

**Model licenses:** Follow original model licenses (TinyLlama, Qwen, Phi-2, etc.)

---

## 🎯 Success Criteria

- ✅ Runs in airplane mode
- ✅ Response time < 3 seconds
- ✅ Personality consistent
- ✅ Memory persists after restart
- ✅ No AI identity leakage

---

**Version:** 1.0.0  
**Last Updated:** Step 1 Complete
