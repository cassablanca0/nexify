package net.nexify.optimization.crystal;

import net.minecraft.util.math.BlockPos;

public class CrystalPlaceCache {

    private static BlockPos lastPos = null;
    private static long lastTick = -1;

    public static boolean shouldSkip(BlockPos pos, long tick) {

        if (lastPos != null && lastPos.equals(pos) && tick == lastTick) {
            return true;
        }

        lastPos = pos.toImmutable();
        lastTick = tick;
        return false;
    }
}