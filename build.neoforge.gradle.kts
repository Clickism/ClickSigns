plugins {
    id("java")
    id("net.neoforged.moddev") version "2.0.147"
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

dependencies {

    fun jarJarAndRuntime(dependencyNotation: Any) {
        jarJar(implementation(dependencyNotation)!!)
        add("additionalRuntimeClasspath", dependencyNotation)
    }

    implementation("thedarkcolour:kotlinforforge-neoforge:${property("deps.forge_kotlin")}")
    jarJarAndRuntime("de.clickism:modrinth-update-checker:1.1")
    jarJar(implementation("de.clickism:clickui:${property("deps.clickui")}+$minecraftVersion-neoforge") {
        isChanging = true
    })
    jarJarAndRuntime("de.clickism:configured-core:${property("deps.configured")}")!!
    jarJarAndRuntime("de.clickism:configured-json:${property("deps.configured")}")!!

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

