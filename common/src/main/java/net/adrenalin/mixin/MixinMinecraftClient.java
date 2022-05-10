package net.adrenalin.mixin;

import net.adrenalin.util.TimeDeacceleratable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {

    @Shadow
    @Nullable
    public ClientWorld world;

    @Shadow
    private volatile boolean paused;

    private double ticksDelta;
    private double deltaStack;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void onTick(CallbackInfo ci) {
        ClientWorld world = this.world;
        if (world instanceof TimeDeacceleratable t) {
            t.setTimeDelta(t.getTimeDelta() + t.getTimeFactor());
            if (t.getTimeDelta() < 1.0) {
                deltaStack += ticksDelta;
                ci.cancel();
                return;
            }
            deltaStack = 0;
            t.setTimeDelta(t.getTimeDelta() - 1.0);
            ticksDelta = 1.0 / MathHelper.ceil((1 - t.getTimeDelta()) / t.getTimeFactor());
        }
    }

    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;render(FJZ)V"))
    private float modifyRender(float tickDelta) {
        if (this.paused) {
            return tickDelta;
        }
        ClientWorld world = this.world;
        if (world != null) {
            TimeDeacceleratable t = (TimeDeacceleratable) world;
            if (1.0 <= t.getTimeFactor()) {
                return tickDelta;
            }

            double factor = ticksDelta;

            return (float) (deltaStack + tickDelta * factor);
        }
        return tickDelta;
    }

}
