# Kaede App CMake Configuration
# Minimal configuration - MLC-LLM integration pending

cmake_minimum_required(VERSION 3.22.1)
project("kaede" VERSION "1.0" LANGUAGES C CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_POSITION_INDEPENDENT_CODE ON)

message(STATUS "Kaede CMake configured")
message(STATUS "Note: MLC-LLM native library will be loaded dynamically if available")

# Placeholder library (does nothing but prevents build errors)
add_library(kaede-mlc-jni SHARED
    mlc-jni.cpp
)

target_link_libraries(kaede-mlc-jni PRIVATE
    android
    log
)

message(STATUS "Kaede native library configured (Smart Stub mode)")
