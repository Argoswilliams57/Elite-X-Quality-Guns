package com.argos_garcia.elitex.mixin;

import com.argos_garcia.elitex.gun.AimingDuringReloadControl;
import com.tacz.guns.resource.network.CommonNetworkCache;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(CommonNetworkCache.class)
public abstract class CommonNetworkCacheMixin {

    @Inject(method = "parse", at = @At("RETURN"), remap = false)
    private void elitex$captureAimingDuringReload(String json, Class<?> dataClass, CallbackInfoReturnable<Object> cir) {
        if (cir.getReturnValue() instanceof GunData gunData) {
            AimingDuringReloadControl.capture(gunData, json);
        }
    }
}
