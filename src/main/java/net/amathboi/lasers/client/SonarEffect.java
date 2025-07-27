package net.amathboi.lasers.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Environment(EnvType.CLIENT)
public class SonarEffect {
    private static final int RANGE = 16;
    private static final Map<BlockPos, Block> foundOres = new HashMap<>();
    private static long lastScanTime = 0;
    private static final long SCAN_COOLDOWN_MS = 1000;
    private static long effectStartTime = 0;
    private static final long EFFECT_DURATION_MS = 10000;

    public static void scanForOres(World world, PlayerEntity player) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastScanTime < SCAN_COOLDOWN_MS) {
            return;
        }
        lastScanTime = currentTime;
        effectStartTime = currentTime;

        foundOres.clear();
        BlockPos playerPos = player.getBlockPos();

        for (int x = -RANGE/2; x <= RANGE/2; x++) {
            for (int y = -RANGE/2; y <= RANGE/2; y++) {
                for (int z = -RANGE/2; z <= RANGE/2; z++) {
                    BlockPos pos = playerPos.add(x, y, z);
                    BlockState state = world.getBlockState(pos);

                    if (state.isIn(BlockTags.PICKAXE_MINEABLE) &&
                            (state.isIn(BlockTags.GOLD_ORES) ||
                                    state.isIn(BlockTags.IRON_ORES) ||
                                    state.isIn(BlockTags.DIAMOND_ORES) ||
                                    state.isIn(BlockTags.REDSTONE_ORES) ||
                                    state.isIn(BlockTags.LAPIS_ORES) ||
                                    state.isIn(BlockTags.EMERALD_ORES) ||
                                    state.isIn(BlockTags.COAL_ORES) ||
                                    state.isIn(BlockTags.COPPER_ORES) ||
                                    state.isOf(Blocks.ANCIENT_DEBRIS))) {
                        foundOres.put(pos.toImmutable(), state.getBlock());
                    }
                }
            }
        }
    }

    public static void render(DrawContext context, float tickDelta) {
        long currentTime = System.currentTimeMillis();
        if (foundOres.isEmpty() || currentTime - effectStartTime > EFFECT_DURATION_MS) {
            if (!foundOres.isEmpty()) {
                foundOres.clear();
            }
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;

        PlayerEntity player = client.player;
        Vec3d cameraPos = client.gameRenderer.getCamera().getPos();
        TextRenderer textRenderer = client.textRenderer;

        Set<BlockPos> toRemove = new HashSet<>();

        for (Map.Entry<BlockPos, Block> entry : foundOres.entrySet()) {
            BlockPos pos = entry.getKey();
            Block block = entry.getValue();

            if (!client.world.getBlockState(pos).isOf(block)) {
                toRemove.add(pos);
                continue;
            }

            double x = pos.getX() + 0.5 - cameraPos.x;
            double y = pos.getY() + 0.5 - cameraPos.y;
            double z = pos.getZ() + 0.5 - cameraPos.z;

            double distSq = x * x + y * y + z * z;
            if (distSq > RANGE * RANGE) continue;

            float yawRad = (float) Math.toRadians(-player.getYaw(tickDelta));
            float pitchRad = (float) Math.toRadians(-player.getPitch(tickDelta));

            double xRot = x * Math.cos(yawRad) - z * Math.sin(yawRad);
            double zRot = x * Math.sin(yawRad) + z * Math.cos(yawRad);

            double yRot = y * Math.cos(pitchRad) - zRot * Math.sin(pitchRad);
            zRot = y * Math.sin(pitchRad) + zRot * Math.cos(pitchRad);
            if (zRot <= 0.1) continue;

            double scale = 100.0 / zRot;
            double screenX = client.getWindow().getScaledWidth() / 2.0 + xRot * scale;
            double screenY = client.getWindow().getScaledHeight() / 2.0 - yRot * scale;

            if (scale > 0.1) {
                String name = block.getName().getString();
                int textWidth = textRenderer.getWidth(name);

                context.drawTextWithShadow(textRenderer, Text.literal(name),
                        (int)(screenX - textWidth / 2.0), (int)screenY - 20, 0xFFFFFF);

                context.fill((int)screenX - 2, (int)screenY - 2,
                        (int)screenX + 2, (int)screenY + 2, 0xFFFFFF00);
            }
        }

        for (BlockPos pos : toRemove) {
            foundOres.remove(pos);
        }
    }
}