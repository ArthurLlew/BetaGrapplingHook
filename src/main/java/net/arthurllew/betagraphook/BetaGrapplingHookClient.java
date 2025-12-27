package net.arthurllew.betagraphook;

import net.arthurllew.betagraphook.client.render.GrapplingHookEntityRenderer;
import net.arthurllew.betagraphook.entity.BetaGrapplingHookEntityTypes;
import net.arthurllew.betagraphook.item.BetaGrapplingHookItems;
import net.arthurllew.betagraphook.item.GrapplingHookItem;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = BetaGrapplingHook.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = BetaGrapplingHook.MODID, value = Dist.CLIENT)
public class BetaGrapplingHookClient {
    public BetaGrapplingHookClient(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

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
