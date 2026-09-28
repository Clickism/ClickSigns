plugins {
    id("me.modmuss50.mod-publish-plugin") version "2.2.0"
}
stonecutter active "1.21.1-fabric"

stonecutter parameters {
    val loader = current.project.substringAfterLast('-')
    val loaders = listOf("fabric", "neoforge", "forge")
    constants.match(loader, loaders)
    // Disable platform-specific packages for other loaders
    files {
        loaders.forEach {
            disable(loader != it, "java/**/platform/$it/**")
        }
    }
}

val modVersion = property("mod.version").toString()

publishMods {
    version = project.version.toString()
    displayName = property("mod.version").toString()
    type = STABLE
    changelog = rootProject.file("CHANGELOG.md").readText()
    github {
        accessToken = System.getenv("GITHUB_TOKEN")

        repository = "Clickism/ClickSigns"
        tagName = property("mod.version").toString()
        commitish = "master"
        allowEmptyFiles = true
    }
}