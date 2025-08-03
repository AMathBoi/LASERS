package net.amathboi.lasers.item;

import net.amathboi.lasers.item.DrillItem;
import net.amathboi.lasers.item.UpgradeSpecific;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class DrillEvents {
    public static void register() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (world.isClient) return;

            ItemStack stack = player.getMainHandStack();

            // Check if the item is a drill with fortune upgrade
            if (stack.getItem() instanceof DrillItem && ((DrillItem) stack.getItem()).hasFortuneUpgrade(stack)) {
                // Check if the block is an ore
                if (isOreBlock(state)) {
                    // Get the original drops
                    List<ItemStack> originalDrops = Block.getDroppedStacks(state, (ServerWorld) world, pos, blockEntity, player, stack);

                    // Clear the original drops
                    state.onStacksDropped((ServerWorld) world, pos, stack, true);

                    // Apply fortune effect (3x drops for example)
                    for (ItemStack drop : originalDrops) {
                        if (!drop.isEmpty()) {
                            int count = drop.getCount() * 3; // 3x drops for fortune 3 equivalent
                            drop.setCount(count);
                            Block.dropStack(world, pos, drop);
                        }
                    }
                }
            }
        });
    }

    private static boolean isOreBlock(BlockState state) {
        return state.isOf(Blocks.COAL_ORE) ||
                state.isOf(Blocks.IRON_ORE) ||
                state.isOf(Blocks.GOLD_ORE) ||
                state.isOf(Blocks.LAPIS_ORE) ||
                state.isOf(Blocks.REDSTONE_ORE) ||
                state.isOf(Blocks.DIAMOND_ORE) ||
                state.isOf(Blocks.EMERALD_ORE) ||
                state.isOf(Blocks.NETHER_QUARTZ_ORE) ||
                state.isOf(Blocks.NETHER_GOLD_ORE) ||
                state.isOf(Blocks.ANCIENT_DEBRIS);
    }
}