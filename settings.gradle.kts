pluginManagement {
	repositories {
		maven {
			name = "Fabric"
			url = uri("https://maven.fabricmc.net/")
		}
		mavenCentral()
		gradlePluginPortal()
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
		versions("1.20.1", "1.21.1", "1.21.5", "1.21.8")
		vcsVersion = "1.21.8"
	}
}