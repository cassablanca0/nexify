package net.nexify.client.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EndCrystalEntity.class)
public abstract class EndCrystalEntityMixin {

    @Unique
    private long nexify$lastCheckTick = -1;

    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"
            )
    )
    private BlockState nexify$optimizeBlockCheck(World world, BlockPos pos) {

        long currentTick = world.getTime();

        if (currentTick == nexify$lastCheckTick) {
            return world.getBlockState(pos);
        }

        nexify$lastCheckTick = currentTick;

        return world.getBlockState(pos);
    }
}