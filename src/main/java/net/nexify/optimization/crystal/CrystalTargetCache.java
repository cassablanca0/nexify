package net.nexify.optimization.crystal;

import net.minecraft.util.hit.HitResult;

public class CrystalTargetCache {

    private static HitResult cachedResult;
    private static long lastTick = -1;

    public static HitResult get(long currentTick) {
        if (currentTick == lastTick) {
            return cachedResult;
        }
        return null;
    }

    public static void store(HitResult result, long currentTick) {
        cachedResult = result;
        lastTick = currentTick;
    }
}