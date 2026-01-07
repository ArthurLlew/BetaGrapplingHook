package net.arthurllew.betagraphook.client;

import net.arthurllew.betagraphook.BetaGrapplingHook;
import net.arthurllew.betagraphook.client.render.GrapplingHookEntityRenderer;
import net.arthurllew.betagraphook.registry.BetaGrapplingHookEntityTypes;
import net.arthurllew.betagraphook.registry.BetaGrapplingHookItems;
import net.arthurllew.betagraphook.item.GrapplingHookItem;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@Mod(value = BetaGrapplingHook.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = BetaGrapplingHook.MODID, value = Dist.CLIENT)
public class BetaGrapplingClient {
    /**
     * Mod setup on client.
     */
    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Register renderer for grappling hook entity
        EntityRenderers.register(BetaGrapplingHookEntityTypes.GRAPPLING_HOOK_ENTITY_TYPE.get(),
                GrapplingHookEntityRenderer::new);

        // Register custom item property
        ItemProperties.register(BetaGrapplingHookItems.GRAPPLING_HOOK.get(),
                ResourceLocation.fromNamespaceAndPath(BetaGrapplingHook.MODID, "grapnel"),
                GrapplingHookItem::itemPropertyHandler);
    }
}
