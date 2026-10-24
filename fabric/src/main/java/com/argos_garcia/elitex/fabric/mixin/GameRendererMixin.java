package com.argos_garcia.elitex.fabric.mixin;

import com.argos_garcia.elitex.fabric.client.EliteXCameraAnimation;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Inject(
            method = "renderWorld",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;getPitch()F", ordinal = 0)
    )
    private void elitex$applyCameraAnimationRoll(float tickDelta, long limitTime, MatrixStack matrices,
                                                CallbackInfo ci) {
        float roll = EliteXCameraAnimation.getRoll();
        if (roll != 0.0F) {
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(roll));
        }
    }
}
