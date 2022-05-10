package net.adrenalin.mixin;

import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public abstract class MixinServerWorld extends MixinWorld {
    /*private boolean canTick = true;*/

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void onTick(CallbackInfo ci) {
        setTimeDelta(getTimeDelta() + getTimeFactor());
        if (getTimeDelta() < 1.0) {
            ci.cancel();
            return;
        }
        setTimeDelta(getTimeDelta() - 1.0);
    }

}
