plugins {
    kotlin("jvm") version "2.3.20"
    id("net.fabricmc.fabric-loom-remap") version "1.14-SNAPSHOT"
}
val modVersion = property("mod.version").toString()
val minecraftVersion = property("mod.minecraft_version").toString()
val loader = stonecutter.current.project.substringAfterLast('-')

group = project.property("maven_group").toString()
version = "$modVersion+$minecraftVersion-$loader"

repositories {
    mavenCentral()
}

sourceSets {
    main {
        resources.srcDir(
            "${rootDir}/versions/datagen/${sc.current.version.substringBeforeLast("-")}/src/main/generated"
        )
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
    dependsOn(tasks.named("stonecutterGenerate"))
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

fabricApi {
    configureDataGeneration {
        outputDirectory = rootProject.file("versions/datagen/${sc.current.version.substringBeforeLast("-")}/src/main/generated")
        client = true
    }
}

loom {
    runConfigs.all {
        ideConfigGenerated(true)
        runDir = "../../runs/fabric"
        if (environment == "client") {
            programArgs("--username=ClickToPlay")
        }
    }
}