package com.argos_garcia.elitex;


import com.argos_garcia.elitex.client.EliteXConfigScreen;
import com.argos_garcia.elitex.config.EliteXConfig;
import com.argos_garcia.registeration_center.EliteXCreativeTabRegistry;
import com.argos_garcia.registeration_center.ElitexItemRegistry;
import com.tacz.guns.api.resource.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod("elitex")
public class EliteXQualityGuns {
    public static final String MOD_ID = "elitex";
    public static final Logger LOGGER = LogManager.getLogger("elitex");
    public static final String DEFAULT_GUN_PACK_NAME = "elitex_quality_guns";

    public EliteXQualityGuns() {

        // Common config (config/elitex-common.toml), shared with the Fabric build's spec.
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, EliteXConfig.SPEC);

        Dist side = FMLLoader.getDist();
        if (side.isClient()) {
            // Adds the config button to Forge's Mods screen (client-only extension point).
            ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                    () -> new ConfigScreenHandler.ConfigScreenFactory(
                            (minecraft, parent) -> new EliteXConfigScreen(parent)));
        }

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        EliteXCreativeTabRegistry.CREATIVE_MODE_TABS.register(modEventBus);
        ElitexItemRegistry.ITEMS.register(modEventBus);
        registerDefaultExtraGunPack();

    }

    private static void registerDefaultExtraGunPack() {
        String jarDefaultPackPath = String.format("/assets/%s/custom/%s", "elitex", "elitex_quality_guns");
        ResourceManager.registerExportResource(EliteXQualityGuns.class, jarDefaultPackPath);
    }
}
