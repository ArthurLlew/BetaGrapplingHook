package net.arthurllew.betagraphook.registry;

import net.arthurllew.betagraphook.BetaGrapplingHook;
import net.arthurllew.betagraphook.entity.GrapplingHookEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BetaGrapplingHookEntityTypes {
    /**
     * Deferred Register for entity types.
     */
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, BetaGrapplingHook.MODID);

    /**
     * Kaevator's Wallpaper entity type.
     */
    public static final Supplier<EntityType<GrapplingHookEntity>> GRAPPLING_HOOK_ENTITY_TYPE =
            ENTITY_TYPES.register("grappling_hook_entity", () ->
                    EntityType.Builder.<GrapplingHookEntity>of(GrapplingHookEntity::new, MobCategory.MISC)
                            .noSave().noSummon().sized(0.25F, 0.25F)
                            .setTrackingRange(4)
                            .setUpdateInterval(5)
                            .build("grappling_hook_entity"));
}
