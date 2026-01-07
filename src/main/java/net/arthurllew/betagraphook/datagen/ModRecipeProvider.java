package net.arthurllew.betagraphook.datagen;

import net.arthurllew.betagraphook.registry.BetaGrapplingHookItems;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.CompletableFuture;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        // Grapnel head
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BetaGrapplingHookItems.GRAPNEL_HEAD.get())
                .pattern("NIN")
                .pattern(" I ")
                .pattern(" N ")
                .define('I', Items.IRON_INGOT)
                .define('N', Items.IRON_NUGGET)
                .unlockedBy(getHasName(Items.IRON_INGOT), has(Items.IRON_INGOT))
                .save(recipeOutput);

        // Grappling hook
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, BetaGrapplingHookItems.GRAPPLING_HOOK.get())
                .pattern("SSS")
                .pattern("SGS")
                .pattern("SSS")
                .define('G', BetaGrapplingHookItems.GRAPNEL_HEAD.get())
                .define('S', Items.STRING)
                .unlockedBy(getHasName(BetaGrapplingHookItems.GRAPNEL_HEAD.get()),
                        has(BetaGrapplingHookItems.GRAPNEL_HEAD.get()))
                .save(recipeOutput);
    }
}
