package net.adrenalin.mixin;

import com.mojang.authlib.GameProfile;
import net.adrenalin.config.AdrenalinConfig;
import net.adrenalin.network.SyncTimeFactorPacket;
import net.adrenalin.util.HasAdrenalin;
import net.adrenalin.util.TimeDeacceleratable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class MixinServerPlayerEntity extends PlayerEntity implements HasAdrenalin {
    @Shadow
    public abstract void addCritParticles(Entity target);

    @Shadow
    public abstract void readCustomDataFromNbt(NbtCompound nbt);

    private double prevAdrenalin;
    private double adrenalin;

    @Override
    public double getAdrenalin() {
        return this.adrenalin;
    }

    @Override
    public void setAdrenalin(double adrenalin) {
        this.adrenalin = adrenalin;
    }

    private double prevHealthPercent = 1.0;
    private double healthPercentStack;

    public MixinServerPlayerEntity(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }

    @Inject(method = "tick", at = @At("RETURN"))
    private void onTick(CallbackInfo ci) {
        if (!AdrenalinConfig.getINSTANCE().isEnable()) {
            resetAdrenalin();
            return;
        }

        //adrenalinを減衰させる
        adrenalin = Math.max(0.0, adrenalin - 1.0 / (AdrenalinConfig.getINSTANCE().getAdrenalinDecay()));
        double heathPercent = getHealth() / getMaxHealth();
        double lowHealthThreshold = AdrenalinConfig.getINSTANCE().getLowHealthThreshold();
        //攻撃を食らい、かつ体力が低い場合にアドレナリンが吹き出す
        if (heathPercent < this.prevHealthPercent
                && heathPercent < lowHealthThreshold) {
            //lowLimitを下限、lowHealthThresholdを上限としたhealthPercent
            double lowLimit = AdrenalinConfig.getINSTANCE().getLowHealthLowLimit();
            double percent = (heathPercent - lowLimit) / lowHealthThreshold * (1.0 - lowLimit);
            adrenalin = Math.max(adrenalin, 1.0 - Math.max(0.0, percent));
        }
        //healthPercentStackを減衰させる
        healthPercentStack = Math.max(0, healthPercentStack - 1.0 / AdrenalinConfig.getINSTANCE().getContinuousDamageDecay());
        //このtickで食らったダメージを加算
        healthPercentStack = MathHelper.clamp(healthPercentStack + (prevHealthPercent - heathPercent), 0.0f, 1.0);
        //ダメージを食らい続けた場合にアドレナリンが吹き出す
        if (AdrenalinConfig.getINSTANCE().getContinuousDamageThreshold() < healthPercentStack) {
            double lowLimit = AdrenalinConfig.getINSTANCE().getContinuousDamageLowLimit();
            double maxLimit = AdrenalinConfig.getINSTANCE().getContinuousDamageMaxLimit();
            //lowLimitを0とし、最大値を1としたhealthPercentStack
            double percent = (healthPercentStack - lowLimit) / (maxLimit - lowLimit);
            adrenalin = Math.max(adrenalin, Math.min(percent, 1.0));
        }
        TimeDeacceleratable t = ((TimeDeacceleratable) this.world);
        if (prevAdrenalin != adrenalin) {
            double nowFactor = t.getTimeFactor();
            double newFactor = MathHelper.lerp(adrenalin, 1.0, AdrenalinConfig.getINSTANCE().getMaxTimeStasisFactor());
            if (nowFactor != newFactor) {
                ((TimeDeacceleratable) this.world).setTimeFactor(newFactor);
                SyncTimeFactorPacket.sendS2C((ServerWorld) this.world);
            }
        }

        this.prevAdrenalin = adrenalin;
        this.prevHealthPercent = heathPercent;
    }

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void onOnDeath(DamageSource source, CallbackInfo ci) {
        if (!AdrenalinConfig.getINSTANCE().isEnable()) {
            return;
        }
        resetAdrenalin();
    }

    private void resetAdrenalin() {
        TimeDeacceleratable t = ((TimeDeacceleratable) this.world);
        if (1.0 != t.getTimeFactor()) {
            ((TimeDeacceleratable) this.world).setTimeFactor(1.0);
            SyncTimeFactorPacket.sendS2C((ServerWorld) this.world);
        }
    }

}
