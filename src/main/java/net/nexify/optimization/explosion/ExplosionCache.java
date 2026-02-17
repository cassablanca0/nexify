package net.nexify.optimization.explosion;

import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;

public class ExplosionCache {

    private static final Map<BlockPos, Float> DENSITY_CACHE = new HashMap<>();
    private static long lastTick = -1;

    public static void clearIfNewTick(long currentTick) {
        if (currentTick != lastTick) {
            DENSITY_CACHE.clear();
            lastTick = currentTick;
        }
    }

    public static Float get(BlockPos pos) {
        return DENSITY_CACHE.get(pos);
    }

    public static void put(BlockPos pos, float density) {
        DENSITY_CACHE.put(pos.toImmutable(), density);
    }
}