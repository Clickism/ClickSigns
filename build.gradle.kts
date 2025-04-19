plugins {
    id("fabric-loom") version "1.10-SNAPSHOT"
    id("me.modmuss50.mod-publish-plugin") version "0.8.4"
}

version = "${property("mod.version")}+${stonecutter.current.version}"
group = project.property("maven_group").toString()

base {
    archivesName.set(property("archives_base_name").toString())
}

repositories {
    gradlePluginPortal()
}

fabricApi {
    configureDataGeneration {
        client = true
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${stonecutter.current.project}")
    mappings("net.fabricmc:yarn:${property("deps.yarn_mappings")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric_api")}")
}

tasks.processResources {
    val properties = mapOf(
        "version" to version,
        "targetVersion" to project.property("mod.mc_version"),
        "minecraftVersion" to stonecutter.current.version,
        "fabricVersion" to project.property("deps.fabric_loader")
    )
    filesMatching("fabric.mod.json") {
        expand(properties)
    }
    inputs.properties(properties)
}

java {
    val j21 = stonecutter.eval(stonecutter.current.version, ">=1.20.5")
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(if (j21) 21 else 17))
    }
    val javaVersion = if (j21) JavaVersion.VERSION_17 else JavaVersion.VERSION_17
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${project.base.archivesName.get()}" }
    }
}

publishMods {
    displayName.set("${rootProject.name} ${property("mod.version")} for Fabric")
    file.set(tasks.remapJar.get().archiveFile)
    version.set(project.version.toString())
    changelog.set(rootProject.file("CHANGELOG.md").readText())
    type.set(STABLE)
    modLoaders.add("fabric")
    val mcVersions = property("mod.target_mc_versions").toString().split(',')
    modrinth {
        accessToken.set(System.getenv("MODRINTH_TOKEN"))
        projectId.set("xaXWiLzT")
        requires("fabric-api")
        minecraftVersions.addAll(mcVersions)
    }
    curseforge {
        accessToken.set(System.getenv("CURSEFORGE_TOKEN"))
        projectId.set("1161795")
        clientRequired.set(true)
        serverRequired.set(true)
        requires("fabric-api")
        minecraftVersions.addAll(mcVersions)
    }
}

loom {
    runConfigs.all {
        ideConfigGenerated(true)
        runDir = "../../run"
    }
}