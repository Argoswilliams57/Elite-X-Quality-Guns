package com.argos_garcia.elitex.fabric.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

/**
 * Common configuration of Elite X Quality Guns (Fabric).
 *
 * <p>Built with {@code ForgeConfigSpec} from Forge Config API Port, which TaCZ: Refabricated
 * already requires, so the file name ({@code config/elitex-common.toml}), the TOML layout and
 * the option names are identical to the Forge build of this mod.</p>
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

    /** Live COMMON config instance returned by Forge Config API Port when registering the spec. */
    private static ModConfig config;

    private EliteXConfig() {
    }

    /**
     * Remembers the config instance created by {@code ForgeConfigRegistry}, so the in-game
     * configuration screen can persist changes to {@code config/elitex-common.toml}.
     */
    public static void setConfig(ModConfig modConfig) {
        config = modConfig;
    }

    /**
     * Writes the gameplay option through the real spec value and saves the COMMON config file.
     * The new value is visible to {@link #allowAimingDuringReload()} immediately, so no restart
     * is required.
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
