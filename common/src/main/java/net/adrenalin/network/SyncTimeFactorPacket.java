package net.adrenalin.network;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.adrenalin.AdrenalinMod;
import net.adrenalin.config.AdrenalinConfig;
import net.adrenalin.util.TimeDeacceleratable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

public class SyncTimeFactorPacket {
    public static final Identifier ID = new Identifier(AdrenalinMod.MOD_ID, "sync_time_factor");

    public static void sendS2C(ServerWorld world) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeDouble(((TimeDeacceleratable) world).getTimeFactor());
        NetworkManager.sendToPlayers(world.getPlayers(), ID, buf);
    }

    public static void receiveS2C(PacketByteBuf packetByteBuf, NetworkManager.PacketContext packetContext) {
        double factor = packetByteBuf.readDouble();
        packetContext.queue(() -> applyS2C(factor));
    }

    private static void applyS2C(double factor) {
        if (!AdrenalinConfig.getINSTANCE().isEnable()) {
            return;
        }
        ClientWorld world = MinecraftClient.getInstance().world;
        if (world instanceof TimeDeacceleratable t) {
            t.setTimeFactor(factor);
        }
    }
}
