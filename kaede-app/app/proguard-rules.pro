# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in ${sdk.dir}/tools/proguard/proguard-android.txt

# Keep llama.cpp native methods
-keepclassmembers class com.kaede.ai.** {
    native <methods>;
}

# Keep model files
-keepclassmembers class com.kaede.data.** {
    *;
}
