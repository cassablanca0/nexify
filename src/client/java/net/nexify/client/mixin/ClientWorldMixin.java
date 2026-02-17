package net.nexify.client.mixin;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {

    @Unique
    private int nexify$particleCount = 0;

    @Unique
    private long nexify$lastTick = -1;

    @Inject(
            method = "addParticle(Lnet/minecraft/particle/ParticleEffect;ZDDDDDD)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void nexify$optimizeParticles(
            ParticleEffect parameters,
            boolean alwaysSpawn,
            double x, double y, double z,
            double velocityX, double velocityY, double velocityZ,
            CallbackInfo ci
    ) {

        ClientWorld world = (ClientWorld)(Object)this;
        long tick = world.getTime();

        if (tick != nexify$lastTick) {
            nexify$particleCount = 0;
            nexify$lastTick = tick;
        }

        nexify$particleCount++;

        if (nexify$particleCount > 75) {
            ci.cancel();
        }
    }
}