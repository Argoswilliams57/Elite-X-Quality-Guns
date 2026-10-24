package com.argos_garcia.elitex.mixin;

import com.tacz.guns.entity.EntityKineticBullet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Reads TaCZ's per-projectile {@code ignite.block} flag (Forge 1.20.1).
 *
 * <p>{@code EntityKineticBullet} keeps the value in a private field without a getter, and the
 * explosion itself never sees it. {@code ExplodeUtilMixin} uses this accessor to decide whether
 * an explosion created by the projectile is allowed to ignite the area around it.</p>
 */
@Mixin(EntityKineticBullet.class)
public interface EntityKineticBulletAccessor {
    @Accessor(value = "igniteBlock", remap = false)
    boolean elitex$isIgniteBlock();
}
