package net.adrenalin;

import net.adrenalin.network.Networking;

public class AdrenalinMod {
    public static final String MOD_ID = "adrenalin";

    public static void init() {
        Networking.init();
    }
}
