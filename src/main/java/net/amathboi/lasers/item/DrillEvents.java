package net.amathboi.lasers.item;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;

import java.util.List;

public class DrillEvents {
    public static void register() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (world.isClient) return;

            ItemStack stack = player.getMainHandStack();

            if (stack.getItem() instanceof DrillItem && ((DrillItem) stack.getItem()).hasFortuneUpgrade(stack)) {
                if (isOreBlock(state)) {
                    List<ItemStack> originalDrops = Block.getDroppedStacks(state, (ServerWorld) world, pos, blockEntity, player, stack);

                    state.onStacksDropped((ServerWorld) world, pos, stack, true);

                    for (ItemStack drop : originalDrops) {
                        if (!drop.isEmpty()) {
                            int count = drop.getCount() * 3;
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