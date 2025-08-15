pluginManagement {
	repositories {
		mavenCentral()
		gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev")
        maven("https://maven.minecraftforge.net")
	}
}

plugins {
	id("dev.kikugie.stonecutter") version "0.7.6"
}

rootProject.name = "ClickSigns"

stonecutter {
	kotlinController = true
	centralScript = "build.gradle.kts"
	create(rootProject) {
        fun version(version: String, vararg loaders: String) {
            loaders.forEach { vers("$version-$it", version) }
        }
        version("1.20.1", "fabric", "forge")
        version("1.21.1", "fabric")
        version("1.21.5", "fabric")
        version("1.21.8", "fabric")
		vcsVersion = "1.21.8-fabric"
	}
}