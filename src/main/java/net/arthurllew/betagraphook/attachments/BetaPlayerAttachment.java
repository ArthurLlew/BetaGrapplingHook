package net.arthurllew.betagraphook.attachments;

import net.arthurllew.betagraphook.entity.GrapplingHookEntity;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Player custom data.
 */
@ParametersAreNonnullByDefault
public class BetaPlayerAttachment {
    /**
     * Grappling hook attached to this player.
     */
    @Nullable
    public GrapplingHookEntity grapplingHook;

    /**
     * Player custom data.
     */
    public BetaPlayerAttachment(@Nullable GrapplingHookEntity grapplingHook) {
        this.grapplingHook = grapplingHook;
    }
    public BetaPlayerAttachment() {}
}
