plugins {
    id("java")
    id("net.fabricmc.fabric-loom-remap") version "1.17-SNAPSHOT"
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
        java {
            val platform = "de/clickism/clicksigns/platform"
            exclude("$platform/forge/**")
            exclude("$platform/neoforge/**")
        }
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

base {
    archivesName.set(property("archives_base_name").toString())
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric_api")}")
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
        val currentVersion = sc.current.version.substringBeforeLast("-")
        outputDirectory = rootProject.file("versions/datagen/$currentVersion/src/main/generated")
        client = true
    }
}

loom {
    runConfigs.all {
        generateRunConfig.set(true)
        runDirectory.set(project.file("runs/fabric"))
        if (runtimeEnvironment.get() == "client") {
            programArguments.set(listOf("--username=ClickToPlay"))
        }
    }
}