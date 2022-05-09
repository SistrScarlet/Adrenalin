package net.adrenalin.network;

import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import net.fabricmc.api.EnvType;

public class Networking {

    public static void init() {
        commonInit();
        if (Platform.getEnv() == EnvType.CLIENT) {
            clientInit();
        }
    }

    private static void commonInit() {

    }

    private static void clientInit() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, SyncTimeFactorPacket.ID, SyncTimeFactorPacket::receiveS2C);
    }

}
