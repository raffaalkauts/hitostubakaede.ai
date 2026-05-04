#!/bin/bash

# llama.cpp Submodule Setup Script
# Run this script to initialize llama.cpp submodule

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
LLAMA_DIR="$SCRIPT_DIR/app/src/main/cpp/llama.cpp"

echo "=== Kaede - llama.cpp Setup ==="
echo ""

# Check if llama.cpp already exists
if [ -d "$LLAMA_DIR" ]; then
    echo "llama.cpp directory already exists at: $LLAMA_DIR"
    echo "Checking if it's a valid git repository..."
    
    if [ -d "$LLAMA_DIR/.git" ]; then
        echo "llama.cpp is already initialized as git submodule"
        echo "Updating submodule..."
        cd "$SCRIPT_DIR"
        git submodule update --init --recursive
        echo "Done!"
        exit 0
    else
        echo "Warning: Directory exists but is not a git repository"
        echo "Please remove the directory and run this script again"
        exit 1
    fi
fi

# Check if git is available
if ! command -v git &> /dev/null; then
    echo "Error: git is not installed or not in PATH"
    echo "Please install git and try again"
    exit 1
fi

# Initialize git repository if not already
if [ ! -d "$SCRIPT_DIR/.git" ]; then
    echo "Initializing git repository..."
    cd "$SCRIPT_DIR"
    git init
fi

# Add llama.cpp as submodule
echo "Cloning llama.cpp repository..."
echo "This may take a few minutes..."
cd "$SCRIPT_DIR"
git submodule add https://github.com/ggerganov/llama.cpp.git app/src/main/cpp/llama.cpp

# Update submodules
echo "Initializing submodules..."
git submodule update --init --recursive

# Verify installation
if [ -d "$LLAMA_DIR" ] && [ -f "$LLAMA_DIR/CMakeLists.txt" ]; then
    echo ""
    echo "=== Setup Complete ==="
    echo "llama.cpp successfully cloned to: $LLAMA_DIR"
    echo ""
    echo "Next steps:"
    echo "1. Download a GGUF model (see LLAMA_SETUP.md)"
    echo "2. Place model in model/kaede.gguf"
    echo "3. Build the project: ./gradlew assembleDebug"
else
    echo ""
    echo "Error: Setup failed - llama.cpp directory not properly created"
    exit 1
fi
