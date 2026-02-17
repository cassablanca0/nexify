package net.nexify.client.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(Explosion.class)
public class ExplosionMixin {

    @Redirect(
            method = "collectBlocksAndDamageEntities",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;iterator()Ljava/util/Iterator;"
            )
    )
    private java.util.Iterator<Entity> nexify$optimizeEntityIteration(List<Entity> list) {


        if (list.size() < 10) {
            return list.iterator();
        }


        return list.stream()
                .filter(entity -> entity.squaredDistanceTo(entity.getPos()) < 144) // 12 blok
                .iterator();
    }
}