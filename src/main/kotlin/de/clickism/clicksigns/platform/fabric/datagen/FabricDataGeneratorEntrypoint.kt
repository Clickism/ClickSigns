package de.clickism.clicksigns.platform.fabric.datagen
//? if fabric {
/*import de.clickism.clicksigns.ClickSigns
import de.clickism.clicksigns.registry.ModBlocks
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.core.HolderLookup
import net.minecraft.data.models.BlockModelGenerators
import net.minecraft.data.models.ItemModelGenerators
import net.minecraft.data.models.model.ModelTemplates
import net.minecraft.data.recipes.FinishedRecipe
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.data.recipes.ShapedRecipeBuilder
import net.minecraft.tags.BlockTags
import net.minecraft.tags.ItemTags
import net.minecraft.world.item.Items
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

class FabricDataGeneratorEntrypoint : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(datagen: FabricDataGenerator) {
        val pack = datagen.createPack()
        pack.addProvider(::ModBlockTagProvider)
        pack.addProvider(::ModLootTableProvider)
        pack.addProvider(::ModModelProvider)
        pack.addProvider(::ModRecipeProvider)
    }
}

class ModBlockTagProvider(
    output: FabricDataOutput,
    registriesFuture: CompletableFuture<HolderLookup.Provider>
) : FabricTagProvider.BlockTagProvider(output, registriesFuture) {
    override fun addTags(lookup: HolderLookup.Provider) {
        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_PICKAXE)
            .add(ModBlocks.ROAD_SIGN.get())
    }
}

class ModLootTableProvider(
    output: FabricDataOutput,
) : FabricBlockLootTableProvider(output) {
    override fun generate() {
        dropSelf(ModBlocks.ROAD_SIGN.get())
    }
}

class ModModelProvider(output: FabricDataOutput) : FabricModelProvider(output) {
    override fun generateBlockStateModels(blockStateModelGenerator: BlockModelGenerators) {
        blockStateModelGenerator.createTrivialCube(ModBlocks.ROAD_SIGN.get())
    }

    override fun generateItemModels(itemModelGenerator: ItemModelGenerators) {
        itemModelGenerator.generateFlatItem(ModBlocks.ROAD_SIGN.get().asItem(), ModelTemplates.FLAT_ITEM)
    }
}

class ModRecipeProvider(output: FabricDataOutput) : FabricRecipeProvider(output) {
    override fun buildRecipes(exporter: Consumer<FinishedRecipe?>) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.ROAD_SIGN.get(), 4)
            .pattern("###")
            .pattern("#*#")
            .pattern("###")
            .define('#', Items.IRON_INGOT)
            .define('*', ItemTags.SIGNS)
            .unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
            .save(exporter, ClickSigns.identifier("road_sign"));
    }

    override fun getName(): String? {
        return ClickSigns.identifier("recipes").toString()
    }
}
*///?}