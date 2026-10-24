package com.argos_garcia.elitex.mixin;

import cn.sh1rocu.tacz.compat.meshloader.render.PolyMeshGpuRenderer;
import com.mojang.renderpearl.api.textures.FilterMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

// This fixes and restores the default lighting which piece of shit latast tacz mod intrduces
@Mixin(PolyMeshGpuRenderer.class)
public abstract class PolyMeshSamplerMixin {

    /** Scoped to {@code drawList} so the lightmap / scope-mask samplers keep their own modes. */
    @ModifyArg(method = "drawList", remap = false,
            at = @At(value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/systems/SamplerCache;getClampToEdge(Lcom/mojang/renderpearl/api/textures/FilterMode;)Lcom/mojang/renderpearl/api/textures/GpuSampler;"),
            index = 0)
    private static FilterMode elitex$keepTexelFiltering(FilterMode mode) {
        return mode == FilterMode.LINEAR ? FilterMode.NEAREST : mode;
    }
}
