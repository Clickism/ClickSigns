plugins {
    id("dev.kikugie.stonecutter")
}
stonecutter active "1.21.8-fabric" /* [SC] DO NOT EDIT */

stonecutter parameters {
    constants {
        val loaders = listOf("fabric", "forge")
        match(node.metadata.project.substringAfterLast("-"), loaders)
    }
}

