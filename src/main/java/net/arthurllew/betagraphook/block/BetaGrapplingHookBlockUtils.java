package net.arthurllew.betagraphook.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class BetaGrapplingHookBlockUtils {
    /**
     * Replaces given block with water or air depending on its state.
     */
    public static void replaceWithWaterOrAir(LevelAccessor level, BlockPos pos, BlockState state) {
        // Try tp replace block with water or air
        try {
            level.setBlock(pos, state.getValue(BlockStateProperties.WATERLOGGED)
                    ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState(), 3);
        }
        // If there is no waterlogged property
        catch (IllegalArgumentException e) {
            // Replace with air anyway
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        }
    }
}
