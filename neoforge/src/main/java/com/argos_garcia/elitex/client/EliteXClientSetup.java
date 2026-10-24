package com.argos_garcia.elitex.client;

import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;


public final class EliteXClientSetup {

    private EliteXClientSetup() {
    }


    public static void registerConfigScreen(ModContainer modContainer) {
        modContainer.registerExtensionPoint(IConfigScreenFactory.class,
                (container, parent) -> new EliteXConfigScreen(parent));
    }
}
