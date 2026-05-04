# Download Gemma-3 270M (SMALLER - 200MB)
# Faster download, still REAL AI!

$ErrorActionPreference = "Stop"

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$ModelDir = Join-Path $ScriptDir "model"
$AssetsDir = Join-Path $ScriptDir "app\src\main\assets"

Write-Host "=== Downloading Gemma-3 270M (Compact Model) ===" -ForegroundColor Cyan
Write-Host ""

# Create directories
if (-not (Test-Path $ModelDir)) { New-Item -ItemType Directory -Path $ModelDir | Out-Null }
if (-not (Test-Path $AssetsDir)) { New-Item -ItemType Directory -Path $AssetsDir | Out-Null }

# Gemma-3 270M - SMALLER and FASTER
$ModelUrl = "https://huggingface.co/google/gemma-3-270m-it/resolve/main/gemma-3-270m-it-int4.task"
$ModelFile = Join-Path $ModelDir "gemma-3-270m-it.task"
$AssetsFile = Join-Path $AssetsDir "gemma-3-270m-it.task"

Write-Host "Downloading Gemma-3 270M (270M parameters)..."
Write-Host "Size: ~200MB (FAST download - 2-5 minutes)"
Write-Host ""

$ProgressPreference = "SilentlyContinue"
try {
    Invoke-WebRequest -Uri $ModelUrl -OutFile $ModelFile -UseBasicParsing
    Write-Host "✓ Download complete!" -ForegroundColor Green
    
    Copy-Item $ModelFile $AssetsFile -Force
    Write-Host "✓ Model ready!" -ForegroundColor Green
    
    $FileSize = (Get-Item $ModelFile).Length / (1024 * 1024)
    Write-Host "Model size: $([math]::Round($FileSize, 2)) MB" -ForegroundColor Cyan
    
    Write-Host ""
    Write-Host "=== READY TO BUILD ===" -ForegroundColor Green
    Write-Host "Run: .\gradlew.bat assembleDebug"
    
} catch {
    Write-Host "Download failed: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}
