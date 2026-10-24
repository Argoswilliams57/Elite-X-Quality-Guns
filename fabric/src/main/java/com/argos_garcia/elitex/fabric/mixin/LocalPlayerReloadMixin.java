package com.argos_garcia.elitex.fabric.mixin;

import com.argos_garcia.elitex.fabric.gun.AimingDuringReloadControl;
import com.tacz.guns.api.client.gameplay.IClientPlayerGunOperator;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.client.gameplay.LocalPlayerReload;
import com.tacz.guns.client.resource.GunDisplayInstance;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Cancels an active ADS when a reload starts for a gun that forbids aiming during reload.
 *
 * <p>{@code LocalPlayerReload#doReload} runs only after every reload precondition passed
 * (ammo check, state lock, {@code GunReloadEvent}), i.e. exactly when the reload really starts,
 * and the reload packet has already been sent at that point. Calling the operator's
 * {@code aim(false)} also notifies the server, so both sides leave ADS together.</p>
 */
@Mixin(LocalPlayerReload.class)
public abstract class LocalPlayerReloadMixin {

    @Shadow(remap = false)
    @Final
    private ClientPlayerEntity player;

    @Inject(method = "doReload", at = @At("HEAD"), remap = false)
    private void elitex$cancelAimOnReloadStart(IGun gun, GunDisplayInstance display, GunData gunData,
                                               ItemStack stack, CallbackInfo ci) {
        if (AimingDuringReloadControl.isAllowed(gunData)) {
            return;
        }
        IClientPlayerGunOperator operator = IClientPlayerGunOperator.fromLocalPlayer(this.player);
        if (operator.isAim()) {
            operator.aim(false);
        }
    }
}
