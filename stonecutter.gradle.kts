plugins {
    id("dev.kikugie.stonecutter")
}
stonecutter active "1.20.1-forge" /* [SC] DO NOT EDIT */

stonecutter parameters {
    constants {
        val loaders = listOf("fabric", "forge")
        match(node.metadata.project.substringAfterLast("-"), loaders)
    }
}

