package com.argos_garcia.elitex.mixin;

import com.argos_garcia.elitex.gun.AimingDuringReloadControl;
import com.tacz.guns.entity.shooter.LivingEntityAim;
import com.tacz.guns.entity.shooter.ShooterDataHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Server-side counterpart of {@link LocalPlayerAimMixin}: keeps the authoritative aim state
 * off while the shooter reloads a gun whose gun JSON forbids aiming during reload.
 *
 * <p>This is the funnel used for every living entity that is not the local player, including
 * the {@code ServerPlayer} that receives {@code ClientMessagePlayerAim}, so neither another mod
 * nor a modified client can enter ADS during the reload.</p>
 */
@Mixin(LivingEntityAim.class)
public abstract class LivingEntityAimMixin {

    @Shadow(remap = false)
    @Final
    private LivingEntity shooter;

    @Shadow(remap = false)
    @Final
    private ShooterDataHolder data;

    @Inject(method = "aim", at = @At("HEAD"), cancellable = true, remap = false)
    private void elitex$rejectAimDuringReload(boolean isAim, CallbackInfo ci) {
        if (!isAim || !this.data.reloadStateType.isReloading()) {
            return;
        }
        ItemStack stack = this.data.currentGunItem == null ? ItemStack.EMPTY : this.data.currentGunItem.get();
        if (!AimingDuringReloadControl.isAllowed(AimingDuringReloadControl.gunDataOf(stack))) {
            ci.cancel();
        }
    }
}
