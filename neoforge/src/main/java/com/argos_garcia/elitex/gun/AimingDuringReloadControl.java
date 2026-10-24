package com.argos_garcia.elitex.gun;

import com.argos_garcia.elitex.config.EliteXConfig;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.resource.index.CommonGunIndex;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class AimingDuringReloadControl {
    /** Canonical per-gun JSON property name. */
    public static final String PROPERTY = "allow_aim_during_reload";

    /** Previous spelling, kept so existing packs that already use it keep working. */
    public static final String LEGACY_PROPERTY = "allow_aiming_during_reload";

    private static final Logger LOGGER = LoggerFactory.getLogger("elitex");


    private static final Map<GunData, Boolean> GUN_SETTINGS = Collections.synchronizedMap(new WeakHashMap<>());

    private AimingDuringReloadControl() {
    }


    public static void capture(GunData data, JsonElement element) {
        if (data == null || element == null || !element.isJsonObject()) {
            return;
        }
        JsonObject object = element.getAsJsonObject();
        JsonElement canonical = object.get(PROPERTY);
        JsonElement legacy = object.get(LEGACY_PROPERTY);

        JsonElement value = null;
        if (isBoolean(canonical)) {
            value = canonical;
        } else if (isBoolean(legacy)) {
            value = legacy;
        }

        if (value == null) {
            if (canonical != null || legacy != null) {
                LOGGER.warn("Ignoring non-boolean {} in gun data, the global setting is used instead",
                        canonical != null ? PROPERTY : LEGACY_PROPERTY);
            }
            // No explicit setting: fall back to the global config value.
            GUN_SETTINGS.remove(data);
            return;
        }
        GUN_SETTINGS.put(data, value.getAsBoolean());
    }


    public static void capture(GunData data, String json) {
        if (data == null || json == null) {
            return;
        }
        try {
            capture(data, JsonParser.parseString(json));
        } catch (JsonParseException exception) {
            LOGGER.warn("Could not read {} from synced gun data", PROPERTY, exception);
        }
    }

    private static boolean isBoolean(JsonElement element) {
        return element != null && element.isJsonPrimitive() && element.getAsJsonPrimitive().isBoolean();
    }


    public static boolean isAllowed(GunData data) {
        if (data != null) {
            Boolean explicit = GUN_SETTINGS.get(data);
            if (explicit != null) {
                return explicit;
            }
        }
        return EliteXConfig.allowAimingDuringReload();
    }


    public static GunData gunDataOf(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return null;
        }
        IGun gun = IGun.getIGunOrNull(stack);
        if (gun == null) {
            return null;
        }
        return TimelessAPI.getCommonGunIndex(gun.getGunId(stack))
                .map(CommonGunIndex::getGunData)
                .orElse(null);
    }
}
