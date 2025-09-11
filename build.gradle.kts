// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {/*
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    //id("com.google.devtools.ksp") version "2.0.21-1.0.27" apply false
    //id("com.google.dagger.hilt.android") version "2.57.1" apply false
    //kotlin("jvm") version "2.2.10"
    //kotlin("plugin.serialization") version "2.2.10"
    id("com.google.devtools.ksp") version "1.9.22-1.0.17" apply false // ← ИЗМЕНИТЕ
    id("com.google.dagger.hilt.android") version "2.48" apply false     // ← ИЗМЕНИТЕ
    kotlin("jvm") version "1.9.22"                                     // ← ИЗМЕНИТЕ
    kotlin("plugin.serialization") version "1.9.22"                    // ← ИЗМЕНИТЕ
    */

        alias(libs.plugins.android.application) apply false
        alias(libs.plugins.kotlin.android) apply false
        alias(libs.plugins.kotlin.serialization) apply false
        alias(libs.plugins.kotlin.compose) apply false
        alias(libs.plugins.ksp) apply false
        alias(libs.plugins.hilt.android) apply false
}