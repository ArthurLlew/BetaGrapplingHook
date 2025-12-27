package net.arthurllew.betagraphook;

import net.arthurllew.betagraphook.attachments.BetaGrapplingHookAttachments;
import net.arthurllew.betagraphook.block.BetaGrapplingHookBlocks;
import net.arthurllew.betagraphook.block.entity.BetaGrapplingHookBlockEntities;
import net.arthurllew.betagraphook.entity.BetaGrapplingHookEntityTypes;
import net.arthurllew.betagraphook.item.BetaGrapplingHookItems;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod(BetaGrapplingHook.MODID)
public class BetaGrapplingHook {
    /**
     * Mod ID.
     */
    public static final String MODID = "betagraphook";

    /**
     * Basic mod init.
     */
    public BetaGrapplingHook(IEventBus modEventBus, ModContainer modContainer) {
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
