package me.clickism.clicksigns.datagen;

//? if >=1.20.5 {
import net.minecraft.data.server.recipe.RecipeExporter;
import java.util.concurrent.CompletableFuture;
import net.minecraft.registry.RegistryWrapper;
//?} else {

/*import java.util.function.Consumer;
import net.minecraft.data.server.recipe.RecipeJsonProvider;

*///?}

import me.clickism.clicksigns.ClickSigns;
import me.clickism.clicksigns.block.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
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

    @Override
    public void generate(
            //? if >=1.20.5 {
            RecipeExporter recipeExporter
            //?} else {
            /*Consumer<RecipeJsonProvider> recipeExporter
            *///?}
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
}
