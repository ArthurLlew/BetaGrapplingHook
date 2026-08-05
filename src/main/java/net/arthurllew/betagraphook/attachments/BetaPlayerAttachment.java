package net.arthurllew.betagraphook.attachments;

import net.arthurllew.betagraphook.entity.GrapplingHookEntity;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class BetaPlayerAttachment {
    /**
     * Grappling hook attached to this player (just like fishing rod hook).
     */
    @Nullable
    public GrapplingHookEntity grapplingHook;

    /**
     * Basic constructor.
     */
    public BetaPlayerAttachment(@Nullable GrapplingHookEntity grapplingHook) {
        this.grapplingHook = grapplingHook;
    }

    /**
     * Simple constructor.
     */
    public BetaPlayerAttachment() {}
}
