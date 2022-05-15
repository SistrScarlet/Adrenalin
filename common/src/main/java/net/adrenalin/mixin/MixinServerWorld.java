package net.adrenalin.mixin;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.adrenalin.config.AdrenalinConfig;
import net.adrenalin.network.SyncTickIntervalPacket;
import net.adrenalin.util.ChunkTimeStasisDate;
import net.adrenalin.util.HasAdrenalin;
import net.adrenalin.util.TimeDeacceleratable;
import net.adrenalin.util.TimeDeacceleratableImpl;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Comparator;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Mixin(ServerWorld.class)
public abstract class MixinServerWorld extends World implements TimeDeacceleratable {
    private final TimeDeacceleratable timeDeacceleratable = new TimeDeacceleratableImpl();

    protected MixinServerWorld(MutableWorldProperties properties, RegistryKey<World> registryRef, RegistryEntry<DimensionType> registryEntry, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long seed) {
        super(properties, registryRef, registryEntry, profiler, isClient, debugWorld, seed);
    }

    @Override
    public void setTickDelta(float delta) {
        this.timeDeacceleratable.setTickDelta(delta);
    }

    @Override
    public float getTickDelta() {
        return this.timeDeacceleratable.getTickDelta();
    }

    @Override
    public void setTickInterval(int tickInterval) {
        this.timeDeacceleratable.setTickInterval(tickInterval);
    }

    @Override
    public int getTickInterval() {
        return this.timeDeacceleratable.getTickInterval();
    }

    @Shadow
    public abstract List<ServerPlayerEntity> getPlayers();

    @Shadow
    public abstract List<ServerPlayerEntity> getPlayers(Predicate<? super ServerPlayerEntity> predicate);

    private float adrenalin;
    private float prevAdrenalin;

    private final Object2ObjectMap<ChunkPos, ChunkTimeStasisDate> stasisChunks = new Object2ObjectOpenHashMap<>();

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void onTick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        if (!AdrenalinConfig.getINSTANCE().isEnable()) {
            return;
        }
        //とりまtickしとく
        this.getPlayers().forEach(entity -> ((HasAdrenalin) entity).tickAdrenalin());

        if (!AdrenalinConfig.getINSTANCE().isChunkBaseTimeStasis()) {
            //ワールド内で一番アドレナリンが多いやつを取って、そいつ基準で時を合わせる
            this.getPlayers().stream()
                    .max(Comparator.comparingInt(e -> (int) (((HasAdrenalin) e).getAdrenalin() * 100)))
                    .ifPresent(entity -> {
                        this.prevAdrenalin = adrenalin;
                        HasAdrenalin hasAdrenalin = (HasAdrenalin) entity;
                        this.adrenalin = hasAdrenalin.getAdrenalin();
                        if (this.adrenalin != this.prevAdrenalin) {
                            TimeDeacceleratable t = this;
                            float newFactor = MathHelper.lerp(this.adrenalin,
                                    1.0f, AdrenalinConfig.getINSTANCE().getMaxTimeStasisFactor());
                            int integer = MathHelper.ceil(1 / newFactor);
                            t.setTickInterval(integer);
                            SyncTickIntervalPacket.sendS2C(this, this.getPlayers());
                            if (integer == 1) {
                                setTickDelta(0);
                            }
                        }

                        setTickDelta(getTickDelta() + 1.0f / getTickInterval());
                        if (getTickDelta() < 1.0f) {
                            ci.cancel();
                            return;
                        }
                        setTickDelta(getTickDelta() - 1.0f);
                    });
        } else {
            Object2ObjectMap<ChunkPos, ChunkTimeStasisDate> prevStasisChunks
                    = new Object2ObjectOpenHashMap<>(stasisChunks);
            stasisChunks.clear();
            //停滞させるべきチャンクをマークする
            this.getPlayers()
                    .forEach(entity -> {
                        HasAdrenalin hasAdrenalin = (HasAdrenalin) entity;
                        float adrenalin = hasAdrenalin.getAdrenalin();
                        int radius = 4;
                        ChunkPos.stream(entity.getChunkPos(), radius)
                                .filter(c -> !prevStasisChunks.containsKey(c)
                                        || adrenalin < prevStasisChunks.get(c).adrenalin)
                                .forEach(c -> {
                                    ChunkTimeStasisDate date = prevStasisChunks.get(c);
                                    if (date == null) {
                                        date = new ChunkTimeStasisDate();
                                    }
                                    date.prevAdrenalin = date.adrenalin;
                                    date.adrenalin = adrenalin;

                                    if (date.adrenalin != date.prevAdrenalin) {
                                        TimeDeacceleratable t = this;
                                        float newFactor = MathHelper.lerp(this.adrenalin,
                                                1.0f, AdrenalinConfig.getINSTANCE().getMaxTimeStasisFactor());
                                        int integer = MathHelper.ceil(1 / newFactor);
                                        t.setTickInterval(integer);
                                        //対象プレイヤーのチャンクではなく、ワールドを停滞する
                                        //todo チャンクを停滞させる
                                        SyncTickIntervalPacket.sendS2C(this, this.getPlayers(e ->
                                                e.getChunkPos().getChebyshevDistance(entity.getChunkPos()) <= radius));
                                        if (integer == 1) {
                                            setTickDelta(0);
                                        }
                                    }

                                    stasisChunks.put(c, date);
                                });
                    });
        }
    }

    @Inject(method = "shouldTick(Lnet/minecraft/util/math/ChunkPos;)Z", at = @At("HEAD"), cancellable = true)
    public void onShouldTick(ChunkPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (!AdrenalinConfig.getINSTANCE().isEnable()
                || !AdrenalinConfig.getINSTANCE().isChunkBaseTimeStasis()
                || !this.stasisChunks.containsKey(pos)) {
            return;
        }
        TimeDeacceleratable t = this.stasisChunks.get(pos);
        t.setTickDelta(t.getTickDelta() + 1.0f / t.getTickInterval());
        if (t.getTickDelta() < 1.0f) {
            cir.setReturnValue(false);
            return;
        }
        t.setTickDelta(t.getTickDelta() - 1.0f);
    }


}
