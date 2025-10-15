package net.amathboi.lasers.mixin;

import net.amathboi.lasers.item.DrillItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class DrillParticleMixin {
    private int tickCounter = 0;

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
        ItemStack stack = player.getMainHandStack();

        if (stack.getItem() instanceof DrillItem) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.options.attackKey.isPressed()) {
                if (++tickCounter % 2 != 0) {
                    return;
                }

                World world = player.getWorld();
                HitResult hit = player.raycast(5.0, 0, false);

                if (hit.getType() == HitResult.Type.BLOCK) {
                    BlockPos targetPos = ((BlockHitResult) hit).getBlockPos();

                    float yaw = player.getYaw() + 80;
                    float pitch = player.getPitch() + 15;
                    double distance = 0.5;

                    double startX = player.getX() - MathHelper.sin(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F) * distance;
                    double startY = player.getEyeY() - 0.4 - MathHelper.sin(pitch * 0.017453292F) * distance;
                    double startZ = player.getZ() + MathHelper.cos(yaw * 0.017453292F) * MathHelper.cos(pitch * 0.017453292F) * distance;

                    Vec3d startPos = new Vec3d(startX, startY, startZ);
                    Vec3d targetCenter = Vec3d.ofCenter(targetPos);

                    int particleCount = 15;
                    for (int i = 0; i < particleCount; i++) {
                        double ratio = (double) i / particleCount;
                        double x = startPos.x + (targetCenter.x - startPos.x) * ratio;
                        double y = startPos.y + (targetCenter.y - startPos.y) * ratio;
                        double z = startPos.z + (targetCenter.z - startPos.z) * ratio;

                        double offset = 0.05;
                        x += (world.random.nextDouble() - 0.5) * offset;
                        y += (world.random.nextDouble() - 0.5) * offset;
                        z += (world.random.nextDouble() - 0.5) * offset;

                        world.addParticle(
                                ParticleTypes.FLAME,
                                true,
                                x, y, z,
                                0, 0, 0
                        );
                    }

                    Direction hitFace = ((BlockHitResult) hit).getSide();
                    Vec3i vecInt = hitFace.getVector();
                    Vec3d normalVec = new Vec3d(vecInt.getX(), vecInt.getY(), vecInt.getZ());
                    Vec3d facePos = targetCenter.subtract(normalVec.multiply(0.5));

                    world.addParticle(
                            ParticleTypes.CAMPFIRE_COSY_SMOKE,
                            true,
                            facePos.x, facePos.y, facePos.z,
                            0, 0, 0
                    );

                }
            } else {
                tickCounter = 0;
            }
        }
    }
}