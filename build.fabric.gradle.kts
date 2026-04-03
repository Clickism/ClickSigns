plugins {
    kotlin("jvm") version "2.3.20"
    id("net.fabricmc.fabric-loom-remap") version "1.14-SNAPSHOT"
}
val modVersion = property("mod.version").toString()
val minecraftVersion = stonecutter.current.project.substringBeforeLast('-')
val loader = stonecutter.current.project.substringAfterLast('-')

group = project.property("maven_group").toString()
version = "$modVersion+$minecraftVersion-$loader"

repositories {
    mavenCentral()
}

sourceSets {
    main {

    }
}

kotlin {
    jvmToolchain(17)
}

base {
    archivesName.set(property("archives_base_name").toString())
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric_api")}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${property("deps.fabric_kotlin")}")
}

tasks.processResources {
    val properties = mapOf(
        "mod_version" to modVersion,
        "minecraft_version" to minecraftVersion,
        "fabric_loader_version" to project.property("deps.fabric_loader")
    )

    filesMatching(listOf("fabric.mod.json")) {
        expand(properties)
    }
    inputs.properties(properties)
}

loom {
    runConfigs.all {
        ideConfigGenerated(true)
        runDir = "../../run"
        if (environment == "client") {
            programArgs("--username=ClickToPlay")
        }
    }
}