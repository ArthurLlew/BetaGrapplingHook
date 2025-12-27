package net.arthurllew.betagraphook.block;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
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
public class GrapplingHookBlock extends Block implements SimpleWaterloggedBlock {
    /**
     * Horizontal direction state.
     */
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    /**
     * Grapnel rolled/unrolled state.
     */
    public static final BooleanProperty ROLLED = BooleanProperty.create("rolled");
    /**
     * Waterlogged state.
     */
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    // Block shape
    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);

    /**
     * Horizontal directions array.
     */
    public static final Direction[] HORIZONTAL_DIRECTIONS =
            new Direction[]{Direction.NORTH, Direction.WEST, Direction.SOUTH, Direction.EAST};

    /**
     * Constructor.
     */
    public GrapplingHookBlock(BlockBehaviour.Properties properties) {
        super(properties);

        // Set default state
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH)
                .setValue(ROLLED, true).setValue(WATERLOGGED, false));
    }

    /**
     * Registers block properties.
     */
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ROLLED, WATERLOGGED);
    }

    /**
     * @return block voxel shape.
     */
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
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

        // Set correct block state
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(WATERLOGGED, level.getFluidState(pos).getType() == Fluids.WATER);
    }

    /**
     * Is called on block update.
     * @return parent method result if block can survive, air otherwise.
     */
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        // For all horizontal directions
        for (Direction facing : HORIZONTAL_DIRECTIONS) {
            // move towards selected direction
            BlockPos.MutableBlockPos movablePos = pos.mutable().move(facing);

            // If block is rope proxy
            if (level.getBlockState(movablePos).is(BetaGrapplingHookBlocks.ROPE_PROXY.get())) {
                // Return unrolled state
                return state.setValue(FACING, facing).setValue(ROLLED, false);
            }
        }

        // Return rolled state otherwise
        return state.setValue(ROLLED, true);
    }

    /**
     * @return rotated block.
     */
    @Override
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    /**
     * @return mirrored block.
     */
    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    /**
     * @return block fluid state.
     */
    @Override
    public FluidState getFluidState(BlockState pState) {
        return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
    }
}
