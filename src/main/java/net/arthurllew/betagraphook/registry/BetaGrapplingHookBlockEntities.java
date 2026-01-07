package net.arthurllew.betagraphook.registry;

import net.arthurllew.betagraphook.BetaGrapplingHook;
import net.arthurllew.betagraphook.block.entity.RopeBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public abstract class BetaGrapplingHookBlockEntities {
    /**
     * Deferred Register for block entities.
     */
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, BetaGrapplingHook.MODID);

    /**
     * Rope blocks.
     */
    public static final Supplier<BlockEntityType<RopeBlockEntity>> ROPE_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("rope_block", () ->
                    BlockEntityType.Builder.of(RopeBlockEntity::new, BetaGrapplingHookBlocks.ROPE.get())
                            .build(null));
}
