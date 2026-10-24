package com.argos_garcia.elitex.fabric.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Common configuration of Elite X Quality Guns (Fabric).
 *
 * <p>Built with {@code ModConfigSpec} from Forge Config API Port, which TaCZ: Refabricated
 * already requires, so the file name ({@code config/elitex-common.toml}), the TOML layout and
 * the option names are identical to the NeoForge build of this mod.</p>
 */
public final class EliteXConfig {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.BooleanValue ALLOW_AIMING_DURING_RELOAD;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
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
     * Writes the gameplay option through the real spec value and saves the COMMON config file.
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
