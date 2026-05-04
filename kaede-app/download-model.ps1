# Download Kaede Model Script
# Downloads TinyLlama 1.1B Chat Q4_K_M (~650MB)

$ErrorActionPreference = "Stop"

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$ModelDir = Join-Path $ScriptDir "model"
$AssetsDir = Join-Path $ScriptDir "app\src\main\assets"

Write-Host "=== Kaede Model Download ===" -ForegroundColor Cyan
Write-Host ""

# Create directories
Write-Host "Creating directories..."
if (-not (Test-Path $ModelDir)) {
    New-Item -ItemType Directory -Path $ModelDir | Out-Null
}
if (-not (Test-Path $AssetsDir)) {
    New-Item -ItemType Directory -Path $AssetsDir | Out-Null
}

# Model download URL (TinyLlama 1.1B Chat Q4_K_M)
$ModelUrl = "https://huggingface.co/TheBloke/TinyLlama-1.1B-Chat-v1.0-GGUF/resolve/main/tinyllama-1.1b-chat-v1.0.Q4_K_M.gguf"
$ModelFile = Join-Path $ModelDir "kaede.gguf"
$AssetsFile = Join-Path $AssetsDir "kaede.gguf"

Write-Host "Downloading TinyLlama 1.1B Chat Q4_K_M..."
Write-Host "Size: ~650MB (this may take 5-15 minutes depending on your connection)"
Write-Host ""

# Download with progress
$ProgressPreference = "SilentlyContinue"
try {
    Invoke-WebRequest -Uri $ModelUrl -OutFile $ModelFile -UseBasicParsing
    Write-Host "Download complete!" -ForegroundColor Green
    
    # Copy to assets folder
    Write-Host "Copying to assets folder..."
    Copy-Item $ModelFile $AssetsFile -Force
    Write-Host "Model ready at: $AssetsFile" -ForegroundColor Green
    
    # Show file size
    $FileSize = (Get-Item $ModelFile).Length / (1024 * 1024)
    Write-Host "Model size: $([math]::Round($FileSize, 2)) MB" -ForegroundColor Cyan
    
    Write-Host ""
    Write-Host "=== Model Setup Complete ===" -ForegroundColor Green
    Write-Host ""
    Write-Host "Next step: Run '.\gradlew.bat assembleDebug' to build the full AI APK"
    
} catch {
    Write-Host "Download failed: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host ""
    Write-Host "Manual download instructions:" -ForegroundColor Yellow
    Write-Host "1. Go to: https://huggingface.co/TheBloke/TinyLlama-1.1B-Chat-v1.0-GGUF"
    Write-Host "2. Download: tinyllama-1.1b-chat-v1.0.Q4_K_M.gguf"
    Write-Host "3. Rename to: kaede.gguf"
    Write-Host "4. Place in: model\kaede.gguf and app\src\main\assets\kaede.gguf"
    exit 1
}
