package com.argos_garcia.elitex.mixin;

import cn.sh1rocu.tacz.compat.meshloader.core.PolyMesh;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Normalises the pose-transformed poly_mesh normal written by {@code PolyMesh#compile}.
 *
 * <p>TaCZ applies {@code Pose#normal()} by hand without normalising, while vanilla 26.3
 * ({@code PoseStack.Pose#transformNormal}) and TaCZ's own GPU-baked path both emit unit normals. The
 * {@code core/entity} shader does not renormalise and clamps {@code (dot0 + dot1) * 0.6 + 0.4}, so any
 * scale in the pose (0.6 third person / 1.2 fixed display scale in this pack, animation bone scale)
 * becomes a brightness gain on attachment geometry and makes it disagree with the weapon body, which
 * is drawn from unit baked normals by {@code PolyMeshGpuRenderer}. Unscaled poses stay bit-identical.</p>
 */
@Mixin(PolyMesh.class)
public abstract class PolyMeshNormalMixin {

    @Redirect(method = "compile", remap = false,
            at = @At(value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/VertexConsumer;setNormal(FFF)Lcom/mojang/blaze3d/vertex/VertexConsumer;"))
    private static VertexConsumer elitex$normalizeShadingNormal(VertexConsumer consumer, float x, float y, float z) {
        float lengthSquared = x * x + y * y + z * z;
        if (lengthSquared > 1.0E-6F && Math.abs(lengthSquared - 1.0F) > 1.0E-4F) {
            float length = (float) Math.sqrt(lengthSquared);
            x /= length;
            y /= length;
            z /= length;
        }
        return consumer.setNormal(x, y, z);
    }
}
