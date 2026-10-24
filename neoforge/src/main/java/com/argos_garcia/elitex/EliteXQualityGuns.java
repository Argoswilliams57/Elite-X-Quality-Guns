package com.argos_garcia.elitex;


import com.argos_garcia.elitex.client.EliteXClientSetup;
import com.argos_garcia.elitex.config.EliteXConfig;
import com.argos_garcia.registeration_center.EliteXCreativeTabRegistry;
import com.argos_garcia.registeration_center.ElitexItemRegistry;
import com.tacz.guns.api.resource.ResourceManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(EliteXQualityGuns.MOD_ID)
public class EliteXQualityGuns {
    public static final String MOD_ID = "elitex";
    public static final Logger LOGGER = LogManager.getLogger("elitex");
    public static final String DEFAULT_GUN_PACK_NAME = "elitex_quality_guns";

    public EliteXQualityGuns(IEventBus modEventBus, ModContainer modContainer) {


        // Common config (config/elitex-common.toml). 26.3 renamed the config types: LOCAL is the
        // per-instance config that used to be COMMON. The explicit file name keeps the existing
        // config/elitex-common.toml path.
        modContainer.registerConfig(ModConfig.Type.LOCAL, EliteXConfig.SPEC, "elitex-common");

        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            EliteXClientSetup.registerConfigScreen(modContainer);
        }

        EliteXCreativeTabRegistry.CREATIVE_MODE_TABS.register(modEventBus);
        ElitexItemRegistry.ITEMS.register(modEventBus);
        registerDefaultExtraGunPack();
    }

    private static void registerDefaultExtraGunPack() {
        String jarDefaultPackPath = String.format("/assets/%s/custom/%s", "elitex", "elitex_quality_guns");
        ResourceManager.registerExportResource(EliteXQualityGuns.class, jarDefaultPackPath);
    }
}
