package net.arthurllew.betagraphook.block;

import net.arthurllew.betagraphook.registry.BetaGrapplingHookBlockEntities;
import net.arthurllew.betagraphook.block.entity.RopeBlockEntity;
import net.arthurllew.betagraphook.registry.BetaGrapplingHookBlocks;
import net.arthurllew.betagraphook.registry.BetaGrapplingHookItems;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class RopeBlock extends Block implements SimpleWaterloggedBlock, EntityBlock {
    /**
     * Horizontal direction state.
     */
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    /**
     * Waterlogged state.
     */
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    // Block shape
    protected static final VoxelShape EAST_AABB = Block.box(0.0D, 0.0D, 0.0D, 1.0D, 16.0D, 16.0D);
    protected static final VoxelShape WEST_AABB = Block.box(15.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape SOUTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 1.0D);
    protected static final VoxelShape NORTH_AABB = Block.box(0.0D, 0.0D, 15.0D, 16.0D, 16.0D, 16.0D);

    /**
     * Constructor.
     */
    public RopeBlock(BlockBehaviour.Properties properties) {
        super(properties);

        // Set default state
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
    }

    /**
     * Registers block properties.
     */
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    /**
     * @return block voxel shape.
     */
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case NORTH -> NORTH_AABB;
            case SOUTH -> SOUTH_AABB;
            case WEST -> WEST_AABB;
            default -> EAST_AABB;
        };
    }

    /**
     * @return block state corresponding to placement context.
     */
    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        // Placement context
        LevelReader level = context.getLevel();
        BlockPos pos = context.getClickedPos();

        // Set block direction
        BlockState block = this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());

        // Check survivability
        if (block.canSurvive(level, pos)) {
            // Set water state and return
            return block.setValue(WATERLOGGED, level.getFluidState(pos).getType() == Fluids.WATER);
        }

        // Can't place block
        return null;
    }

    /**
     * @return whether block can survive at given conditions.
     */
    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        // Block above should be either rope or rope proxy
        BlockPos.MutableBlockPos movablePos = pos.mutable().move(Direction.UP);
        BlockState block = level.getBlockState(movablePos);
        return block.is(this) || block.is(BetaGrapplingHookBlocks.ROPE_PROXY.get());
    }

    /**
     * Is called on block update.
     * @return parent method result if block can survive, air otherwise.
     */
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        // If block can't survive
        if (!state.canSurvive(level, pos)) {
            // Update water
            if (state.getValue(WATERLOGGED)) {
                level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
            }

            // Replace with air
            return Blocks.AIR.defaultBlockState();
        }

        // If block update happened below rope
        if (direction == Direction.DOWN) {
            // Reset rope lowering delay
            if (level.getBlockEntity(pos) instanceof RopeBlockEntity rope) {
                rope.resetDelay();
            }
        }

        // Return unchanged state
        return state;
    }

    /**
     * Is called on block being removed by player.
     */
    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player,
                                       boolean willHarvest, FluidState fluid) {
        // If player can harvest this block
        if (willHarvest) {
            // Drop grappling hook
            popResource(level, pos, new ItemStack(BetaGrapplingHookItems.GRAPPLING_HOOK.get()));
        }

        // Run parent method
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    /**
     * Is called after this block was removed by player.
     */
    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        // Remove all rope blocks above
        BlockPos.MutableBlockPos movablePos = removeRopeColumnAbove(level, pos);

        // If last block is rope proxy
        BlockState block = level.getBlockState(movablePos);
        if (block.is(BetaGrapplingHookBlocks.ROPE_PROXY.get())) {
            // Replace it with water or air
            this.replaceWithWaterOrAir(level, movablePos, block);

            // Move towards grappling hook
            movablePos.move(state.getValue(FACING).getOpposite());

            // If block is a grappling hook
            block = level.getBlockState(movablePos);
            if (block.is(BetaGrapplingHookBlocks.GRAPNEL.get())) {
                // Replace it with water or air
                this.replaceWithWaterOrAir(level, movablePos, block);
            }
        }
    }

    /**
     * Removes all rope blocks above and excluding provided position.
     * @return position of non-rope block at the end of removed column.
     */
    public BlockPos.MutableBlockPos removeRopeColumnAbove(LevelAccessor level, BlockPos pos) {
        // Get position below
        BlockPos.MutableBlockPos movablePos = pos.mutable().move(Direction.UP);

        // Iterate over all rope blocks above
        BlockState block = level.getBlockState(movablePos);
        while (block.is(this)) {
            // Replace with water or air
            this.replaceWithWaterOrAir(level, movablePos, block);

            // Get next block
            movablePos.move(Direction.UP);
            block = level.getBlockState(movablePos);
        }

        // Return last position
        return movablePos;
    }

    /**
     * Replaces given block with water or air depending on its state.
     */
    private void replaceWithWaterOrAir(LevelAccessor level, BlockPos pos, BlockState state) {
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

    /**
     * @return rotated block.
     */
    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    /**
     * @return mirrored block.
     */
    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    /**
     * @return block fluid state.
     */
    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    /**
     * @return block entity associated with this block.
     */
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RopeBlockEntity(pos, state);
    }

    /**
     * @return ticker for block entity associated with this block.
     */
    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                  BlockEntityType<T> blockEntityType) {
        return (!level.isClientSide && (blockEntityType == BetaGrapplingHookBlockEntities.ROPE_BLOCK_ENTITY.get())) ?
                RopeBlockEntity::serverTick : null;
    }

    /**
     * Places rope block if block below provided rope is air or water.
     */
    public void lowerRope(BlockState state, LevelAccessor level, BlockPos pos) {
        // Block below is air or water
        BlockPos posBelow = pos.relative(Direction.DOWN);
        BlockState block = level.getBlockState(posBelow);
        // Air
        if (!level.isOutsideBuildHeight(posBelow) && block.isAir()) {
            // Place rope with same direction
            level.setBlock(posBelow, this.defaultBlockState()
                    .setValue(FACING, state.getValue(FACING)), 3);
        }
        // Water
        else if (block.is(Blocks.WATER)) {
            // Place waterlogged rope with same direction
            level.setBlock(posBelow, this.defaultBlockState().setValue(WATERLOGGED, true)
                    .setValue(FACING, state.getValue(FACING)), 3);
        }
        // Rope proxy
        else if (block.is(BetaGrapplingHookBlocks.ROPE_PROXY.get())) {
            // Replace it
            level.setBlock(posBelow, this.defaultBlockState().setValue(WATERLOGGED, block.getValue(WATERLOGGED))
                    .setValue(FACING, block.getValue(FACING)), 3);
        }
    }
}
