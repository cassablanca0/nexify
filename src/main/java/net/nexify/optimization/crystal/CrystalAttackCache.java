package net.nexify.optimization.crystal;

import net.minecraft.entity.Entity;

public class CrystalAttackCache {

    private static int lastEntityId = -1;
    private static long lastTick = -1;

    public static boolean shouldSkip(Entity entity, long tick) {
        if (entity.getId() == lastEntityId && tick == lastTick) {
            return true;
        }

        lastEntityId = entity.getId();
        lastTick = tick;
        return false;
    }
}