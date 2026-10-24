package com.argos_garcia.elitex.client;

import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

/**
 * Client-only setup of Elite X Quality Guns (NeoForge).
 *
 * <p>Kept in its own class so the common mod constructor can guard the call with
 * {@link net.neoforged.fml.loading.FMLEnvironment#dist} without ever loading
 * {@link IConfigScreenFactory} (a client-only interface) on a dedicated server.</p>
 */
public final class EliteXClientSetup {

    private EliteXClientSetup() {
    }

    /**
     * Registers the Elite X configuration screen for the Mods screen (also used by
     * mod list mods that look up {@code IConfigScreenFactory}).
     */
    public static void registerConfigScreen(ModContainer modContainer) {
        modContainer.registerExtensionPoint(IConfigScreenFactory.class,
                (container, parent) -> new EliteXConfigScreen(parent));
    }
}
