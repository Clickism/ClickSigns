plugins {
    id("me.modmuss50.mod-publish-plugin") version "0.8.4"
    id("dev.architectury.loom") version "1.10-SNAPSHOT"
    id("com.gradleup.shadow") version "8.3.5"
}

version = "${property("mod.version")}+${stonecutter.current.version}"
val forgeVersion = "${property("mod.version")}-${stonecutter.current.version}"
group = project.property("maven_group").toString()

val minecraft = stonecutter.current.project.substringBeforeLast('-')
val loader = stonecutter.current.project.substringAfterLast('-')

val generatedResources = file("src/generated")

val shadowDependency: Configuration by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

base {
    archivesName.set(property("archives_base_name").toString() + "-${loader}")
}

sourceSets {
    main {
        resources.srcDir(generatedResources)
        runtimeClasspath += shadowDependency
    }
}

repositories {
    gradlePluginPortal()
    mavenCentral()
    maven("https://maven.neoforged.net/releases/")
    maven("https://maven.minecraftforge.net")
    maven("https://maven.su5ed.dev/releases")
}

fabricApi {
    configureDataGeneration {
        client = true
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${minecraft}")
    mappings("net.fabricmc:yarn:${property("deps.yarn_mappings")}:v2")

    val configuredVersion = "0.2.4"

    when (loader) {
        "fabric" -> {
            modImplementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric_api")}")
            modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
            implementation(include("de.clickism:modrinth-update-checker:1.0")!!)
            implementation(include("de.clickism:configured-core:${configuredVersion}")!!)
            implementation(include("de.clickism:configured-json:${configuredVersion}")!!)
        }

        "forge" -> {
            "forge"("net.minecraftforge:forge:$minecraft-${property("deps.forge_loader")}")
            "io.github.llamalad7:mixinextras-common:${property("deps.mixin_extras")}".let {
                annotationProcessor(it)
                compileOnly(it)
            }
            "io.github.llamalad7:mixinextras-forge:${property("deps.mixin_extras")}".let {
                implementation(it)
                include(it)
            }
            modImplementation(include("dev.su5ed.sinytra.fabric-api:fabric-api:${property("deps.forgified_fabric_api")}")!!)
            listOf(
                "de.clickism:modrinth-update-checker:1.0",
                "de.clickism:configured-core:${configuredVersion}",
                "de.clickism:configured-json:${configuredVersion}"
            ).forEach {
                implementation(it)
                shadowDependency(it)
                runtimeOnly(it)
            }
        }
    }
}

tasks.processResources {
    val properties = mapOf(
        "version" to version,
        "forgeVersion" to forgeVersion,
        "targetVersion" to project.property("mod.mc_version"),
        "minecraftVersion" to stonecutter.current.version,
        "fabricVersion" to project.property("deps.fabric_loader")
    )
    filesMatching("fabric.mod.json") {
        expand(properties)
    }
    filesMatching("META-INF/*mods.toml") {
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

tasks.shadowJar {
    archiveClassifier.set("dev")
    configurations = listOf(shadowDependency)

    when (loader) {
        "forge" -> {
            mergeServiceFiles()
            isEnableRelocation = true
            relocationPrefix = "me.clickism.clicksigns.shadow"
            // Exclude Gson
            dependencies {
                exclude(dependency("com.google.code.gson:gson"))
            }
            // Stop Gson and Snakeyaml from being relocated
            relocate("com.google.gson", "com.google.gson")
        }
    }
}

if (loader == "forge") {
    tasks.remapJar {
        inputFile.set(tasks.shadowJar.get().archiveFile)
        dependsOn(tasks.shadowJar)
    }
}

publishMods {
    val loaderName = if (loader == "fabric") "Fabric" else "Forge"
    displayName.set("${rootProject.name} ${property("mod.version")} for $loaderName")
    file.set(tasks.remapJar.get().archiveFile)
    version.set(project.version.toString())
    changelog.set(rootProject.file("CHANGELOG.md").readText())
    type.set(STABLE)
    modLoaders.add(loader)
    val mcVersions = property("mod.target_mc_versions").toString().split(',')
    modrinth {
        accessToken.set(System.getenv("MODRINTH_TOKEN"))
        projectId.set("xaXWiLzT")
        when (loader) {
            "fabric" -> {
                requires("fabric-api")
            }

            "forge" -> {
                requires("forgified-fabric-api")
            }
        }
        minecraftVersions.addAll(mcVersions)
    }
    curseforge {
        accessToken.set(System.getenv("CURSEFORGE_TOKEN"))
        projectId.set("1161795")
        clientRequired.set(true)
        serverRequired.set(true)
        when (loader) {
            "fabric" -> {
                requires("fabric-api")
            }

            "forge" -> {
                requires("forgified-fabric-api")
            }
        }
        minecraftVersions.addAll(mcVersions)
    }
}

loom {
    if (stonecutter.eval(stonecutter.current.version, "<1.21.5")) {
        accessWidenerPath = rootProject.file("src/main/resources/clicksigns.accesswidener")
    }
    if (stonecutter.eval(stonecutter.current.version, "forge")) {
        forge.convertAccessWideners = true
    }

    runConfigs.all {
        ideConfigGenerated(true)
        runDir = "../../run"
    }
}