package com.argos_garcia.elitex.fabric.mixin;

import com.argos_garcia.elitex.fabric.gun.AimingDuringReloadControl;
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

/**
 * Keeps the local player out of ADS while reloading a gun whose effective
 * {@code allow_aim_during_reload} setting is {@code false}.
 *
 * <p>Every client aim path TaCZ has (the aim mouse button, hold-to-aim polling in
 * {@code AimKey#onAimHoldingPreInput}, the controller binding and the zoom key's aim check)
 * ends in {@code IClientPlayerGunOperator#aim(boolean)}, whose only implementation delegates
 * to {@link LocalPlayerAim#aim(boolean)}. Blocking {@code aim(true)} there also prevents the
 * {@code ClientMessagePlayerAim} packet, so the server never sees the aim request.</p>
 *
 * <p>Blocking new attempts is not enough for an ADS that is already active when the reload
 * starts: the reload key and the hold-to-aim polling both run inside the same client tick, and
 * the client only learns that the reload started after the server round trip. TaCZ's polling
 * therefore re-sends {@code aim(true)} in that window and the aim state would survive the whole
 * reload. {@code tickAimingProgress} is called from {@code LocalPlayer#tick} every tick, so it
 * is used to clear a stale ADS as soon as the synced reload state is visible.</p>
 */
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
