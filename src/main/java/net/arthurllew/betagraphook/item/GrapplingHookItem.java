package net.arthurllew.betagraphook.item;

import net.arthurllew.betagraphook.registry.BetaGrapplingHookAttachments;
import net.arthurllew.betagraphook.entity.GrapplingHookEntity;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Grappling hook item. Works similarly to {@link net.minecraft.world.item.FishingRodItem}.
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class GrapplingHookItem extends Item {
    public GrapplingHookItem(Properties properties) {
        super(properties);
    }

    /**
     * Called on item use by player.
     * @return interaction result.
     */
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);

        // Grappling hook was thrown
        if (player.getData(BetaGrapplingHookAttachments.BETA_PLAYER_ATTACHMENT).grapplingHook != null) {
            // Remove grappling hook
            if (!level.isClientSide) {
                player.getData(BetaGrapplingHookAttachments.BETA_PLAYER_ATTACHMENT).grapplingHook.discard();
            }

            // Play retrieve sound
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.FISHING_BOBBER_RETRIEVE, SoundSource.NEUTRAL,
                    1.0F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));

            // Player game event (item interaction finished)
            player.gameEvent(GameEvent.ITEM_INTERACT_FINISH);
        }
        // Grappling hook should be thrown
        else {
            // Play throw sound
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.FISHING_BOBBER_THROW, SoundSource.NEUTRAL,
                    0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));

            // Add grappling hook entity
            if (!level.isClientSide) {
                level.addFreshEntity(new GrapplingHookEntity(player, level));
            }

            // Player game event (item interaction started)
            player.gameEvent(GameEvent.ITEM_INTERACT_START);
        }

        // Sided success
        return InteractionResultHolder.sidedSuccess(itemstack, level.isClientSide());
    }

    /**
     * Handles custom item property related to the grappling hook.
     */
    public static float itemPropertyHandler(ItemStack item, @Nullable ClientLevel level,
                                            @Nullable LivingEntity entity, int seed) {
        // Entity should be a player
        if (entity instanceof Player player) {
            // Check items in hands
            boolean isItemInMainHand = player.getMainHandItem() == item;
            boolean isItemInOffHand = player.getOffhandItem() == item;

            // Prefer main hand
            if (player.getMainHandItem().getItem() instanceof GrapplingHookItem) {
                isItemInOffHand = false;
            }

            // Check whether player has active grappling hook
            boolean hasHook = player.getData(BetaGrapplingHookAttachments.BETA_PLAYER_ATTACHMENT).grapplingHook != null;

            return (isItemInMainHand || isItemInOffHand) && hasHook ? 1.0F : 0.0F;
        }

        return 0.0F;
    }
}
