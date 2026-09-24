plugins {
    id("java")
    id("net.neoforged.moddev.legacyforge") version "2.0.147"
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
    maven("https://thedarkcolour.github.io/KotlinForForge/")
}

legacyForge {
    version = "${property("mod.minecraft_version")}-${property("deps.forge")}"

    runs {
        register("client") {
            client()
            gameDirectory = rootProject.file("runs/forge")
            ideName = "Forge Client (${stonecutter.active?.version})"
            programArgument("--username=ClickToPlay")
        }
        register("server") {
            server()
            gameDirectory = rootProject.file("runs/forge")
            ideName = "Forge Server (${stonecutter.active?.version})"
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
    jarJar(modImplementation("de.clickism:clickui:${property("deps.clickui")}+$minecraftVersion-forge") {
        isChanging = true
    })
}

sourceSets {
    main {
        resources.srcDir(
            "${rootDir}/versions/datagen/${sc.current.version.substringBeforeLast("-")}/src/main/generated"
        )
        java {
            val platform = "de/clickism/clicksigns/platform"
            exclude("$platform/fabric/**")
            exclude("$platform/neoforge/**")
        }
    }
}

//mixin {
//    add(sourceSets.main.get(), "${property("mod.id")}.mixins.refmap.json")
//    config("${property("mod.id")}.mixins.json")
//}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
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

tasks.jar {
    finalizedBy("reobfJar")
}

publishMods {
    displayName.set("ClickSigns ${property("mod.version")} for Forge")
    file.set(tasks.getByName<Jar>("reobfJar").archiveFile)
    version.set(project.version.toString())
    changelog.set(rootProject.file("CHANGELOG.md").readText())
    type.set(BETA)
    modLoaders.add("forge")
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