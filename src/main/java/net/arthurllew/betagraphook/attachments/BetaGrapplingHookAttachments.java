package net.arthurllew.betagraphook.attachments;

import com.mojang.serialization.Codec;
import net.arthurllew.betagraphook.BetaGrapplingHook;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * Registers custom attachments linked to minecraft classes.
 */
public class BetaGrapplingHookAttachments {
    /**
     * Deferred Register for attachments.
     */
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, BetaGrapplingHook.MODID);

    /**
     * Custom player attachment.
     */
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<BetaPlayerAttachment>> BETA_PLAYER_ATTACHMENT =
            ATTACHMENTS.register("beta_player", () -> AttachmentType.builder(() -> new BetaPlayerAttachment())
                    .serialize(Codec.unit(BetaPlayerAttachment::new)).copyOnDeath().build());
}
