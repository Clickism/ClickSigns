plugins {
    id("me.modmuss50.mod-publish-plugin") version "2.2.0"
}
stonecutter active "1.20.1-fabric"

stonecutter parameters {
    constants.match(
        node.metadata.project.substringAfterLast('-'),
        "fabric", "neoforge", "forge"
    )
    files {
        val platform = "java/de/clickism/clicksigns/platform"
        disable(!current.project.endsWith("-fabric"), "$platform/fabric/**")
        disable(!current.project.endsWith("-neoforge"), "$platform/neoforge/**")
        disable(!current.project.endsWith("-forge"), "$platform/forge/**")
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