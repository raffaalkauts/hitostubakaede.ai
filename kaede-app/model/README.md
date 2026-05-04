# Model Directory

Place your GGUF model file here.

**Recommended models:**
- TinyLlama 1.1B Chat (Q4_K_M)
- Qwen2 1.5B Instruct (Q4_K_M)
- Phi-2 (Q4_K_M)

**File naming:** `kaede.gguf`

**Model requirements:**
- Format: GGUF
- Quantization: Q4_K_M (4-bit)
- Context: 2048 tokens
- Size: ~1-1.5GB

---

**Download sources:**
- HuggingFace
- TheBloke's quantized models

**After downloading:**
1. Download the `.gguf` file
2. Rename to `kaede.gguf`
3. Place in this directory
4. Configure Gradle to include in APK (see Step 3)
