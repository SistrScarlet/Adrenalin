package net.adrenalin;

import net.adrenalin.config.AdrenalinConfig;

public class AdrenalinMod {
    public static final String MOD_ID = "adrenalin";

    public static void init() {
        AdrenalinConfig.getINSTANCE().load();
        AdrenalinConfig.getINSTANCE().save();
    }
}
