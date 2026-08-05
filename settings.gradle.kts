pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.neoforged.net/releases/")
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.9"
}

rootProject.name = "ClickSigns"

stonecutter {
    create(rootProject) {
        fun version(version: String, vararg loaders: String) {
            loaders.forEach {
                if (stonecutter.eval(version, "<=1.21.1")) {
                    this.version("$version-$it", version)
                        .buildscript = "build.$it.gradle.kts"
                } else {
                    this.version("$version-$it", version)
                        .buildscript = "build.$it-modern.gradle.kts"
                }
            }
        }
        version("1.20.1", "fabric", "forge")
        version("1.21.1", "fabric", "neoforge")
        vcsVersion = "1.21.1-fabric"
    }
}