plugins {
    id("java")
    id("net.fabricmc.fabric-loom-remap") version "1.17-SNAPSHOT"
    id("me.modmuss50.mod-publish-plugin") version "2.2.0"
}
val modVersion = property("mod.version").toString()
val minecraftVersion = property("mod.minecraft_version").toString()
val loader = stonecutter.current.project.substringAfterLast('-')

group = project.property("maven_group").toString()
version = "$modVersion+$minecraftVersion-$loader"

repositories {
    mavenCentral()
    mavenLocal()
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

configurations.all {
    resolutionStrategy {
        cacheChangingModulesFor(0, "seconds")
    }
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric_api")}")
    implementation(include("de.clickism:modrinth-update-checker:1.1")!!)
    include(modImplementation("de.clickism:clickui:${property("deps.clickui")}+$minecraftVersion-fabric") {
        isChanging = true
        isTransitive = false
    })
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
            programArguments.set(listOf("--username=ClickToPlay"))
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