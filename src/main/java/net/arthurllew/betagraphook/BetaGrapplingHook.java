package net.arthurllew.betagraphook;

import net.arthurllew.betagraphook.registry.BetaGrapplingHookAttachments;
import net.arthurllew.betagraphook.registry.BetaGrapplingHookBlocks;
import net.arthurllew.betagraphook.registry.BetaGrapplingHookBlockEntities;
import net.arthurllew.betagraphook.registry.BetaGrapplingHookEntityTypes;
import net.arthurllew.betagraphook.registry.BetaGrapplingHookItems;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(BetaGrapplingHook.MODID)
public class BetaGrapplingHook {
    /**
     * Mod ID.
     */
    public static final String MODID = "betagraphook";

    /**
     * Mod constructor. Performs basic mod init.
     */
    public BetaGrapplingHook(IEventBus modEventBus) {
        // Register the commonSetup method for mod loading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Registers to the mod event bus
        BetaGrapplingHookEntityTypes.ENTITY_TYPES.register(modEventBus);
        BetaGrapplingHookItems.ITEMS.register(modEventBus);
        BetaGrapplingHookItems.CREATIVE_MODE_TABS.register(modEventBus);
        BetaGrapplingHookBlocks.BLOCKS.register(modEventBus);
        BetaGrapplingHookBlockEntities.BLOCK_ENTITIES.register(modEventBus);

        // Register mod attachments
        BetaGrapplingHookAttachments.ATTACHMENTS.register(modEventBus);
    }

    /**
     * Common mod setup event handler.
     */
    private void commonSetup(FMLCommonSetupEvent event) {}
}
