package com.argos_garcia.elitex.fabric.mixin;

import com.tacz.guns.config.common.AmmoConfig;
import com.tacz.guns.entity.EntityKineticBullet;
import com.tacz.guns.util.ExplodeUtil;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ExplodeUtil.class)
public abstract class ExplodeUtilMixin {

    @Inject(method = "createExplosion", at = @At("TAIL"), remap = false)
    private static void elitex$igniteExplosionArea(Entity owner, Entity exploder, float damage, float radius,
                                                   boolean knockback, boolean destroy, Vec3d hitPos, CallbackInfo ci) {
        if (radius <= 0.0F || !(exploder instanceof EntityKineticBullet bullet)) {
            return;
        }
        if (!((EntityKineticBulletAccessor) bullet).elitex$isIgniteBlock() || !(Boolean) AmmoConfig.IGNITE_BLOCK.get()) {
            return;
        }
        // With the global option enabled, vanilla already places fire for every explosive ammo
        // type; running this pass as well would only ignite the same area a second time.
        if ((Boolean) AmmoConfig.EXPLOSIVE_AMMO_FIRE.get()) {
            return;
        }
        if (!(bullet.getWorld() instanceof ServerWorld level)) {
            return;
        }

        double radiusSqr = (double) radius * radius;
        BlockPos min = new BlockPos(
                (int) Math.floor(hitPos.x - radius),
                (int) Math.floor(hitPos.y - radius),
                (int) Math.floor(hitPos.z - radius)
        );
        BlockPos max = new BlockPos(
                (int) Math.floor(hitPos.x + radius),
                (int) Math.floor(hitPos.y + radius),
                (int) Math.floor(hitPos.z + radius)
        );

        for (BlockPos candidate : BlockPos.iterate(min, max)) {
            double dx = candidate.getX() + 0.5 - hitPos.x;
            double dy = candidate.getY() + 0.5 - hitPos.y;
            double dz = candidate.getZ() + 0.5 - hitPos.z;
            if (dx * dx + dy * dy + dz * dz > radiusSqr) {
                continue;
            }
            if (level.getRandom().nextInt(3) != 0) {
                continue;
            }
            // Never replace a block and never place fire inside one: only air positions whose
            // support block can carry fire, exactly like vanilla explosion fire.
            BlockPos firePos = candidate.toImmutable();
            if (level.isOutOfHeightLimit(firePos)) {
                continue;
            }
            if (!level.getBlockState(firePos).isAir()) {
                continue;
            }
            BlockPos supportPos = firePos.down();
            if (!level.getBlockState(supportPos).isOpaqueFullCube(level, supportPos)) {
                continue;
            }
            BlockState fireState = AbstractFireBlock.getState(level, firePos);
            level.setBlockState(firePos, fireState);
        }
    }
}
