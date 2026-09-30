pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.kikugie.dev/snapshots")
        maven("https://maven.neoforged.net/releases/")
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.10-alpha.11"
}

rootProject.name = "ClickSigns"

stonecutter {
    create(rootProject) {
        fun version(version: String, vararg loaders: String) {
            loaders.forEach {
                var loader = it.substringBeforeLast('-')
                this.version("$version-$loader", version)
                    .buildscript = "build.$it.gradle.kts"
            }
        }
        version("1.20.1", "fabric-remap", "forge")
        version("1.21.1", "fabric-remap", "neoforge")
        // TODO: 1.21.11?
        version("26.1", "fabric", "neoforge")
        version("26.2", "fabric", "neoforge")
        version("26.3", "fabric", "neoforge")
        vcsVersion = "26.2-fabric"
    }
}