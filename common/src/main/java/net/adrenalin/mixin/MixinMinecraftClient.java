package net.adrenalin.mixin;

import net.adrenalin.config.AdrenalinConfig;
import net.adrenalin.network.SyncTickIntervalPacket;
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

    //違いが大きいので、サーバー側で使ってるTimeDeacceleratableは使わない
    private int tickInterval = 1;
    private float tickDelta;
    private float renderTickStack;
    private float toNextTickSpeed = 1.0f;

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void onTick(CallbackInfo ci) {
        if (!AdrenalinConfig.getINSTANCE().isEnable()) {
            return;
        }

        if (this.world == null) {
            return;
        }

        //tickスキップ処理
        //tickのつなぎ目でサーバーからTimeFactorの更新が発生するため、小数点以下のtickは切り捨てでよい
        tickDelta += 1.0f / tickInterval;
        renderTickStack += toNextTickSpeed;
        if (tickDelta < 1.0f) {
            ci.cancel();
        } else {
            tickDelta -= 1.0f;
            renderTickStack = 0;
            //のこり距離 / あと何tickで = 速度
            toNextTickSpeed = (1.0f - renderTickStack) / MathHelper.ceil(tickInterval * (1 - tickDelta));
        }

        //パケットを受け取って、値が書き換わるタイミングが重要なので、こうする
        int packetTickInterval = SyncTickIntervalPacket.clientTickInterval.get();
        if (packetTickInterval != -1) {
            SyncTickIntervalPacket.clientTickInterval.set(-1);
            tickInterval = packetTickInterval;

            //停滞状態じゃないならリセット
            if (tickInterval == 1) {
                tickDelta = 0.0f;
                renderTickStack = 0.0f;
            } else {
                toNextTickSpeed = (1.0f - renderTickStack) / MathHelper.ceil(tickInterval * (1 - tickDelta));
            }
        }

    }

    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;render(FJZ)V"))
    private float modifyRender(float tickDelta) {
        if (!AdrenalinConfig.getINSTANCE().isEnable() || this.paused || this.world == null) {
            return tickDelta;
        }

        //停滞状態じゃないならスルー
        if (tickInterval <= 1) {
            return tickDelta;
        }

        return renderTickStack + toNextTickSpeed * tickDelta;
    }

}
