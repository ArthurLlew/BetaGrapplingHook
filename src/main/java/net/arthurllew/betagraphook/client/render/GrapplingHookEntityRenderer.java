package net.arthurllew.betagraphook.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.arthurllew.betagraphook.BetaGrapplingHook;
import net.arthurllew.betagraphook.entity.GrapplingHookEntity;
import net.arthurllew.betagraphook.registry.BetaGrapplingHookItems;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class GrapplingHookEntityRenderer extends EntityRenderer<GrapplingHookEntity> {
    /**
     * Used texture.
     */
    private static final ResourceLocation TEXTURE_LOCATION =
            ResourceLocation.fromNamespaceAndPath(BetaGrapplingHook.MODID, "textures/item/grapnel_head.png");
    /**
     * Entity render type.
     */
    private static final RenderType RENDER_TYPE = RenderType.entityCutout(TEXTURE_LOCATION);

    /**
     * Constructor.
     */
    public GrapplingHookEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    /**
     * @return texture identifier.
     */
    public ResourceLocation getTextureLocation(GrapplingHookEntity entity) {
        return TEXTURE_LOCATION;
    }

    /**
     * Renders entity (code adopted from {@link net.minecraft.client.renderer.entity.FishingHookRenderer#render}).
     */
    public void render(GrapplingHookEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        // Get hook owner
        Player player = entity.getPlayerOwner();

        // Only if owner exists
        if (player != null) {
            // Acquire matrices for entity and string
            poseStack.pushPose();
            poseStack.pushPose();

            // Update matrices
            poseStack.scale(0.5F, 0.5F, 0.5F);
            poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
            //poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            PoseStack.Pose lastPose = poseStack.last();

            // Get vertex consumer
            VertexConsumer vertexConsumer = buffer.getBuffer(RENDER_TYPE);

            // Entity sprite
            vertex(vertexConsumer, lastPose, packedLight, 0.0F, 0, 0, 1);
            vertex(vertexConsumer, lastPose, packedLight, 1.0F, 0, 1, 1);
            vertex(vertexConsumer, lastPose, packedLight, 1.0F, 1, 1, 0);
            vertex(vertexConsumer, lastPose, packedLight, 0.0F, 1, 0, 0);

            // Don't forget to release matrix (entity sprite)
            poseStack.popPose();

            // Determine correct player arm
            int arm = player.getMainArm() == HumanoidArm.RIGHT ? 1 : -1;
            ItemStack itemstack = player.getMainHandItem();
            if (!itemstack.is(BetaGrapplingHookItems.GRAPPLING_HOOK.get())) {
                arm = -arm;
            }

            // Complicated math to get proper string positions
            float attackProgress = player.getAttackAnim(partialTicks);
            float attackProgressScaled = Mth.sin(Mth.sqrt(attackProgress) * (float)Math.PI);
            float yRot = Mth.lerp(partialTicks, player.yBodyRotO, player.yBodyRot) * ((float)Math.PI / 180F);
            double yRotSin = Mth.sin(yRot);
            double yRotCos = Mth.cos(yRot);
            double armLocation = (double)arm * 0.35D;

            // Get camera parameters
            double viewX;
            double viewY;
            double viewZ;
            float height;
            // First person view by hook owner
            if (this.entityRenderDispatcher.options.getCameraType().isFirstPerson()
                    && player == Minecraft.getInstance().player) {
                double d7 = 960.0D / (double)this.entityRenderDispatcher.options.fov().get();

                Vec3 cameraPont = this.entityRenderDispatcher.camera.getNearPlane()
                        .getPointOnPlane((float)arm * 0.525F, -0.4F);

                cameraPont = cameraPont.scale(d7);
                cameraPont = cameraPont.yRot(attackProgressScaled * 0.5F);
                cameraPont = cameraPont.xRot(-attackProgressScaled * 0.7F);

                viewX = Mth.lerp(partialTicks, player.xo, player.getX()) + cameraPont.x;
                viewY = Mth.lerp(partialTicks, player.yo, player.getY()) + cameraPont.y;
                viewZ = Mth.lerp(partialTicks, player.zo, player.getZ()) + cameraPont.z;

                height = player.getEyeHeight();
            }
            // Other situations (3rd person or another player)
            else {
                viewX = Mth.lerp(partialTicks, player.xo, player.getX()) - yRotCos * armLocation - yRotSin * 0.6D;
                viewY = player.yo + (double)player.getEyeHeight()
                        + (player.getY() - player.yo) * (double)partialTicks - 0.45D;
                viewZ = Mth.lerp(partialTicks, player.zo, player.getZ()) - yRotSin * armLocation + yRotCos * 0.6D;

                height = player.isCrouching() ? -0.4875F : -0.4F;
            }

            // Entity location
            double entityX = Mth.lerp(partialTicks, entity.xo, entity.getX());
            double entityY = Mth.lerp(partialTicks, entity.yo, entity.getY()) + 0.25D;
            double entityZ = Mth.lerp(partialTicks, entity.zo, entity.getZ());

            // String length
            float stringLengthX = (float)(viewX - entityX);
            float stringLengthY = (float)(viewY - entityY) + height;
            float stringLengthZ = (float)(viewZ - entityZ);

            // Update consumer and pose
            vertexConsumer = buffer.getBuffer(RenderType.lineStrip());
            lastPose = poseStack.last();

            // String rendering
            for(int k = 0; k <= 16; ++k) {
                stringVertex(stringLengthX, stringLengthY, stringLengthZ,
                        vertexConsumer, lastPose,
                        ((float)k)/16.0F, ((float)k+1)/16.0F);
            }

            // Don't forget to release matrix (string)
            poseStack.popPose();

            // Parent method
            super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        }
    }

    /**
     * Creates entity sprite vertex.
     */
    private static void vertex(VertexConsumer consumer, PoseStack.Pose pose,
                               int light, float x, int y, int u, int v) {
        consumer.addVertex(pose, x - 0.5F, (float)y - 0.5F, 0.0F)
                .setColor(255, 255, 255, 255)
                .setUv((float)u, (float)v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, 0.0F, 1.0F, 0.0F);
    }

    /**
     * Creates string vertex.
     */
    private static void stringVertex(float x, float y, float z,
                                     VertexConsumer consumer, PoseStack.Pose pose,
                                     float delta, float deltaNormal) {
        // Compute vertex coordinates
        float vX = x * delta;
        float vY = y * (delta * delta + delta) * 0.5F + 0.25F;
        float vZ = z * delta;

        // Compute normalized normal vector
        float normalX = x * deltaNormal - vX;
        float normalY = y * (deltaNormal * deltaNormal + deltaNormal) * 0.5F + 0.25F - vY;
        float normalZ = z * deltaNormal - vZ;
        float normalLength = Mth.sqrt(normalX * normalX + normalY * normalY + normalZ * normalZ);
        normalX /= normalLength;
        normalY /= normalLength;
        normalZ /= normalLength;

        // Create vertex
        consumer.addVertex(pose, vX, vY, vZ)
                .setColor(109, 109, 67, 255) // the same color is on rope's edge in item texture
                .setNormal(pose, normalX, normalY, normalZ);
    }
}
