plugins {
    id("java")
    id("net.neoforged.moddev.legacyforge") version "2.0.141"
}
val modVersion = property("mod.version").toString()
val minecraftVersion = property("mod.minecraft_version").toString()
val loader = stonecutter.current.project.substringAfterLast('-')

group = project.property("maven_group").toString()
version = "$modVersion+$minecraftVersion-$loader"

repositories {
    mavenCentral()
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