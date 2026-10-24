package com.argos_garcia.elitex.mixin;

import cn.sh1rocu.tacz.compat.meshloader.model.TaczPolyMeshAttachmentModel;
import com.tacz.guns.client.render.scope.ScopeBodyRenderTypes;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Renders poly_mesh attachment geometry with the same body render type TaCZ resolved for the cube
 * geometry of the attachment ({@code tacz:core/scope_body} + {@code SCOPE_MASK} while an aimed
 * scope's ocular mask is active, plain entity cutout otherwise).
 *
 * <p>The 26.3 mesh loader hardcodes {@code RenderTypes.entityCutout} for the poly snapshot, so poly
 * scope housings were never clipped by the ocular mask and covered the picture-in-picture view.
 * PSG-1 is the one EliteX scope without poly geometry, which is why only it looked correct. The
 * 1.21.1 loader drew the poly body through the stencil hole; this restores that behaviour through
 * the 26.3 mask API without touching geometry, light, normals or pass order.</p>
 */
@Mixin(TaczPolyMeshAttachmentModel.class)
public abstract class PolyMeshAttachmentMixin {

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
