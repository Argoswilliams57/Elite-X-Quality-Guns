package com.argos_garcia.elitex.fabric.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

/**
 * Optional Mod Menu integration. Only loaded by Mod Menu itself (fabric.mod.json "modmenu"
 * entrypoint), so Mod Menu stays an optional dependency.
 */
public class EliteXModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> new EliteXConfigScreen(parent);
    }
}
