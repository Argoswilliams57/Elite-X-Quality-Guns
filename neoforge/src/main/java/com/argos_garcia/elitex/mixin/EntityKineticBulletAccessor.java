package com.argos_garcia.elitex.mixin;

import com.tacz.guns.entity.EntityKineticBullet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;


@Mixin(EntityKineticBullet.class)
public interface EntityKineticBulletAccessor {
    @Accessor(value = "igniteBlock", remap = false)
    boolean elitex$isIgniteBlock();
}
