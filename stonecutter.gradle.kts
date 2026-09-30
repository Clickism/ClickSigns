plugins {
    id("me.modmuss50.mod-publish-plugin") version "2.2.0"
}
stonecutter active "26.2-fabric"

stonecutter parameters {
    val loader = current.project.substringAfterLast('-')
    val loaders = listOf("fabric", "neoforge", "forge")
    constants.match(loader, loaders)

    // String replacements
    replacements {
        string(current.parsed < "1.21.11") {
            replace("Identifier", "ResourceLocation")
        }
        string(current.parsed < "26.1") {
            replace("GuiGraphicsExtractor", "GuiGraphics")
            replace("net.minecraft.client.renderer.rendertype.RenderType", "net.minecraft.client.renderer.RenderType")

            // Datagen
            replace("FabricPackOutput", "FabricDataOutput")
            replace("FabricBlockLootSubProvider", "FabricBlockLootTableProvider")
            replace("FabricTagsProvider", "FabricTagProvider")
            replace("BlockTagsProvider", "BlockTagProvider")
        }
        string {  }
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