package net.arthurllew.betagraphook.block;

import net.arthurllew.betagraphook.BetaGrapplingHook;
import net.arthurllew.betagraphook.item.BetaGrapplingHookItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public abstract class BetaGrapplingHookBlocks {
    /**
     * Deferred Register for blocks.
     */
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(BetaGrapplingHook.MODID);

    /**
     * Rope block.
     */
    public static final DeferredBlock<RopeBlock> ROPE = registerBlock("rope",
            () -> new RopeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.LADDER)
                    .sound(SoundType.WOOL).pushReaction(PushReaction.IGNORE)));

    /**
     * Rope proxy block.
     */
    public static final DeferredBlock<RopeProxyBlock> ROPE_PROXY = registerBlock("rope_proxy",
            () -> new RopeProxyBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.LADDER)
                    .sound(SoundType.WOOL).pushReaction(PushReaction.IGNORE).noCollission().noLootTable()));

    /**
     * Grappling hook block.
     */
    public static final DeferredBlock<GrapplingHookBlock> GRAPNEL = registerBlock("grapnel",
            () -> new GrapplingHookBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.LADDER).sound(SoundType.METAL)));

    /**
     * Registers block and its item.
     * @param name block id.
     * @param block block supplier.
     * @return registered block.
     * @param <T> block type.
     */
    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> reg = BLOCKS.register(name, block);
        BetaGrapplingHookItems.ITEMS.register(name, () -> new BlockItem(reg.get(), new Item.Properties()));
        return reg;
    }
}
