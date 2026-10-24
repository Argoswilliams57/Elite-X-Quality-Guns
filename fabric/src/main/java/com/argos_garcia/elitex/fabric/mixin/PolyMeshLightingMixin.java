package com.argos_garcia.elitex.fabric.mixin;

import cn.sh1rocu.tacz.compat.meshloader.render.PolyMeshGpuRenderer;
import com.mojang.renderpearl.api.pipeline.BindGroupLayout;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import net.minecraft.client.renderer.BindGroupLayouts;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Restores Minecraft's per-face ("cardinal") entity lighting for TaCZ's poly_mesh geometry.
 *
 * <p><b>Why this exists.</b> Before 26.3 the standalone TacZ Mesh Loader drew poly meshes through
 * {@code RenderTypes.entityCutout(...)}, i.e. the vanilla {@code core/entity} shader, which
 * multiplies each vertex by {@code minecraft_mix_light(Light0_Direction, Light1_Direction, Normal, Color)}.
 * Because the loader bakes one flat face normal per vertex (the pack's smooth {@code normals}
 * arrays are only used when {@code MeshPolyPreferPackNormals=true}), every face got a single,
 * constant brightness - the faceted "flat shading" the weapon pack was authored against.</p>
 *
 * <p>26.3's GPU-baked path ({@code MeshGpuBaking}, on by default) renders the same vertices with
 * TaCZ's own {@code RenderPipeline}s. Those pipelines declare {@code NO_CARDINAL_LIGHTING} and do
 * not include the {@code Lighting} bind group, so {@code core/entity} compiles down to
 * {@code vertexColor = Color}: the normal attribute is ignored completely and the gun is lit only
 * by its texture and lightmap. That removes every facet boundary and makes flat surfaces and
 * cylinders look smoothly shaded.</p>
 *
 * <p><b>Second defect: the baked lightmap was quantised.</b> The GPU path bakes the packed
 * lightmap into the vertex buffer ({@code PolyMesh#writeRaw -> setLight}) and keys that bake with
 * {@code PolyMeshGpuRenderer#quantizeLight}, which rounds block and sky light <em>down</em> to a
 * multiple of {@code LIGHT_GRID = 4}. That rounded value is what ends up in the vertices, so a gun
 * in open daylight (block 4 / sky 15) is baked as {@code (4, 12)}. Minecraft's lightmap response is
 * non-linear ({@code get_brightness(level) = level / (4 - 3 * level)} in {@code core/lightmap.fsh}):
 * level 15 = 1.0 but level 12 = 0.5, so every poly-mesh surface rendered roughly <em>half as bright
 * as the cube parts of the same gun</em> and half as bright as in 1.21.1. TaCZ's own config
 * description documents the reference behaviour ("Upstream TML uses 8 unquantized levels; we quantize
 * light to a coarse grid first"), the 1.21.1 mesh loader baked the exact packed light
 * ({@code PolyMesh#ensureUploaded -> setLight(packedLight)}, 8-entry per-light VBO cache), and the
 * collector path for translucent bones in 26.3 still writes the exact light - so the quantisation was
 * the only lighting difference against the 1.21.1 render and against the vanilla cube geometry.</p>
 *
 * <p><b>What this does.</b> While TaCZ builds its mesh pipelines it now adds the vanilla
 * {@code Lighting} bind group ({@link RenderSystem#bindDefaultUniforms} already uploads it on every
 * draw) and keeps the cardinal-lighting branch in the shader, and {@code quantizeLight} is turned into
 * an identity function so the bake (and its cache key) uses the real packed light. Geometry, UVs,
 * animation data and the baked vertex buffers are untouched; the GPU path (and therefore its
 * performance) is kept.</p>
 *
 * <p><b>Known limitation (reference-identical, not a regression).</b> The GPU path bakes vertices in
 * bone-local space and does not rotate the stored normals with the bone pose or with the model-view
 * transform. The 1.21.1 mesh loader behaves the same way in the path it actually used in first person
 * ({@code useVBO = !isGuiLike}: {@code PolyMesh#ensureUploaded} bakes the bone-local normal and
 * {@code drawVBO} only supplies {@code modelView * bonePose} as {@code ModelViewMat}), and 26.3 writes
 * the same {@code (faceNx, -faceNy, faceNz)} values with the same {@code Lighting.Entry.LEVEL}, so the
 * faceted pattern matches 1.21.1 face for face. Vanilla cube geometry instead multiplies each face
 * normal by {@code pose.normal()} ({@code BedrockCubeBox#compile}), so cube parts follow the camera
 * while poly meshes keep their shading glued to the model - true in 1.21.1 as well. Removing that last
 * difference needs a vertex shader for the mesh pipelines that transforms {@code Normal} by
 * {@code ModelViewMat} (the {@code DynamicTransforms} block has no normal matrix and the baked buffers
 * are static), which would change the reference look, so it is deliberately not done here.</p>
 */
@Mixin(PolyMeshGpuRenderer.class)
public abstract class PolyMeshLightingMixin {

    /**
     * Drops TaCZ's {@code NO_CARDINAL_LIGHTING} define so {@code core/entity} keeps using the
     * {@code Normal} vertex attribute. All other defines (cutout, emissive, overlay, scope mask)
     * are forwarded unchanged.
     */
    @Redirect(method = "<clinit>", remap = false,
            at = @At(value = "INVOKE",
                    target = "Lcom/mojang/renderpearl/api/pipeline/RenderPipeline$Builder;withShaderDefine(Ljava/lang/String;)Lcom/mojang/renderpearl/api/pipeline/RenderPipeline$Builder;"))
    private static RenderPipeline.Builder elitex$keepCardinalLighting(RenderPipeline.Builder builder, String name) {
        if ("NO_CARDINAL_LIGHTING".equals(name)) {
            return builder;
        }
        return builder.withShaderDefine(name);
    }

    /**
     * Adds {@code BindGroupLayouts.LIGHTING} in the same position vanilla's
     * {@code MATRICES_FOG_LIGHT_DIR_SNIPPET} puts it (after the matrix/fog layouts, before the
     * samplers) so the shader's {@code Lighting} uniform block is bound. The pipeline already
     * declares {@code GLOBALS/PROJECTION/DYNAMIC_TRANSFORMS/FOG} through
     * {@code RenderPipelines.MATRICES_FOG_SNIPPET}; {@code SAMPLER0} is the first sampler layout
     * TaCZ adds, which makes it a reliable insertion point for every mesh pipeline.
     */
    @Redirect(method = "<clinit>", remap = false,
            at = @At(value = "INVOKE",
                    target = "Lcom/mojang/renderpearl/api/pipeline/RenderPipeline$Builder;withBindGroupLayout(Lcom/mojang/renderpearl/api/pipeline/BindGroupLayout;)Lcom/mojang/renderpearl/api/pipeline/RenderPipeline$Builder;"))
    private static RenderPipeline.Builder elitex$addLightingLayout(RenderPipeline.Builder builder, BindGroupLayout layout) {
        if (layout == BindGroupLayouts.SAMPLER0) {
            builder.withBindGroupLayout(BindGroupLayouts.LIGHTING);
        }
        return builder.withBindGroupLayout(layout);
    }

    /**
     * Stops TaCZ from rounding the packed light down to a multiple of {@code LIGHT_GRID} (4) before it
     * is baked into the mesh vertices and before the bake is cached. The value returned here is used
     * both as the {@code setLight} argument of {@code PolyMesh#writeRaw} and as the cache key in
     * {@code TaczPolyMeshGunModel#ensureBaked}/{@code #ensureWorldBaked}, so returning the argument
     * unchanged makes the GPU path carry exactly the packed light that the vanilla cube geometry and
     * the 1.21.1 mesh loader use (which is also what TaCZ's own config text calls "8 unquantized
     * levels"). Lowering the block/sky levels changes brightness non-linearly - level 15 is 1.0 but
     * level 12 is 0.5 in {@code core/lightmap.fsh} - so the old behaviour darkened every mesh surface.
     */
    @Inject(method = "quantizeLight", remap = false, at = @At("HEAD"), cancellable = true)
    private static void elitex$keepExactBakedLight(int packedLight, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(packedLight);
        cir.cancel();
    }
}
