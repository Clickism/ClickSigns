plugins {
    id("java")
    id("net.neoforged.moddev") version "2.0.147"
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
}

neoForge {
    version = "${property("deps.neoforge")}"

    runs {
        register("client") {
            client()
            gameDirectory = rootProject.file("runs/neoforge")
            ideName = "Neoforge Client (${stonecutter.active?.version})"
            programArgument("--username=ClickToPlay")
        }
        register("server") {
            server()
            gameDirectory = rootProject.file("runs/neoforge")
            ideName = "Neoforge Server (${stonecutter.active?.version})"
        }
    }

    mods {
        register(property("mod.id").toString()) {
            sourceSet(sourceSets["main"])
        }
    }
}


dependencies {
    annotationProcessor("org.spongepowered:mixin:0.8.5:processor")

    // Dependencies
    jarJar(implementation("de.clickism:modrinth-update-checker:1.1")!!)
    jarJar(implementation("de.clickism:clickui:${property("deps.clickui")}+$minecraftVersion-forge") {
        isChanging = true
    })

    // Configured
    jarJar(implementation("de.clickism:configured-core:${property("deps.configured")}")!!)
    jarJar(implementation("de.clickism:configured-json:${property("deps.configured")}")!!)
}

sourceSets {
    main {
        resources.srcDir(
            "${rootDir}/versions/datagen/${sc.current.version.substringBeforeLast("-")}/src/main/generated"
        )
    }
}

//mixin {
//    add(sourceSets.main.get(), "${property("mod.id")}.mixins.refmap.json")
//    config("${property("mod.id")}.mixins.json")
//}

java {
    // Check minecraft version
    if (sc.current.parsed >= "26.1") {
        toolchain.languageVersion.set(JavaLanguageVersion.of(25))
        sourceCompatibility = JavaVersion.VERSION_25
        targetCompatibility = JavaVersion.VERSION_25
    } else {
        toolchain.languageVersion.set(JavaLanguageVersion.of(21))
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

base {
    archivesName.set(property("archives_base_name").toString())
}

tasks.processResources {
    val properties = mapOf(
        "mod_version" to modVersion,
        "minecraft_version" to minecraftVersion,
    )
    filesMatching(listOf("META-INF/neoforge.mods.toml", "META-INF/mods.toml")) {
        expand(properties)
    }
    inputs.properties(properties)
}

tasks.named("createMinecraftArtifacts") {
    dependsOn(tasks.named("stonecutterGenerate"))
}

publishMods {
    displayName.set("ClickSigns ${property("mod.version")} for NeoForge")
    file.set(tasks.getByName<Jar>("jar").archiveFile)
    version.set(project.version.toString())
    changelog.set(rootProject.file("CHANGELOG.md").readText())
    type.set(BETA)
    modLoaders.add("neoforge")
    val mcVersions = property("mod.publishing_target_minecraft_versions").toString().split(',')
    modrinth {
        accessToken.set(System.getenv("MODRINTH_TOKEN"))
        projectId.set("xaXWiLzT")
        minecraftVersions.addAll(mcVersions)
        environment.set(CLIENT_AND_SERVER)
    }
    curseforge {
        accessToken.set(System.getenv("CURSEFORGE_TOKEN"))
        projectId.set("1161795")
        client.set(true)
        server.set(true)
        minecraftVersions.addAll(mcVersions)
    }
    github {
        accessToken.set(System.getenv("GITHUB_TOKEN"))
        parent(project(":").tasks.named("publishGithub"))
    }
}