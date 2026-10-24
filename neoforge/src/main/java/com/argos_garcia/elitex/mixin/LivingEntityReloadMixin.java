package com.argos_garcia.elitex.mixin;

import com.argos_garcia.elitex.gun.AimingDuringReloadControl;
import com.tacz.guns.entity.shooter.LivingEntityReload;
import com.tacz.guns.entity.shooter.ShooterDataHolder;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


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
