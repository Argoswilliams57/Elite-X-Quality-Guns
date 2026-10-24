package com.argos_garcia.elitex.fabric.mixin;

import com.argos_garcia.elitex.fabric.gun.AimingDuringReloadControl;
import com.google.gson.JsonElement;
import com.tacz.guns.resource.manager.JsonDataManager;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Captures the per-gun {@code allow_aim_during_reload} flag while TaCZ parses gun data
 * from a gun pack ({@code data/<namespace>/data/guns/<gun>_data.json}).
 *
 * <p>{@code JsonDataManager#parseJson} is the single funnel used by
 * {@code CommonDataManager}, which is what loads {@code GunData} on both the client and the
 * server, so the flag is known before any shot can be fired with the gun.</p>
 */
@Mixin(JsonDataManager.class)
public abstract class JsonDataManagerMixin {

    @Inject(method = "parseJson", at = @At("RETURN"), remap = false)
    private void elitex$captureAimingDuringReload(JsonElement element, CallbackInfoReturnable<Object> cir) {
        if (cir.getReturnValue() instanceof GunData gunData) {
            AimingDuringReloadControl.capture(gunData, element);
        }
    }
}
