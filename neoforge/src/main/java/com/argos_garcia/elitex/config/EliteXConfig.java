package com.argos_garcia.elitex.config;

import net.neoforged.neoforge.common.ModConfigSpec;


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


    public static void setAllowAimingDuringReload(boolean value) {
        ALLOW_AIMING_DURING_RELOAD.set(value);
        // NeoForge (and Forge Config API Port on Fabric) write the live config through the spec.
        SPEC.save();
    }

    public static boolean allowAimingDuringReload() {
        return ALLOW_AIMING_DURING_RELOAD.get();
    }
}
