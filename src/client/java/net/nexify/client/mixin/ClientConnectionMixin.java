package net.nexify.client.mixin;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {

    // ==============================
    // ATTACK PACKET OPTIMIZATION
    // ==============================

    @Unique
    private long nexify$lastAttackPacketTime = 0;

    // ==============================
    // PLACE PACKET OPTIMIZATION
    // ==============================

    @Unique
    private BlockPos nexify$lastPlacePos = null;

    @Unique
    private long nexify$lastPlacePacketTime = 0;

    // ==============================
    // ROTATION STABILIZER
    // ==============================

    @Unique
    private float nexify$lastYaw = Float.NaN;

    @Unique
    private float nexify$lastPitch = Float.NaN;

    @Inject(method = "send", at = @At("HEAD"), cancellable = true)
    private void nexify$optimizePackets(Packet<?> packet, CallbackInfo ci) {

        long now = System.nanoTime();

        // ----------------------------------
        // CRYSTAL BREAK (Entity Attack)
        // ----------------------------------
        if (packet instanceof PlayerInteractEntityC2SPacket) {

            // 0.5ms altı duplicate attack kes
            if (now - nexify$lastAttackPacketTime < 500_000) {
                ci.cancel();
                return;
            }

            nexify$lastAttackPacketTime = now;
        }

        // ----------------------------------
        // CRYSTAL PLACE (Block Interact)
        // ----------------------------------
        if (packet instanceof PlayerInteractBlockC2SPacket interactPacket) {

            BlockPos pos = interactPacket.getBlockHitResult().getBlockPos();
            Hand hand = interactPacket.getHand();

            if (nexify$lastPlacePos != null &&
                    nexify$lastPlacePos.equals(pos) &&
                    now - nexify$lastPlacePacketTime < 1_000_000) {

                ci.cancel();
                return;
            }

            nexify$lastPlacePos = pos;
            nexify$lastPlacePacketTime = now;
        }

        // ----------------------------------
        // ROTATION STABILIZER
        // ----------------------------------
        if (packet instanceof PlayerMoveC2SPacket movePacket) {

            float yaw = movePacket.getYaw(0);
            float pitch = movePacket.getPitch(0);

            if (!Float.isNaN(nexify$lastYaw)) {

                float yawDiff = Math.abs(yaw - nexify$lastYaw);
                float pitchDiff = Math.abs(pitch - nexify$lastPitch);


                if (yawDiff < 0.08f && pitchDiff < 0.08f) {
                    ci.cancel();
                    return;
                }
            }

            nexify$lastYaw = yaw;
            nexify$lastPitch = pitch;
        }
    }
}