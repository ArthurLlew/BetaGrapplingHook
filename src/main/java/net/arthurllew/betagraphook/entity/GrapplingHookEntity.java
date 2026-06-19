package net.arthurllew.betagraphook.entity;

import com.mojang.logging.LogUtils;
import net.arthurllew.betagraphook.registry.BetaGrapplingHookAttachments;
import net.arthurllew.betagraphook.registry.BetaGrapplingHookBlocks;
import net.arthurllew.betagraphook.block.GrapplingHookBlock;
import net.arthurllew.betagraphook.block.RopeBlock;
import net.arthurllew.betagraphook.block.RopeProxyBlock;
import net.arthurllew.betagraphook.attachments.BetaPlayerAttachment;
import net.arthurllew.betagraphook.registry.BetaGrapplingHookEntityTypes;
import net.arthurllew.betagraphook.registry.BetaGrapplingHookItems;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Grappling hook entity. Works similarly to {@link net.minecraft.world.entity.projectile.FishingHook}.
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class GrapplingHookEntity extends Projectile {
    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * Direction in which this entity was thrown by a player.
     */
    private Direction placementDirection = Direction.NORTH;

    /**
     * How many ticks this entity as on the ground.
     */
    private int ticksOnGround = 0;

    /**
     * This constructor is used to register entity type.
     */
    public GrapplingHookEntity(EntityType<? extends GrapplingHookEntity> entityType, Level level) {
        super(entityType, level);
    }

    /**
     * Used to define custom entity data. This entity has none.
     */
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    /**
     * This constructor is used to spawn entity.
     */
    public GrapplingHookEntity(Player player, Level level) {
        this(BetaGrapplingHookEntityTypes.GRAPPLING_HOOK_ENTITY_TYPE.get(), level);

        // Set owner (will also update its info)
        this.setOwner(player);

        // Calculate player X angles
        float xRot = player.getXRot();
        float xRotCos = -Mth.cos(-xRot * ((float)Math.PI / 180F));
        float xRotSin = Mth.sin(-xRot * ((float)Math.PI / 180F));

        // Calculate player Y angles
        float yRot = player.getYRot();
        float yRotCos = Mth.cos(-yRot * ((float)Math.PI / 180F) - (float)Math.PI);
        float yRotSin = Mth.sin(-yRot * ((float)Math.PI / 180F) - (float)Math.PI);

        // Sync entity position and rotation with player
        double playerX = player.getX() - (double)yRotSin * 0.3D;
        double playerY = player.getEyeY();
        double playerZ = player.getZ() - (double)yRotCos * 0.3D;
        this.moveTo(playerX, playerY, playerZ, yRot, xRot);

        // Set impulse
        Vec3 impulse = new Vec3(yRotSin * xRotCos, xRotSin, yRotCos * xRotCos);
        impulse = impulse.multiply(this.random.triangle(0.8D, 0.01D),
                this.random.triangle(1.2D, 0.01D),
                this.random.triangle(0.8D, 0.01D));
        this.setDeltaMovement(impulse);

        // Set rotations from impulse
        this.setYRot((float)(Mth.atan2(impulse.x, impulse.z) * (double)(180F / (float)Math.PI)));
        this.setXRot((float)(Mth.atan2(impulse.y, impulse.horizontalDistance()) * (double)(180F / (float)Math.PI)));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();

        // Compute placement direction (opposite to thrown direction)
        float minDist = Float.MIN_VALUE;
        for(Direction direction : GrapplingHookBlock.HORIZONTAL_DIRECTIONS) {
            Vec3i normal = direction.getNormal();
            float dist = (float)(impulse.x * normal.getX() + impulse.y * normal.getY() + impulse.z * normal.getZ());
            if (dist > minDist) {
                minDist = dist;
                this.placementDirection = direction.getOpposite();
            }
        }
    }

    /**
     * @return entity movement emission (none for this kind of entity).
     */
    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.NONE;
    }

    /**
     * @return whether this entity is in range to render.
     */
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 4096.0D;
    }

    /**
     * @return whether this entity can move between dimensions.
     */
    @Override
    public boolean canChangeDimensions(Level oldLevel, Level newLevel) {
        return false;
    }

    /**
     * @return player owning this entity.
     */
    @Nullable
    public Player getPlayerOwner() {
        Entity entity = this.getOwner();
        return entity instanceof Player ? (Player)entity : null;
    }

    /**
     * Sets entity's owner.
     */
    public void setOwner(@Nullable Entity owner) {
        super.setOwner(owner);

        // Update owner
        this.updateOwnerInfo(this);
    }

    /**
     * Updates entity's owner info.
     */
    private void updateOwnerInfo(@Nullable GrapplingHookEntity grapplingHook) {
        // Get existing owner
        Player player = this.getPlayerOwner();
        if (player != null) {
            // Set beta player attachment
            player.setData(BetaGrapplingHookAttachments.BETA_PLAYER_ATTACHMENT,
                    new BetaPlayerAttachment(grapplingHook));
        }
    }

    /**
     * Removes this entity.
     */
    public void remove(Entity.RemovalReason reason) {
        // Update owner
        this.updateOwnerInfo(null);

        // Remove entity
        super.remove(reason);
    }

    /**
     * Called client-side on entity removal.
     */
    public void onClientRemoval() {
        // Update owner
        this.updateOwnerInfo(null);
    }

    /**
     * Ticks this entity.
     */
    public void tick() {
        super.tick();

        // Check player is alive
        Player player = this.getPlayerOwner();
        if (player == null) {
            this.discard();
        }
        // Check grappling hook living conditions
        else if (this.level().isClientSide || !this.shouldRetrieveHook(player)) {
            // Update ticks spend on ground
            if (this.onGround()) {
                ++this.ticksOnGround;
            } else {
                this.ticksOnGround = 0;
            }

            // If hook is on ground for too long
            if (this.ticksOnGround >= 5) {
                // Place blocks only server-side
                Level level = this.level();
                if (!level.isClientSide) {
                    BlockPos pos = this.blockPosition();

                    // If block below is solid
                    if (level.getBlockState(pos.relative(Direction.DOWN)).isSolid()) {
                        // Block at entity position
                        BlockState block = level.getBlockState(pos);
                        boolean isWater = block.is(Blocks.WATER);

                        // Block at entity position is air or water
                        if (!level.isOutsideBuildHeight(pos) && block.isAir() || isWater) {
                            // Block at potential rope proxy position
                            BlockPos ropeProxyPos = pos.relative(this.placementDirection);
                            BlockState blockRopeProxy = level.getBlockState(ropeProxyPos);
                            boolean isWaterRopeProxy = blockRopeProxy.is(Blocks.WATER);
                            // Block at potential rope position
                            BlockPos ropePos = ropeProxyPos.relative(Direction.DOWN);
                            BlockState blockRope = level.getBlockState(ropePos);
                            boolean isWaterRope = blockRope.is(Blocks.WATER);

                            // Same conditions for rope placement
                            if ((level.isInWorldBounds(ropeProxyPos) && blockRopeProxy.isAir() || isWaterRopeProxy)
                                    && (!level.isOutsideBuildHeight(ropePos) && blockRope.isAir() || isWaterRope)) {
                                // Place grapnel head
                                level.setBlock(pos, BetaGrapplingHookBlocks.GRAPNEL.get()
                                        .defaultBlockState()
                                        .setValue(GrapplingHookBlock.WATERLOGGED, isWater)
                                        .setValue(GrapplingHookBlock.FACING, this.placementDirection), 3);
                                // Place rope proxy
                                level.setBlock(ropeProxyPos, BetaGrapplingHookBlocks.ROPE_PROXY.get()
                                        .defaultBlockState()
                                        .setValue(RopeProxyBlock.WATERLOGGED, isWaterRopeProxy)
                                        .setValue(RopeProxyBlock.FACING, this.placementDirection), 3);
                                // Place rope
                                level.setBlock(ropePos, BetaGrapplingHookBlocks.ROPE.get()
                                        .defaultBlockState()
                                        .setValue(RopeBlock.WATERLOGGED, isWaterRope)
                                        .setValue(RopeBlock.FACING, this.placementDirection), 3);

                                // If player is not in creative mode
                                if (!player.isCreative()) {
                                    // Remove grappling hook item
                                    ItemStack itemRight = player.getMainHandItem();
                                    ItemStack itemLeft = player.getOffhandItem();
                                    if (itemRight.is(BetaGrapplingHookItems.GRAPPLING_HOOK.get())) {
                                        itemRight.shrink(1);
                                    }
                                    else if (itemLeft.is(BetaGrapplingHookItems.GRAPPLING_HOOK.get())) {
                                        itemLeft.shrink(1);
                                    }
                                }
                            }
                        }
                    }
                }

                // Discard entity
                this.discard();
            }
            // Is still moving
            else {
                // Process hit result
                HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
                if (hitresult.getType() != HitResult.Type.MISS
                        && !EventHooks.onProjectileImpact(this, hitresult)) {
                    this.onHit(hitresult);
                }

                // Gravity
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.03D, 0.0D));

                // Move and update
                this.move(MoverType.SELF, this.getDeltaMovement());
                this.updateRotation();

                // Stop entity if it is on the ground or hit something
                if (this.onGround() || this.horizontalCollision) {
                    this.setDeltaMovement(Vec3.ZERO);
                }

                // Movement gradually slows over time
                this.setDeltaMovement(this.getDeltaMovement().scale(0.98D));
                // Recompute entity's bounding box
                this.reapplyPosition();
            }
        }
    }

    /**
     * @return whether player should retrieve hook.
     */
    private boolean shouldRetrieveHook(Player player) {
        // Item conditions
        ItemStack itemRight = player.getMainHandItem();
        ItemStack itemLeft = player.getOffhandItem();
        boolean flag1 = itemRight.is(BetaGrapplingHookItems.GRAPPLING_HOOK.get());
        boolean flag2 = itemLeft.is(BetaGrapplingHookItems.GRAPPLING_HOOK.get());

        // Player is alive, item in either hand is a grappling hook and distance from player is not too large
        if (!player.isRemoved() && player.isAlive()
                && (flag1 || flag2)
                && !(this.distanceToSqr(player) > 1024.0D)) {
            return false;
        }
        // Else discard this entity
        else {
            this.discard();
            return true;
        }
    }

    /**
     * Writes custom NBT data into save file.
     */
    public void addAdditionalSaveData(CompoundTag compound) {
    }

    /**
     * Reads custom NBT data from save file.
     */
    public void readAdditionalSaveData(CompoundTag compound) {
    }

    /**
     * @return entity packet.
     */
    public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity p_entity) {
        Entity entity = this.getOwner();
        return new ClientboundAddEntityPacket(this, p_entity, entity == null ? this.getId() : entity.getId());
    }

    /**
     * Tries to recreate this entity from received packet.
     */
    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);

        // If owner somehow doesn't exist, log this issue
        if (this.getPlayerOwner() == null) {
            int i = packet.getData();
            LOGGER.error("Failed to recreate grappling hook on client. {} (id: {}) is not a valid owner.",
                    this.level().getEntity(i), i);

            this.kill();
        }
    }
}
