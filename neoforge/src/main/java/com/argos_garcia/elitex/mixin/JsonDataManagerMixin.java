package com.argos_garcia.elitex.mixin;

import com.argos_garcia.elitex.gun.AimingDuringReloadControl;
import com.google.gson.JsonElement;
import com.tacz.guns.resource.manager.JsonDataManager;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(JsonDataManager.class)
public abstract class JsonDataManagerMixin {

    @Inject(method = "parseJson", at = @At("RETURN"), remap = false)
    private void elitex$captureAimingDuringReload(JsonElement element, CallbackInfoReturnable<Object> cir) {
        if (cir.getReturnValue() instanceof GunData gunData) {
            AimingDuringReloadControl.capture(gunData, element);
        }
    }
}
