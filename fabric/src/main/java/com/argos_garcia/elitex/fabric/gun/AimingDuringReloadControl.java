package com.argos_garcia.elitex.fabric.gun;

import com.argos_garcia.elitex.fabric.config.EliteXConfig;
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
import net.minecraft.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Evaluates whether a gun may still be aimed (ADS) while it is reloading.
 *
 * <p>TaCZ's gun JSON is deserialized into fixed POJOs, so the custom top-level property
 * {@value #PROPERTY} is not kept by {@link GunData}. The value is captured while TaCZ parses
 * the gun data (see the {@code JsonDataManager} and {@code CommonNetworkCache} mixins) and
 * remembered per parsed {@link GunData} instance. TaCZ keeps those instances for the lifetime
 * of a resource reload, and every lookup ({@code CommonGunIndex#getGunData()},
 * {@code ClientGunIndex#getGunData()}) returns the same instance.</p>
 *
 * <p>Precedence: an explicit gun JSON value always wins; the global config is only the
 * default for guns that do not set the property.</p>
 *
 * <pre>
 * gun property present -&gt; that value
 * gun property absent  -&gt; EliteXConfig.allowAimingDuringReload()
 * </pre>
 *
 * <p>Backward compatibility: {@value #LEGACY_PROPERTY} is still honoured as an alias. If a gun
 * sets both keys, the canonical {@value #PROPERTY} wins; if the canonical key is present but not
 * a boolean, the alias is used when it is a boolean.</p>
 */
public final class AimingDuringReloadControl {
    /** Canonical per-gun JSON property name. */
    public static final String PROPERTY = "allow_aim_during_reload";

    /** Previous spelling, kept so existing packs that already use it keep working. */
    public static final String LEGACY_PROPERTY = "allow_aiming_during_reload";

    private static final Logger LOGGER = LoggerFactory.getLogger("elitex");

    /**
     * Explicit per-gun values only. No entry means "not configured", which falls back to the
     * global option; this keeps an explicit {@code true} distinguishable from a missing key.
     * Keys are the parsed {@link GunData} instances (identity semantics, weakly held).
     */
    private static final Map<GunData, Boolean> GUN_SETTINGS = Collections.synchronizedMap(new WeakHashMap<>());

    private AimingDuringReloadControl() {
    }

    /**
     * Reads the property from the JSON element TaCZ is currently deserializing.
     *
     * @param data    the freshly parsed gun data
     * @param element the raw gun JSON
     */
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

    /**
     * Same as {@link #capture(GunData, JsonElement)} for gun data that arrives as a raw JSON
     * string from the server ({@code CommonNetworkCache}).
     */
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

    /**
     * @return {@code true} when aiming is allowed for this gun; an explicit gun JSON value wins,
     *         otherwise the global option decides
     */
    public static boolean isAllowed(GunData data) {
        if (data != null) {
            Boolean explicit = GUN_SETTINGS.get(data);
            if (explicit != null) {
                return explicit;
            }
        }
        return EliteXConfig.allowAimingDuringReload();
    }

    /**
     * Resolves the gun data of the held stack through TaCZ's common resource provider, which
     * serves locally loaded data on a singleplayer/server side and the synced data on a client
     * connected to a dedicated server.
     */
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
