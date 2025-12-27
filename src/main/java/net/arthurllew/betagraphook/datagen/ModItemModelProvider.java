package net.arthurllew.betagraphook.datagen;

import net.arthurllew.betagraphook.BetaGrapplingHook;
import net.arthurllew.betagraphook.block.BetaGrapplingHookBlocks;
import net.arthurllew.betagraphook.item.BetaGrapplingHookItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, BetaGrapplingHook.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        simpleItem(BetaGrapplingHookItems.GRAPNEL_HEAD);
        simpleBlockItem(BetaGrapplingHookBlocks.ROPE);
        simpleBlockItem(BetaGrapplingHookBlocks.ROPE_PROXY, "rope");
        simpleBlockItem(BetaGrapplingHookBlocks.GRAPNEL);
    }

    private ItemModelBuilder simpleItem(DeferredItem<? extends Item> item) {
        return withExistingParent(item.getId().getPath(),
                ResourceLocation.withDefaultNamespace("item/generated")).texture("layer0",
                ResourceLocation.fromNamespaceAndPath(BetaGrapplingHook.MODID,"item/" + item.getId().getPath()));
    }

    private ItemModelBuilder simpleBlockItem(DeferredBlock<? extends Block> block) {
        return withExistingParent(block.getId().getPath(),
                ResourceLocation.withDefaultNamespace("item/generated")).texture("layer0",
                ResourceLocation.fromNamespaceAndPath(BetaGrapplingHook.MODID,"block/" + block.getId().getPath()));
    }

    private ItemModelBuilder simpleBlockItem(DeferredBlock<? extends Block> block, String name) {
        return withExistingParent(block.getId().getPath(),
                ResourceLocation.withDefaultNamespace("item/generated")).texture("layer0",
                ResourceLocation.fromNamespaceAndPath(BetaGrapplingHook.MODID,"block/" + name));
    }
}
