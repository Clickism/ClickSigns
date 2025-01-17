/*
 * Copyright 2020-2025 Clickism
 * Released under the GNU General Public License 3.0.
 * See LICENSE.md for details.
 */

package me.clickism.clicksigns.datagen;

//? if >=1.21.2 {
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.data.recipe.ShapedRecipeJsonBuilder;
import java.util.concurrent.CompletableFuture;

import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.*;
//?} elif >=1.20.5 {
/*import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeExporter;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.registry.*;
*///?} else {
/*import java.util.function.Consumer;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
*///?}

import me.clickism.clicksigns.ClickSigns;
import me.clickism.clicksigns.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.tag.ItemTags;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output
             //? if >=1.20.5
            , CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture
    ) {
        super(output
                //? if >=1.20.5
                , registriesFuture
        );
    }
    
    //? if >=1.21.2 {
    @Override
    protected RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup wrapperLookup, RecipeExporter recipeExporter) {
        return new RecipeGenerator(wrapperLookup, recipeExporter) {
            @Override
            public void generate() {
                ShapedRecipeJsonBuilder.create(Registries.ITEM, RecipeCategory.DECORATIONS, ModBlocks.ROAD_SIGN, 4)
                        .pattern("###")
                        .pattern("#*#")
                        .pattern("###")
                        .input('#', Items.IRON_INGOT)
                        .input('*', ingredientFromTag(ItemTags.SIGNS))
                        .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                        .offerTo(recipeExporter, RegistryKey.of(RegistryKeys.RECIPE, ClickSigns.identifier("road_sign")));
            }
        };
    }

    @Override
    public String getName() {
        return ClickSigns.identifier("recipes").toString();
    }
    //?} else {
    /*@Override
    public void generate(
            //? if >=1.20.5 {
            RecipeExporter recipeExporter
            //?} else {
            /^Consumer<RecipeJsonProvider> recipeExporter
            ^///?}
    ) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.DECORATIONS, ModBlocks.ROAD_SIGN, 4)
                .pattern("###")
                .pattern("#*#")
                .pattern("###")
                .input('#', Items.IRON_INGOT)
                .input('*', ItemTags.SIGNS)
                .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                .offerTo(recipeExporter, ClickSigns.identifier("road_sign"));
    }
    *///?}
}
