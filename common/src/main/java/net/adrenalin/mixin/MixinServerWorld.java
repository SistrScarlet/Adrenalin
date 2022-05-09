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
        timeDelta += timeFactor;
        if (timeDelta < 1.0f) {
            ci.cancel();
            return;
        }
        timeDelta--;
    }

}
