# llama.cpp Submodule Setup Script (PowerShell)
# Run this script to initialize llama.cpp submodule

$ErrorActionPreference = "Stop"

$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$LlamaDir = Join-Path $ScriptDir "app\src\main\cpp\llama.cpp"

Write-Host "=== Kaede - llama.cpp Setup ===" -ForegroundColor Cyan
Write-Host ""

# Check if llama.cpp already exists
if (Test-Path $LlamaDir) {
    Write-Host "llama.cpp directory already exists at: $LlamaDir"
    Write-Host "Checking if it's a valid git repository..."
    
    if (Test-Path (Join-Path $LlamaDir ".git")) {
        Write-Host "llama.cpp is already initialized as git submodule"
        Write-Host "Updating submodule..."
        Set-Location $ScriptDir
        git submodule update --init --recursive
        Write-Host "Done!" -ForegroundColor Green
        exit 0
    } else {
        Write-Host "Warning: Directory exists but is not a git repository" -ForegroundColor Yellow
        Write-Host "Please remove the directory and run this script again"
        exit 1
    }
}

# Check if git is available
try {
    $gitVersion = git --version
    Write-Host "Git found: $gitVersion"
} catch {
    Write-Host "Error: git is not installed or not in PATH" -ForegroundColor Red
    Write-Host "Please install Git for Windows and try again"
    Write-Host "Download from: https://git-scm.com/download/win"
    exit 1
}

# Initialize git repository if not already
if (-not (Test-Path (Join-Path $ScriptDir ".git"))) {
    Write-Host "Initializing git repository..."
    Set-Location $ScriptDir
    git init
}

# Add llama.cpp as submodule
Write-Host "Cloning llama.cpp repository..." -ForegroundColor Cyan
Write-Host "This may take a few minutes..."
Set-Location $ScriptDir
git submodule add https://github.com/ggerganov/llama.cpp.git app/src/main/cpp/llama.cpp

# Update submodules
Write-Host "Initializing submodules..."
git submodule update --init --recursive

# Verify installation
if ((Test-Path $LlamaDir) -and (Test-Path (Join-Path $LlamaDir "CMakeLists.txt"))) {
    Write-Host ""
    Write-Host "=== Setup Complete ===" -ForegroundColor Green
    Write-Host "llama.cpp successfully cloned to: $LlamaDir"
    Write-Host ""
    Write-Host "Next steps:" -ForegroundColor Cyan
    Write-Host "1. Download a GGUF model (see LLAMA_SETUP.md)"
    Write-Host "2. Place model in model\kaede.gguf"
    Write-Host "3. Build the project: .\gradlew assembleDebug"
} else {
    Write-Host ""
    Write-Host "Error: Setup failed - llama.cpp directory not properly created" -ForegroundColor Red
    exit 1
}
