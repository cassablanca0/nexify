package net.nexify.client.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.nexify.optimization.crystal.CrystalPlaceCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {




    //------------------------ //
    // CLIENT SIDE CRYSTAL BREAKING //
    @Inject(method = "attackEntity", at = @At("HEAD"))
    private void nexify$removeCrystalEarly(PlayerEntity player, Entity target, CallbackInfo ci) {

        if (target instanceof EndCrystalEntity) {
            target.remove(Entity.RemovalReason.KILLED);
        }
    }

    // ==============================
    // PLACE OPTIMIZATION
    // ==============================

    @Inject(method = "interactBlock", at = @At("HEAD"), cancellable = true)
    private void nexify$optimizeCrystalPlace(
            ClientPlayerEntity player,
            Hand hand,
            BlockHitResult hitResult,
            CallbackInfoReturnable<ActionResult> cir
    ) {

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return;


        if (player.getStackInHand(hand).getItem() != Items.END_CRYSTAL) {
            return;
        }

        long tick = client.world.getTime();
        BlockPos pos = hitResult.getBlockPos();


        if (CrystalPlaceCache.shouldSkip(pos, tick)) {
            cir.setReturnValue(ActionResult.SUCCESS);
        }
    }

// ==============================
// BREAK OPTIMIZATION
// ==============================

    @Unique
    private int nexify$lastAttackedEntityId = -1;

    @Unique
    private long nexify$lastAttackTick = -1;

    @Unique
    private long nexify$attackGracePeriod = 1; // 1 tick grace

    @Inject(method = "attackEntity", at = @At("HEAD"), cancellable = true)
    private void nexify$optimizeCrystalBreak(
            PlayerEntity player, Entity target, CallbackInfo ci
    ) {

        if (!(target instanceof EndCrystalEntity)) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return;

        long tick = client.world.getTime();
        int entityId = target.getId();

        if (entityId == nexify$lastAttackedEntityId &&
                tick - nexify$lastAttackTick <= nexify$attackGracePeriod) {

            ci.cancel();
            return;
        }

        nexify$lastAttackedEntityId = entityId;
        nexify$lastAttackTick = tick;
    }
    // ==============================
    // SLOT SYNC OPTIMIZATION
    // ==============================

    @Unique
    private int nexify$lastSyncedSlot = -1;

    @Inject(method = "syncSelectedSlot", at = @At("HEAD"), cancellable = true)
    private void nexify$optimizeSlotSync(CallbackInfo ci) {

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        int currentSlot = client.player.getInventory().selectedSlot;

        if (currentSlot == nexify$lastSyncedSlot) {
            ci.cancel();
            return;
        }

        nexify$lastSyncedSlot = currentSlot;
    }
}
