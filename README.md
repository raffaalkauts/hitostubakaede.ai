# 🌌 AI Hitotsuba (Kaede AI)

AI Hitotsuba adalah proyek asisten AI berbasis LLM yang dioptimalkan untuk perangkat mobile dan desktop. Proyek ini menggabungkan kekuatan `llama.cpp`, `MLC-LLM`, dan `MediaPipe` untuk menghadirkan pengalaman asisten AI yang responsif secara lokal (On-Device AI).

## 🚀 Fitur Utama
- **On-Device LLM:** Menjalankan model AI langsung di perangkat tanpa koneksi internet.
- **Multi-Backend:** Mendukung `llama.cpp`, `MLC-LLM`, dan `Gemma Task`.
- **High Performance:** Optimasi menggunakan CUDA, Vulkan, dan Metal.
- **Cross-Platform:** Tersedia untuk Android dan Desktop.

## 📂 Struktur Project
- `kaede-app/`: Aplikasi utama Android (Kotlin/Java).
- `SmolChat-Android/`: Interface chat ringan untuk perangkat mobile.
- `llama-jni/`: Java Native Interface untuk integrasi llama.cpp.
- `llama.cpp-official/`: Backend core untuk inferensi model GGUF.

## 📥 Panduan Download Model (Setup)
Karena keterbatasan ukuran file di GitHub, model AI (.gguf, .bin, .task) tidak disertakan dalam repository ini. Kamu harus mendownloadnya secara manual:

### 1. Model GGUF (untuk llama.cpp)
Download model GGUF (misal: Gemma 2B atau Llama 3) dan letakkan di:
`kaede-app/app/src/main/assets/kaede.gguf`

### 2. Model MLC (untuk MLC-LLM)
Download parameter shard `.bin` dan letakkan di:
`kaede-app/app/src/main/assets/gemma-2b-it-q4f16_1-MLC/`

### 3. MediaPipe Task
Download model `.task` dan letakkan di:
`kaede-app/model/gemma3-1b-it-int4.task`

> **Note:** Kamu bisa menggunakan script `download-model.ps1` yang tersedia di folder `kaede-app` untuk membantu proses download otomatis.

## 🛠️ Cara Menjalankan
1. Clone repository ini.
2. Letakkan model AI di folder yang sesuai (lihat panduan di atas).
3. Buka proyek menggunakan **Android Studio** atau **VS Code**.
4. Build dan Run!

---
Dikembangkan oleh [raffaalkauts](https://github.com/raffaalkauts)
