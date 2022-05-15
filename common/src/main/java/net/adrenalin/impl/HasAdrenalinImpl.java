package net.adrenalin.impl;

import net.adrenalin.util.HasAdrenalin;
import net.adrenalin.config.AdrenalinConfig;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

public class HasAdrenalinImpl implements HasAdrenalin {
    private final LivingEntity entity;
    private float adrenalin;
    private float prevHealthPercent = 1.0f;
    private float healthPercentStack;

    public HasAdrenalinImpl(LivingEntity entity) {
        this.entity = entity;
    }

    @Override
    public void tickAdrenalin() {
        /*if (!AdrenalinConfig.getINSTANCE().isEnable()) {
            resetAdrenalin();
            return;
        }*/

        //adrenalinを減衰させる
        adrenalin = Math.max(0.0f, adrenalin - 1.0f / (AdrenalinConfig.getINSTANCE().getAdrenalinDecay()));
        float heathPercent = entity.getHealth() / entity.getMaxHealth();
        float lowHealthThreshold = AdrenalinConfig.getINSTANCE().getLowHealthThreshold();
        //攻撃を食らい、かつ体力が低い場合にアドレナリンが吹き出す
        if (heathPercent < this.prevHealthPercent
                && heathPercent < lowHealthThreshold) {
            //lowLimitを下限、lowHealthThresholdを上限としたhealthPercent
            float lowLimit = AdrenalinConfig.getINSTANCE().getLowHealthLowLimit();
            float percent = (heathPercent - lowLimit) / lowHealthThreshold * (1.0f - lowLimit);
            adrenalin = Math.max(adrenalin, 1.0f - Math.max(0.0f, percent));
        }
        //healthPercentStackを減衰させる
        healthPercentStack = Math.max(0.0f, healthPercentStack - 1.0f / AdrenalinConfig.getINSTANCE().getContinuousDamageDecay());
        //このtickで食らったダメージを加算
        healthPercentStack = MathHelper.clamp(healthPercentStack + (prevHealthPercent - heathPercent), 0.0f, 1.0f);
        //ダメージを食らい続けた場合にアドレナリンが吹き出す
        if (AdrenalinConfig.getINSTANCE().getContinuousDamageThreshold() < healthPercentStack) {
            float lowLimit = AdrenalinConfig.getINSTANCE().getContinuousDamageLowLimit();
            float maxLimit = AdrenalinConfig.getINSTANCE().getContinuousDamageMaxLimit();
            //lowLimitを0とし、最大値を1としたhealthPercentStack
            float percent = (healthPercentStack - lowLimit) / (maxLimit - lowLimit);
            adrenalin = Math.max(adrenalin, Math.min(percent, 1.0f));
        }
        /*TimeDeacceleratable t = ((TimeDeacceleratable) this.world);
        if (prevAdrenalin != adrenalin) {
            float nowFactor = t.getTimeFactor();
            float newFactor = MathHelper.lerp(adrenalin, 1.0f, AdrenalinConfig.getINSTANCE().getMaxTimeStasisFactor());
            if (nowFactor != newFactor) {
                ((TimeDeacceleratable) this.world).setTimeFactor(newFactor);
                SyncTimeFactorPacket.sendS2C(this.world);
            }
        }

        this.prevAdrenalin = adrenalin;*/
        this.prevHealthPercent = heathPercent;
    }

    /*private void resetAdrenalin() {
        TimeDeacceleratable t = ((TimeDeacceleratable) this.world);
        if (1.0 != t.getTimeFactor()) {
            ((TimeDeacceleratable) this.world).setTimeFactor(1.0f);
            SyncTimeFactorPacket.sendS2C(this.world);
        }
    }*/

    @Override
    public float getAdrenalin() {
        return this.adrenalin;
    }

    @Override
    public void setAdrenalin(float adrenalin) {
        this.adrenalin = adrenalin;
    }
}
