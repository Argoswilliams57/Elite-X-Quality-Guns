package com.argos_garcia.elitex.fabric.mixin;

import cn.sh1rocu.tacz.compat.meshloader.render.PolyMeshGpuRenderer;
import com.mojang.renderpearl.api.textures.FilterMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Restores vanilla texel filtering (crisp pixel art) for TaCZ's poly_mesh GPU pass.
 *
 * <p><b>Why this exists.</b> {@code PolyMeshGpuRenderer#drawList} binds {@code Sampler0} through
 * {@code SamplerCache#getClampToEdge(FilterMode.LINEAR)}, i.e. bilinear minification <em>and</em>
 * magnification. Vanilla entity render types ({@code RenderTypes.entityCutout(...)}) do not use a
 * fixed sampler: {@code RenderSetup#prepareTextures} binds {@code AbstractTexture#getSampler()},
 * and for a texture without {@code .mcmeta} that is {@code NEAREST/NEAREST}
 * ({@code ReloadableTexture}: {@code contents.blur() ? LINEAR : NEAREST}). The 1.21.1 mesh loader
 * drew through those render types, so texels stayed sharp. With the 26.3 GPU path the gun textures
 * (256x256 - 512x512 in this pack) are magnified a lot on screen and every texel boundary is
 * interpolated, which visually rounds off the small details and contributes to the "smooth"
 * appearance that survives the cardinal-lighting fix.</p>
 *
 * <p><b>What this does.</b> Only the {@code Sampler0} lookup inside {@code drawList} is changed, and
 * only the {@link FilterMode} argument: it becomes {@link FilterMode#NEAREST}, which is exactly what
 * vanilla derives for these textures (no mipmaps, address mode unchanged). Sampler2 (lightmap) and
 * the scope-mask sampler are already NEAREST and are untouched, as are UVs, mipmaps and the GPU
 * baking path.</p>
 */
@Mixin(PolyMeshGpuRenderer.class)
public abstract class PolyMeshSamplerMixin {

    /**
     * The mesh pass draws with the same sampling rules as {@code RenderTypes.entityCutout(...)}.
     * Scoped to {@code drawList} so the lightmap / scope-mask samplers created in
     * {@code drawListInto} keep their own modes.
     */
    @ModifyArg(method = "drawList", remap = false,
            at = @At(value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/systems/SamplerCache;getClampToEdge(Lcom/mojang/renderpearl/api/textures/FilterMode;)Lcom/mojang/renderpearl/api/textures/GpuSampler;"),
            index = 0)
    private static FilterMode elitex$keepTexelFiltering(FilterMode mode) {
        return mode == FilterMode.LINEAR ? FilterMode.NEAREST : mode;
    }
}
