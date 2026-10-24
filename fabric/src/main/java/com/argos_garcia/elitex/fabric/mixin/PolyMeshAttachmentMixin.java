package com.argos_garcia.elitex.fabric.mixin;

import cn.sh1rocu.tacz.compat.meshloader.model.TaczPolyMeshAttachmentModel;
import com.tacz.guns.client.render.scope.ScopeBodyRenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Puts poly_mesh attachment geometry through the same body render type TaCZ uses for the cube
 * geometry of that same attachment.
 *
 * <p><b>Why this exists.</b> {@code BedrockAttachmentModel#submit} receives a render type that TaCZ
 * already resolved for the attachment body: in first person it is
 * {@code ScopeBodyRenderTypes.clipForViewmodel(entityCutout(texture), texture, firstPerson)}, i.e.
 * {@code tacz:core/scope_body} + {@code SCOPE_MASK} while the ocular mask of an aimed scope is
 * active, and the plain entity cutout otherwise. The cube parts (and the whole weapon body) are
 * submitted with that type, so the scope housing is clipped away inside the ocular projection and
 * the picture-in-picture world view shows through the lens.</p>
 *
 * <p>The 26.3 mesh loader ignores that argument for poly_mesh geometry: it hardcodes
 * {@code RenderTypes.entityCutout(texture)}. Every EliteX scope body is poly_mesh (PSG-1 is the one
 * model without poly geometry, which is why it looks right), so the housing was painted over the
 * ocular aperture and over the PIP composite - the reticle still drew (TaCZ clips the reticle with
 * its own type), but the sight picture stayed dark. The 1.21.1 mesh loader had the equivalent
 * handling ({@code renderPolyMeshThroughStencilHole} drew the poly body through the stencil hole),
 * so this restores the reference behaviour with the 26.3 mask API.</p>
 *
 * <p><b>What this does.</b> The texture argument of that one {@code RenderTypes.entityCutout} call is
 * resolved exactly like the cube path resolves its own type. Nothing else changes: no geometry, no
 * UVs, no light, no normals, no render pass, and non-scope attachments (sights, grips, muzzles) keep
 * the plain cutout because {@link ScopeBodyRenderTypes#clipForViewmodel} only clips when the ocular
 * mask of an aimed scope is actually ready for the viewmodel.</p>
 */
@Mixin(TaczPolyMeshAttachmentModel.class)
public abstract class PolyMeshAttachmentMixin {

    /**
     * Mirrors {@code BedrockAttachmentModel#submit}: scopes and hybrid optics get the mask-aware body
     * type, sights keep the vanilla cutout (TaCZ's sight channel deliberately does not clip the sight
     * body, see {@code SCOPE_SIGHT_CLIP_FIX}).
     */
    @Redirect(method = "submit", remap = false,
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/rendertype/RenderTypes;entityCutout(Lnet/minecraft/resources/Identifier;)Lnet/minecraft/client/renderer/rendertype/RenderType;"))
    private RenderType elitex$polyAttachmentBodyType(Identifier texture) {
        RenderType plain = RenderTypes.entityCutout(texture);
        TaczPolyMeshAttachmentModel self = (TaczPolyMeshAttachmentModel) (Object) this;
        if (!self.isScope()) {
            return plain;
        }
        return ScopeBodyRenderTypes.clipForViewmodel(plain, texture, true);
    }
}
