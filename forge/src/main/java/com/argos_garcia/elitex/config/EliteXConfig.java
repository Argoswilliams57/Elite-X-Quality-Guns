package com.argos_garcia.elitex.config;

import com.argos_garcia.elitex.EliteXQualityGuns;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

/**
 * Common configuration of Elite X Quality Guns (Forge).
 *
 * <p>Registered in the mod constructor as {@code ModConfig.Type.COMMON}, so the values live in
 * {@code config/elitex-common.toml}. The Fabric build declares the identical spec through
 * Forge Config API Port, which keeps the file name, layout and option names the same on both
 * loaders.</p>
 */
public final class EliteXConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.BooleanValue ALLOW_AIMING_DURING_RELOAD;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment("Gameplay options").push("gameplay");
        ALLOW_AIMING_DURING_RELOAD = builder
                .comment("Default for aiming (ADS) while a gun is reloading.",
                        "Only used by guns whose gun JSON has no \"allow_aim_during_reload\" entry.",
                        "true: such guns allow aiming during reload (default).",
                        "false: such guns prohibit aiming during reload.",
                        "An explicit allow_aim_during_reload in the gun JSON always overrides this.")
                .define("allowAimingDuringReload", true);
        builder.pop();
        SPEC = builder.build();
    }

    /** Live COMMON config instance, captured from {@code ModConfigEvent} so the screen can save. */
    private static ModConfig config;

    private EliteXConfig() {
    }

    /**
     * Remembers the live config instance. Called for MOD-bus {@code ModConfigEvent.Loading} and
     * {@code ModConfigEvent.Reloading}; only the mod's COMMON config is kept.
     */
    public static void onConfigLoaded(ModConfig modConfig) {
        if (modConfig.getType() == ModConfig.Type.COMMON
                && EliteXQualityGuns.MOD_ID.equals(modConfig.getModId())) {
            config = modConfig;
        }
    }

    /**
     * Writes the gameplay option through the real spec value and persists the COMMON config file
     * ({@code config/elitex-common.toml}). The new value is visible to {@link #allowAimingDuringReload()}
     * immediately, so no restart is required.
     */
    public static void setAllowAimingDuringReload(boolean value) {
        ALLOW_AIMING_DURING_RELOAD.set(value);
        ModConfig modConfig = config;
        if (modConfig != null) {
            modConfig.save();
        }
    }

    public static boolean allowAimingDuringReload() {
        return ALLOW_AIMING_DURING_RELOAD.get();
    }
}
