package net.adrenalin.network;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.adrenalin.AdrenalinMod;
import net.adrenalin.util.HasAdrenalin;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class SparkAdrenalinPacket {
    public static final Identifier ID = new Identifier(AdrenalinMod.MOD_ID, "spark_adrenalin");

    public static void sendC2S() {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        NetworkManager.sendToServer(ID, buf);
    }

    public static void receiveC2S(PacketByteBuf packetByteBuf, NetworkManager.PacketContext packetContext) {
        PlayerEntity player = packetContext.getPlayer();
        packetContext.queue(() -> applyC2S(player));
    }

    private static void applyC2S(PlayerEntity player) {
        ((HasAdrenalin) player).setAdrenalin(1.0f);
    }
}
