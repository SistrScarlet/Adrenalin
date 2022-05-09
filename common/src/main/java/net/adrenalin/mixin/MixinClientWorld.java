package net.adrenalin.mixin;

import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ClientWorld.class)
public class MixinClientWorld extends MixinWorld {
    /*private boolean canTick = true;*/

    /*@Inject(method = "tickEntities", at = @At("HEAD"), cancellable = true)
    private void onTickEntities(CallbackInfo ci) {
        timeDelta += timeFactor;
        if (timeDelta < 1.0f) {
            canTick = false;
            ci.cancel();
            return;
        }
        canTick = true;
        timeDelta--;
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void onTick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        if (!canTick) {
            ci.cancel();
        }
    }*/


}