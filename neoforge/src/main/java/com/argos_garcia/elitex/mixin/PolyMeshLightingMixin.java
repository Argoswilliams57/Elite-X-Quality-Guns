package com.argos_garcia.elitex.mixin;

import cn.sh1rocu.tacz.compat.meshloader.render.PolyMeshGpuRenderer;
import com.mojang.renderpearl.api.pipeline.BindGroupLayout;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import net.minecraft.client.renderer.BindGroupLayouts;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(PolyMeshGpuRenderer.class)
public abstract class PolyMeshLightingMixin {

    /** Drops {@code NO_CARDINAL_LIGHTING} so {@code core/entity} keeps using the Normal attribute. */
    @Redirect(method = "<clinit>", remap = false,
            at = @At(value = "INVOKE",
                    target = "Lcom/mojang/renderpearl/api/pipeline/RenderPipeline$Builder;withShaderDefine(Ljava/lang/String;)Lcom/mojang/renderpearl/api/pipeline/RenderPipeline$Builder;"))
    private static RenderPipeline.Builder elitex$keepCardinalLighting(RenderPipeline.Builder builder, String name) {
        if ("NO_CARDINAL_LIGHTING".equals(name)) {
            return builder;
        }
        return builder.withShaderDefine(name);
    }

    /** Adds the {@code Lighting} layout where vanilla's light-dir snippet puts it (before samplers). */
    @Redirect(method = "<clinit>", remap = false,
            at = @At(value = "INVOKE",
                    target = "Lcom/mojang/renderpearl/api/pipeline/RenderPipeline$Builder;withBindGroupLayout(Lcom/mojang/renderpearl/api/pipeline/BindGroupLayout;)Lcom/mojang/renderpearl/api/pipeline/RenderPipeline$Builder;"))
    private static RenderPipeline.Builder elitex$addLightingLayout(RenderPipeline.Builder builder, BindGroupLayout layout) {
        if (layout == BindGroupLayouts.SAMPLER0) {
            builder.withBindGroupLayout(BindGroupLayouts.LIGHTING);
        }
        return builder.withBindGroupLayout(layout);
    }

    @Inject(method = "quantizeLight", remap = false, at = @At("HEAD"), cancellable = true)
    private static void elitex$keepExactBakedLight(int packedLight, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(packedLight);
        cir.cancel();
    }
}
