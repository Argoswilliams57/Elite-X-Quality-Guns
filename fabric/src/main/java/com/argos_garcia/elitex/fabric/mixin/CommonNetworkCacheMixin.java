package com.argos_garcia.elitex.fabric.mixin;

import com.argos_garcia.elitex.fabric.gun.AimingDuringReloadControl;
import com.tacz.guns.resource.network.CommonNetworkCache;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Captures {@code allow_aim_during_reload} for gun data received from a dedicated server.
 *
 * <p>When the client joins a remote server TaCZ throws away its local common assets and
 * deserializes the server's gun data through {@code CommonNetworkCache#parse}, which bypasses
 * {@code JsonDataManager#parseJson}. Both paths have to be covered or the flag would only work
 * in singleplayer.</p>
 */
@Mixin(CommonNetworkCache.class)
public abstract class CommonNetworkCacheMixin {

    @Inject(method = "parse", at = @At("RETURN"), remap = false)
    private void elitex$captureAimingDuringReload(String json, Class<?> dataClass, CallbackInfoReturnable<Object> cir) {
        if (cir.getReturnValue() instanceof GunData gunData) {
            AimingDuringReloadControl.capture(gunData, json);
        }
    }
}
