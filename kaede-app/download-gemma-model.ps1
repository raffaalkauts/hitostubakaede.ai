# Download Gemma-3N Model for MediaPipe LLM
# Direct download from HuggingFace

$ErrorActionPreference = "Stop"

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$ModelDir = Join-Path $ScriptDir "model"
$AssetsDir = Join-Path $ScriptDir "app\src\main\assets"

Write-Host "=== Downloading Gemma-3N for MediaPipe LLM ===" -ForegroundColor Cyan
Write-Host ""

# Create directories
Write-Host "Creating directories..."
if (-not (Test-Path $ModelDir)) {
    New-Item -ItemType Directory -Path $ModelDir | Out-Null
}
if (-not (Test-Path $AssetsDir)) {
    New-Item -ItemType Directory -Path $AssetsDir | Out-Null
}

# Gemma-3N E2B (2B multimodal) - Direct download link
$ModelUrl = "https://huggingface.co/google/gemma-3n-e2b-it-litert-lm/resolve/main/gemma-3n-e2b-it-litertlm.task"
$ModelFile = Join-Path $ModelDir "gemma-3n-e2b-it.task"
$AssetsFile = Join-Path $AssetsDir "gemma-3n-e2b-it.task"

Write-Host "Downloading Gemma-3N E2B (2B parameters)..."
Write-Host "Model: google/gemma-3n-e2b-it-litert-lm"
Write-Host "Size: ~1.5GB (this may take 10-30 minutes)"
Write-Host "URL: $ModelUrl"
Write-Host ""

# Download with progress
$ProgressPreference = "SilentlyContinue"
try {
    Invoke-WebRequest -Uri $ModelUrl -OutFile $ModelFile -UseBasicParsing
    Write-Host "✓ Download complete!" -ForegroundColor Green
    
    # Copy to assets folder
    Write-Host "Copying to assets folder..."
    Copy-Item $ModelFile $AssetsFile -Force
    Write-Host "✓ Model ready at: $AssetsFile" -ForegroundColor Green
    
    # Show file size
    $FileSize = (Get-Item $ModelFile).Length / (1024 * 1024)
    Write-Host "Model size: $([math]::Round($FileSize, 2)) MB" -ForegroundColor Cyan
    
    Write-Host ""
    Write-Host "=== Model Setup Complete ===" -ForegroundColor Green
    Write-Host ""
    Write-Host "Next step: Run '.\gradlew.bat assembleDebug' to build the APK"
    Write-Host ""
    
} catch {
    Write-Host "Download failed: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host ""
    Write-Host "Manual download:" -ForegroundColor Yellow
    Write-Host "1. Go to: https://huggingface.co/google/gemma-3n-e2b-it-litert-lm"
    Write-Host "2. Download: gemma-3n-e2b-it-litertlm.task"
    Write-Host "3. Rename to: gemma-3n-e2b-it.task"
    Write-Host "4. Place in: model\ AND app\src\main\assets\"
    exit 1
}
