package net.adrenalin.network;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.adrenalin.AdrenalinMod;
import net.adrenalin.config.AdrenalinConfig;
import net.adrenalin.util.TimeDeacceleratable;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SyncTickIntervalPacket {
    public static final Identifier ID = new Identifier(AdrenalinMod.MOD_ID, "sync_time_factor");
    public static final AtomicInteger clientTickInterval = new AtomicInteger(-1);

    public static void sendS2C(TimeDeacceleratable t, List<ServerPlayerEntity> target) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeShort(t.getTickInterval());
        NetworkManager.sendToPlayers(target, ID, buf);
    }

    public static void receiveS2C(PacketByteBuf packetByteBuf, NetworkManager.PacketContext packetContext) {
        int tickInterval = packetByteBuf.readShort();
        applyS2CAsync(tickInterval);
    }

    private static void applyS2CAsync(int tickInterval) {
        if (!AdrenalinConfig.getINSTANCE().isEnable()) {
            return;
        }
        clientTickInterval.set(tickInterval);
    }
}
