package net.adrenalin.mixin;

import com.mojang.authlib.GameProfile;
import net.adrenalin.network.SyncTimeFactorPacket;
import net.adrenalin.util.TimeDeacceleratable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class MixinServerPlayerEntity extends PlayerEntity {
    private float adrenalin;
    private float maxFactor = 0.5f;
    private float prevHealthPercent = 1.0f;
    private float healthPercentStack;

    public MixinServerPlayerEntity(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void onTick(CallbackInfo ci) {
        adrenalin = Math.max(0.0f, adrenalin - 1.0f / (20 * 5));//5秒間で0に
        float heathPercent = Math.min(1.0f, getHealth() / getMaxHealth());
        if (heathPercent < 0.5f) {
            adrenalin = Math.max(adrenalin, 1.0f - heathPercent * 2f);
        }
        //割合減少のがいいか？
        healthPercentStack -= 1.0f / (20 * 5);//5秒間で0に
        healthPercentStack += prevHealthPercent - heathPercent;
        healthPercentStack = MathHelper.clamp(healthPercentStack, 0.0f, 1.0f);
        if (0.25f < healthPercentStack) {
            adrenalin = Math.max(adrenalin, Math.min(healthPercentStack * 2.0f, 1.0f));
        }
        TimeDeacceleratable t = ((TimeDeacceleratable) this.world);
        float nowFactor = t.getTimeFactor();
        float newFactor = MathHelper.lerp(adrenalin, 1.0f, maxFactor);
        if (nowFactor != newFactor) {
            ((TimeDeacceleratable) this.world).setTimeFactor(newFactor);
            SyncTimeFactorPacket.sendS2C((ServerWorld) this.world);
        }

        this.prevHealthPercent = heathPercent;
    }

}
