package net.arthurllew.betagraphook.block.entity;

import net.arthurllew.betagraphook.block.RopeBlock;
import net.arthurllew.betagraphook.registry.BetaGrapplingHookBlockEntities;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Block entity that allows to slowly lower {@link RopeBlock}.
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class RopeBlockEntity extends BlockEntity {
    /**
     * Delay before new rope can be placed below.
     */
    private int delay = 5;

    /**
     * Constructor.
     */
    public RopeBlockEntity(BlockPos pos, BlockState blockState) {
        super(BetaGrapplingHookBlockEntities.ROPE_BLOCK_ENTITY.get(), pos, blockState);
    }

    /**
     * Resets rope placement delay.
     */
    public void resetDelay() {
        this.delay = 5;
    }

    /**
     * Ticks block entity on server.
     */
    public static void serverTick(Level level, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        RopeBlockEntity rope = (RopeBlockEntity)blockEntity;

        // If rope delay is not locked
        if(rope.delay != 6) {
            // Delay expired
            if (rope.delay <= 0) {
                // Lower rope
                ((RopeBlock)state.getBlock()).lowerRope(state, level, pos);

                // Lock delay
                rope.delay = 6;
            }
            // Delay is active
            else {
                --rope.delay;
            }
        }
    }

    /**
     * Loads block entity data from world save file.
     * @param tag data to load.
     */
    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        this.delay = tag.getInt("Delay");
    }

    /**
     * Saves block entity data in world save file.
     * @param tag data to save.
     */
    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        tag.putInt("Delay", this.delay);
    }
}
