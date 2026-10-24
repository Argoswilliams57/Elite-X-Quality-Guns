package com.argos_garcia.elitex.config;

import com.argos_garcia.elitex.EliteXQualityGuns;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

/**
 * Keeps {@link EliteXConfig} pointed at the live COMMON {@code ModConfig} so the in-game
 * configuration screen can persist its changes through the normal config lifecycle.
 */
@Mod.EventBusSubscriber(modid = EliteXQualityGuns.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class EliteXConfigEvents {

    private EliteXConfigEvents() {
    }

    @SubscribeEvent
    public static void onConfigLoading(ModConfigEvent.Loading event) {
        EliteXConfig.onConfigLoaded(event.getConfig());
    }

    @SubscribeEvent
    public static void onConfigReloading(ModConfigEvent.Reloading event) {
        EliteXConfig.onConfigLoaded(event.getConfig());
    }
}
