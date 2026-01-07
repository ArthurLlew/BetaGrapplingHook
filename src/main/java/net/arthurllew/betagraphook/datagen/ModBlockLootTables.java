package net.arthurllew.betagraphook.datagen;

import net.arthurllew.betagraphook.registry.BetaGrapplingHookBlocks;
import net.arthurllew.betagraphook.registry.BetaGrapplingHookItems;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;

import java.util.Set;

public class ModBlockLootTables extends BlockLootSubProvider {
    protected ModBlockLootTables(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    public void generate() {
        // Drops grappling hook
        this.dropOther(BetaGrapplingHookBlocks.GRAPNEL.get(), BetaGrapplingHookItems.GRAPPLING_HOOK.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        // All blocks except rope and its proxy
        return BetaGrapplingHookBlocks.BLOCKS.getEntries().stream().map(Holder::value)
                .filter(block -> block != BetaGrapplingHookBlocks.ROPE.get()
                        && block != BetaGrapplingHookBlocks.ROPE_PROXY.get())::iterator;
    }
}
