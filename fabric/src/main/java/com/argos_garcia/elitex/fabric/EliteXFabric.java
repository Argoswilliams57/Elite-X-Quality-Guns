package com.argos_garcia.elitex.fabric;

import com.argos_garcia.elitex.fabric.config.EliteXConfig;
import com.argos_garcia.elitex.fabric.registry.EliteXFabricCreativeTab;
import com.argos_garcia.elitex.fabric.registry.EliteXFabricItems;
import com.tacz.guns.api.resource.ResourceManager;
import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
import net.fabricmc.api.ModInitializer;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EliteXFabric implements ModInitializer {
    public static final String MOD_ID = "elitex";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final String DEFAULT_GUN_PACK_NAME = "elitex_quality_guns";

    @Override
    public void onInitialize() {
        // Common config (config/elitex-common.toml). Forge Config API Port 26.3 keeps the
        // Forge/NeoForge 1.21 config types on Fabric (COMMON/CLIENT/SERVER), so the type and the
        // resulting file name are unchanged. Only the registry entry point moved to
        // fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry and the spec to ForgeConfigSpec.
        ConfigRegistry.INSTANCE.register(MOD_ID, ModConfig.Type.COMMON, EliteXConfig.SPEC);

        EliteXFabricItems.register();
        EliteXFabricCreativeTab.register();
        registerDefaultExtraGunPack();
        LOGGER.info("Elite X Quality Guns (Fabric) initialised, gun pack '{}' registered", DEFAULT_GUN_PACK_NAME);
    }

    private static void registerDefaultExtraGunPack() {
        String jarDefaultPackPath = String.format("/assets/%s/custom/%s", MOD_ID, DEFAULT_GUN_PACK_NAME);
        ResourceManager.registerExportResource(EliteXFabric.class, jarDefaultPackPath);
    }
}
