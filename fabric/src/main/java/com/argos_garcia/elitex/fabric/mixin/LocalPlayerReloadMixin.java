package com.argos_garcia.elitex.fabric.mixin;

import com.argos_garcia.elitex.fabric.gun.AimingDuringReloadControl;
import com.tacz.guns.api.client.gameplay.IClientPlayerGunOperator;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.client.gameplay.LocalPlayerReload;
import com.tacz.guns.client.resource.GunDisplayInstance;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Cancels an active ADS when a reload starts for a gun that forbids aiming during reload.
 *
 * <p>TaCZ 26.3 replaced the old {@code doReload} hook with {@code reload()} +
 * {@code reloadWithDisplay}. {@code triggerClientReloadAnimation} is the point of
 * {@code reloadWithDisplay} that runs only after every precondition passed (ammo check, state
 * lock, shoot cool-down, {@code GunReloadEvent}), i.e. exactly when the reload really starts
 * and the reload packet has already been sent. Calling the operator's {@code aim(false)} also
 * notifies the server, so both sides leave ADS together.</p>
 */
@Mixin(LocalPlayerReload.class)
public abstract class LocalPlayerReloadMixin {

    @Shadow(remap = false)
    @Final
    private LocalPlayer player;

    @Inject(method = "triggerClientReloadAnimation", at = @At("HEAD"), remap = false)
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
