plugins {
    id("net.neoforged.moddev.legacyforge") version "2.0.141"
    kotlin("jvm") version "2.2.21"
    id("java")
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
//    sourceSets["main"].resources.srcDir("${rootDir}/versions/datagen/${stonecutter.current.version.split("-")[0]}/src/main/generated")
}

dependencies {
    annotationProcessor("org.spongepowered:mixin:0.8.5:processor")
    implementation("thedarkcolour:kotlinforforge:${property("deps.forge_kotlin")}")
}

//sourceSets.main {
//    kotlin.exclude("**/platfotm/fabric/**", "**/platform/neoforge/**")
//}

//mixin {
//    add(sourceSets.main.get(), "${property("mod.id")}.mixins.refmap.json")
//    config("${property("mod.id")}.mixins.json")
//}

kotlin {
    jvmToolchain(17)
}

sourceSets.configureEach {
    val dir = layout.buildDirectory.dir("sourcesSets/$name")
    output.setResourcesDir(dir)
    java.destinationDirectory.set(dir)
    kotlin.destinationDirectory.set(dir)
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