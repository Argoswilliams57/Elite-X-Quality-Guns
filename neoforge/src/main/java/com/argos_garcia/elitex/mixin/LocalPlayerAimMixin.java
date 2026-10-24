package com.argos_garcia.elitex.mixin;

import com.argos_garcia.elitex.gun.AimingDuringReloadControl;
import com.tacz.guns.api.client.gameplay.IClientPlayerGunOperator;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.client.gameplay.LocalPlayerAim;
import com.tacz.guns.client.gameplay.LocalPlayerDataHolder;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(LocalPlayerAim.class)
public abstract class LocalPlayerAimMixin {

    @Shadow(remap = false)
    @Final
    private LocalPlayer player;

    @Shadow(remap = false)
    @Final
    private LocalPlayerDataHolder data;

    @Inject(method = "aim", at = @At("HEAD"), cancellable = true, remap = false)
    private void elitex$rejectAimDuringReload(boolean isAim, CallbackInfo ci) {
        if (!isAim) {
            return;
        }
        IGunOperator operator = IGunOperator.fromLivingEntity(this.player);
        if (!operator.getSynReloadState().getStateType().isReloading()) {
            return;
        }
        if (!AimingDuringReloadControl.isAllowed(AimingDuringReloadControl.gunDataOf(this.player.getMainHandItem()))) {
            ci.cancel();
        }
    }

    @Inject(method = "tickAimingProgress", at = @At("HEAD"), remap = false)
    private void elitex$cancelAimDuringReload(CallbackInfo ci) {
        if (!this.data.clientIsAiming) {
            return;
        }
        IGunOperator operator = IGunOperator.fromLivingEntity(this.player);
        if (!operator.getSynReloadState().getStateType().isReloading()) {
            return;
        }
        if (AimingDuringReloadControl.isAllowed(AimingDuringReloadControl.gunDataOf(this.player.getMainHandItem()))) {
            return;
        }
        // Drop the local ADS state and tell the server, just like TaCZ's own aim(false) path.
        this.data.clientIsAiming = false;
        IClientPlayerGunOperator.fromLocalPlayer(this.player).aim(false);
    }
}
