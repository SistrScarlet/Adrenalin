package net.adrenalin.setup;

import dev.architectury.event.events.client.ClientTickEvent;
import net.adrenalin.key.AdrenalinKeyBindings;

public class ClientSetup {
    public static void init() {
        AdrenalinKeyBindings.init();
        ClientTickEvent.CLIENT_PRE.register(c -> AdrenalinKeyBindings.tick());
    }
}
