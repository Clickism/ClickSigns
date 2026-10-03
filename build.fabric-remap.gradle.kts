plugins {
    id("java")
    id("net.fabricmc.fabric-loom-remap") version "1.18-SNAPSHOT"
    id("me.modmuss50.mod-publish-plugin") version "2.2.0"
}
val modVersion = property("mod.version").toString()
val minecraftVersion = stonecutter.current.project.substringBeforeLast('-')
val loader = stonecutter.current.project.substringAfterLast('-')

group = project.property("maven_group").toString()
version = "$modVersion+$minecraftVersion-$loader"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://maven.parchmentmc.org")
}

sourceSets {
    main {
        resources.srcDir(
            "${rootDir}/versions/datagen/$minecraftVersion/src/main/generated"
        )
        java {
            val platform = "de/clickism/clicksigns/platform"
            exclude("$platform/forge/**")
            exclude("$platform/neoforge/**")
        }
    }
}

java {
    if (sc.current.parsed >= "1.20.5") {
        toolchain.languageVersion.set(JavaLanguageVersion.of(21))
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    } else {
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

base {
    archivesName.set(property("archives_base_name").toString())
}

configurations.all {
    resolutionStrategy {
        cacheChangingModulesFor(0, "seconds")
    }
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    compileOnly("org.jspecify:jspecify:1.0.0") // For compat with newer versions

    // Mappings
    @Suppress("UnstableApiUsage")
    mappings(loom.layered() {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-${property("deps.parchment")}@zip")
    })

    // Fabric
    modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric_api")}")

    // Libraries
    implementation(include("de.clickism:modrinth-update-checker:1.1")!!)
    include(modImplementation("de.clickism:clickui:${property("deps.clickui")}+$minecraftVersion-$loader") {
        isChanging = true
        isTransitive = false
    })

    // Configured
    implementation(include("de.clickism:configured-core:${property("deps.configured")}")!!)
    implementation(include("de.clickism:configured-json:${property("deps.configured")}")!!)
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
        runDirectory.set(rootProject.file("runs/fabric"))
        if (runtimeEnvironment.get() == "client") {
            programArguments.set(listOf("--username=Clickism"))
        }
    }
}

tasks.register<Delete>("cleanLoomCache") {
    description = "Cleans the Loom cache for remapped mods"
    delete(rootProject.file(".gradle/loom-cache/remapped_mods"))
}

publishMods {
    displayName.set("ClickSigns ${property("mod.version")} for Fabric")
    file.set(tasks.remapJar.get().archiveFile)
    version.set(project.version.toString())
    changelog.set(rootProject.file("CHANGELOG.md").readText())
    type.set(BETA)
    modLoaders.add("fabric")
    val mcVersions = property("mod.publishing_target_minecraft_versions").toString().split(',')
    modrinth {
        accessToken.set(System.getenv("MODRINTH_TOKEN"))
        projectId.set("xaXWiLzT")
        requires("fabric-api")
        minecraftVersions.addAll(mcVersions)
        environment.set(CLIENT_AND_SERVER)
    }
    curseforge {
        accessToken.set(System.getenv("CURSEFORGE_TOKEN"))
        projectId.set("1161795")
        client.set(true)
        server.set(true)
        requires("fabric-api")
        minecraftVersions.addAll(mcVersions)
    }
    github {
        accessToken.set(System.getenv("GITHUB_TOKEN"))
        parent(project(":").tasks.named("publishGithub"))
    }
}