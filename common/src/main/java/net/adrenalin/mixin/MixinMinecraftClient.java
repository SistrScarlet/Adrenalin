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

    @Shadow
    public abstract void tick();

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void onTick(CallbackInfo ci) {
        ClientWorld world = this.world;
        if (world instanceof TimeDeacceleratable t) {
            t.setTimeDelta(t.getTimeDelta() + t.getTimeFactor());
            if (t.getTimeDelta() < 1.0f) {
                ci.cancel();
                return;
            }
            t.setTimeDelta(t.getTimeDelta() - 1.0f);
        }
    }

    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;render(FJZ)V"))
    private float modifyRender(float tickDelta) {
        if (this.paused) {
            return tickDelta;
        }
        ClientWorld world = this.world;
        if (world instanceof TimeDeacceleratable t) {
            float delta = t.getTimeDelta();
            float factor = t.getTimeFactor();
            //DeltaをFactorで割った整数
            return Math.min(MathHelper.floor(delta / factor) * factor + tickDelta * factor, 1.0f);
        }
        return tickDelta;
    }

}
