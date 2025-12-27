package net.arthurllew.betagraphook.item;

import net.arthurllew.betagraphook.BetaGrapplingHook;
import net.arthurllew.betagraphook.block.BetaGrapplingHookBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public abstract class BetaGrapplingHookItems {
    /**
     * Deferred Register for items.
     */
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(BetaGrapplingHook.MODID);
    /**
     * Deferred Register for creative tabs.
     */
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, BetaGrapplingHook.MODID);

    /**
     * Grapnel head item.
     */
    public static final DeferredItem<Item> GRAPNEL_HEAD = ITEMS.register("grapnel_head",
            () -> new Item(new Item.Properties().stacksTo(1)));

    /**
     * Grappling hook item.
     */
    public static final DeferredItem<GrapplingHookItem> GRAPPLING_HOOK = ITEMS.register("grappling_hook",
            () -> new GrapplingHookItem(new Item.Properties().stacksTo(1)));

    /**
     * Beta Deco item group.
     */
    public static final Supplier<CreativeModeTab> BETA_GRAPPLING_HOOK_ITEM_GROUP =
            CREATIVE_MODE_TABS.register("graphooktab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemgroup." + BetaGrapplingHook.MODID + ".graphook"))
                    .icon(() -> GRAPPLING_HOOK.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(GRAPNEL_HEAD.get());
                        output.accept(GRAPPLING_HOOK.get());
                        output.accept(BetaGrapplingHookBlocks.ROPE.get());
                        output.accept(BetaGrapplingHookBlocks.ROPE_PROXY.get());
                        output.accept(BetaGrapplingHookBlocks.GRAPNEL.get());
                    }).build());
}
