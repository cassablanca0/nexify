package net.nexify.client.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Unique
    private long nexify$lastTick = -1;

    @Unique
    private HitResult nexify$tickCachedTarget;

    @Inject(method = "tick", at = @At("HEAD"))
    private void nexify$stabilizeCrosshair(CallbackInfo ci) {

        MinecraftClient client = (MinecraftClient)(Object)this;
        if (client.world == null) return;

        long tick = client.world.getTime();

        if (tick != nexify$lastTick) {
            nexify$lastTick = tick;
            nexify$tickCachedTarget = client.crosshairTarget;
            return;
        }

        if (nexify$tickCachedTarget != null) {
            client.crosshairTarget = nexify$tickCachedTarget;
        }
    }
}