package com.argos_garcia.elitex.fabric.config;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Common configuration of Elite X Quality Guns (Fabric).
 *
 * <p>Built with {@code ForgeConfigSpec} from Forge Config API Port 26.3, which TaCZ: Refabricated
 * already requires. 26.3 moved the Fabric config API to the Forge-style spec and the
 * {@code fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry} entry point, but the TOML layout
 * and the option names stay identical to the NeoForge build of this mod.</p>
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

    private EliteXConfig() {
    }

    /**
     * Writes the gameplay option through the real spec value and saves the config file.
     * The new value is visible to {@link #allowAimingDuringReload()} immediately, so no restart
     * is required.
     */
    public static void setAllowAimingDuringReload(boolean value) {
        ALLOW_AIMING_DURING_RELOAD.set(value);
        // Forge Config API Port attaches the live config to the spec, exactly like NeoForge.
        SPEC.save();
    }

    public static boolean allowAimingDuringReload() {
        return ALLOW_AIMING_DURING_RELOAD.get();
    }
}
