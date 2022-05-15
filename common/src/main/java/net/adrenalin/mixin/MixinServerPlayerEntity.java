package net.adrenalin.mixin;

import com.mojang.authlib.GameProfile;
import net.adrenalin.impl.HasAdrenalinImpl;
import net.adrenalin.util.HasAdrenalin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
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
    public abstract void addEnchantedHitParticles(Entity target);

    //tickはworldにて
    private final HasAdrenalinImpl adrenalin = new HasAdrenalinImpl(this);

    public MixinServerPlayerEntity(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }

    @Override
    public float getAdrenalin() {
        return this.adrenalin.getAdrenalin();
    }

    @Override
    public void setAdrenalin(float adrenalin) {
        this.adrenalin.setAdrenalin(adrenalin);
    }

    @Override
    public void tickAdrenalin() {
        this.adrenalin.tickAdrenalin();
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("RETURN"))
    public void onWriteCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putFloat("adrenalin", this.adrenalin.getAdrenalin());
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("RETURN"))
    public void onReadCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        this.adrenalin.setAdrenalin(nbt.getFloat("adrenalin"));
    }
}
