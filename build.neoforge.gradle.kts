plugins {
    id("net.neoforged.moddev") version "2.0.141"
    id("java")
}
val modVersion = property("mod.version").toString()

group = project.property("maven_group").toString()
version = "${modVersion}+${stonecutter.current.project}"

repositories {
    mavenCentral()
    maven("https://thedarkcolour.github.io/KotlinForForge/")
}

dependencies {
    implementation("thedarkcolour:kotlinforforge-neoforge:${property("deps.forge_kotlin")}")
}

neoForge {
    version = property("deps.neoforge").toString()

    runs {
        register("client") {
            client()
            gameDirectory = file("run/")
            ideName = "NeoForge Client (${stonecutter.active?.version})"
            programArgument("--username=ClickToPlay")
        }
        register("server") {
            server()
            gameDirectory = file("run/")
            ideName = "NeoForge Server (${stonecutter.active?.version})"
        }
    }

    mods {
        register(property("mod.id").toString()) {
            sourceSet(sourceSets["main"])
        }
    }
}

sourceSets {
    main {
        resources.srcDir(
            "${rootDir}/versions/datagen/${sc.current.version.substringBeforeLast("-")}/src/main/generated"
        )
        java {
            val platform = "de/clickism/clicksigns/platform"
            exclude("$platform/fabric/**")
            exclude("$platform/forge/**")
        }
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

base {
    archivesName.set(property("archives_base_name").toString())
}

tasks.processResources {
    val properties = mapOf(
        "mod_version" to modVersion,
        "minecraft_version" to project.property("mod.minecraft_version"),
    )
    filesMatching(listOf("META-INF/neoforge.mods.toml", "META-INF/mods.toml")) {
        expand(properties)
    }
    inputs.properties(properties)
}