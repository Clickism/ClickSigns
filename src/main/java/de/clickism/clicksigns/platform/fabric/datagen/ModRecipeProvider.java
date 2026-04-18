package de.clickism.clicksigns.platform.fabric.datagen;

import de.clickism.clicksigns.ClickSigns;
import de.clickism.clicksigns.ClickSignsBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Recipe generator for the mod.
 */
class ModRecipeProvider extends FabricRecipeProvider {

    public ModRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void buildRecipes(Consumer<FinishedRecipe> exporter) {
        // Generate road sign recipe
        ShapedRecipeBuilder
                .shaped(RecipeCategory.DECORATIONS, ClickSignsBlocks.ROAD_SIGN.get(), 4)
                .pattern("###")
                .pattern("#*#")
                .pattern("###")
                .define('#', Items.IRON_INGOT)
                .define('*', ItemTags.SIGNS)
                .unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
                .save(exporter, ClickSigns.identifier("road_sign"));
    }

    @Override
    public @NotNull String getName() {
        return ClickSigns.identifier("recipes").toString();
    }
}
