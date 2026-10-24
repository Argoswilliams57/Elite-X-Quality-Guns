package com.argos_garcia.elitex.fabric.client;

import cn.sh1rocu.simplebedrockmodel.api.event.ViewportEvent;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// This class fixes stupid fabric camera animation bug which tacz had
public final class EliteXCameraAnimation implements ClientModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger("elitex");


    private static volatile float roll;

    @Override
    public void onInitializeClient() {
        try {
            // Lowest phase, so every other camera listener (including TaCZ's animation and
            // recoil handlers) has already contributed to the event values.
            ViewportEvent.CAMERA.register(ViewportEvent.LOWEST, event -> roll = event.getRoll());
        } catch (Throwable throwable) {
            // SimpleBedrockModel ships Jar-in-Jar with TaCZ: Refabricated. If a future version
            // drops its camera event, keep the client usable without roll animation instead of
            // failing during mod initialisation.
            LOGGER.warn("TaCZ camera roll hook unavailable, camera roll animations stay disabled", throwable);
        }
    }

    /**
     * @return the roll (in degrees) the camera animation asked for during the current frame
     */
    public static float getRoll() {
        return roll;
    }
}
