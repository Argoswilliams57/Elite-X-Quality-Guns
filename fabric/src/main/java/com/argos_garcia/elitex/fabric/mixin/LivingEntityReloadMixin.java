package com.argos_garcia.elitex.fabric.mixin;

import com.argos_garcia.elitex.fabric.gun.AimingDuringReloadControl;
import com.tacz.guns.entity.shooter.LivingEntityReload;
import com.tacz.guns.entity.shooter.ShooterDataHolder;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Server-side counterpart of {@link LocalPlayerReloadMixin}: drops the authoritative aim flag
 * when a reload really started for a gun that forbids aiming during reload.
 *
 * <p>The check runs at the end of {@code LivingEntityReload#reload()} and only when the reload
 * state actually became a reloading state, so failed reload attempts (no ammo, blocked by
 * events, bolting, shoot/draw cool-down) leave aiming untouched. TaCZ syncs aiming progress
 * from this data holder, so clearing {@code isAiming} also stops the synced ADS progress.</p>
 */
@Mixin(LivingEntityReload.class)
public abstract class LivingEntityReloadMixin {

    @Shadow(remap = false)
    @Final
    private ShooterDataHolder data;

    @Inject(method = "reload", at = @At("RETURN"), remap = false)
    private void elitex$cancelAimOnReloadStart(CallbackInfo ci) {
        if (!this.data.reloadStateType.isReloading() || !this.data.isAiming) {
            return;
        }
        ItemStack stack = this.data.currentGunItem == null ? ItemStack.EMPTY : this.data.currentGunItem.get();
        if (!AimingDuringReloadControl.isAllowed(AimingDuringReloadControl.gunDataOf(stack))) {
            this.data.isAiming = false;
        }
    }
}
