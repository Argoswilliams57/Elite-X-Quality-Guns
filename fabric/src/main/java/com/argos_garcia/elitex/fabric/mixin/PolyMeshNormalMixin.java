package com.argos_garcia.elitex.fabric.mixin;

import cn.sh1rocu.tacz.compat.meshloader.core.PolyMesh;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Normalises the pose-transformed poly_mesh normal before it is written to the vertex.
 *
 * <p><b>Why this exists.</b> {@code PolyMesh#compile} (the collector path used by every poly_mesh
 * attachment, and by the weapon whenever GPU baking is off or a shader-pack route is taken) applies
 * {@code PoseStack.Pose#normal()} by hand:</p>
 *
 * <pre>{@code
 * tnx = n00 * nx + n10 * ny + n20 * nz;   // no normalisation
 * ...
 * consumer.addVertex(...).setNormal(tnx, tny, tnz);
 * }</pre>
 *
 * <p>Vanilla 26.3 does not do that: {@code PoseStack.Pose#transformNormal} returns
 * {@code trustedNormals ? result : result.normalize()} and {@code ModelPart.Cube} goes through it,
 * so vanilla vertices always carry unit normals. TaCZ's GPU-baked path also carries unit normals
 * (baked from normalised face cross products, never transformed). The collector path is the only one
 * that can emit a non-unit normal, and the 26.3 {@code core/entity} shader never renormalises:
 * {@code minecraft_compute_light} is a raw {@code dot(lightDir, normal)} and
 * {@code minecraft_mix_light_separate} clamps {@code (dot0 + dot1) * 0.6 + 0.4} to [0, 1].</p>
 *
 * <p>Any scale in the pose chain is therefore applied to the lighting as a brightness gain:
 * {@code Pose#normal()} is the inverse transpose, so a 0.6 display scale (third person / ground in
 * this pack) multiplies every normal by ~1.67 and saturates bright faces, while a 1.2 scale (fixed /
 * GUI) multiplies by ~0.83 and dims the model, and animation bone scale does the same per frame. That
 * is exactly the "attachment lighting does not match the weapon" symptom: the weapon body is drawn
 * from unit baked normals by {@code PolyMeshGpuRenderer}, the attachment from these scaled ones.</p>
 *
 * <p><b>What this does.</b> Only the {@code setNormal} inside {@code PolyMesh#compile} is wrapped: the
 * vector is normalised when it is not already unit length and handed on unchanged otherwise, so
 * unscaled poses are bit-identical to before. {@code PolyMesh#writeRaw} (the GPU bake) already writes
 * unit normals and is untouched, as are positions, UVs, light and geometry.</p>
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
