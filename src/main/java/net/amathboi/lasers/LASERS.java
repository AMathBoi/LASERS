package net.amathboi.lasers;

import net.amathboi.lasers.Screen.ModScreenHandlers;
import net.amathboi.lasers.Screen.custom.LaserScreenHandler;
import net.amathboi.lasers.block.ModBlocks;
import net.amathboi.lasers.block.entity.ModBlockEntities;
import net.amathboi.lasers.block.entity.custom.LaserWorkstationEntity;
import net.amathboi.lasers.component.ModDataComponentTypes;
import net.amathboi.lasers.item.*;
import net.amathboi.lasers.util.ModTags;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.reborn.energy.api.EnergyStorage;

public class LASERS implements ModInitializer {
    public static final String MOD_ID = "lasers";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private boolean hasHardnessUpgrade(ItemStack stack) {
        return LaserScreenHandler.loadUpgrades(stack).stream()
                .anyMatch(up -> up.getItem() instanceof UpgradeItem ui
                        && ui.getUpgradeSpecific() == UpgradeSpecific.HARDNESS);
    }

    @Override
    public void onInitialize() {
        ModDataComponentTypes.registerDataComponentTypes();
        ModItemGroups.registerItemGroups();

        ModItems.registerModItems();
        ModBlocks.registerModBlocks();

        ModTags.Blocks.registerModTags();

        ModBlockEntities.registerBlockEntities();
        ModScreenHandlers.registerScreenHandlers();

        PlayerBlockBreakEvents.BEFORE.register((World world,
                                                PlayerEntity player,
                                                BlockPos pos,
                                                BlockState state,
                                                @Nullable BlockEntity blockEntity) -> {
            boolean needsDiamond = state.isIn(BlockTags.NEEDS_DIAMOND_TOOL);
            ItemStack stack = player.getMainHandStack();
            boolean hardness = hasHardnessUpgrade(stack);
            if (!world.isClient() && player.getMainHandStack().getItem() instanceof DrillItem && !hardness && needsDiamond) {
                world.removeBlock(pos, false);
                return false;
            } else if (!world.isClient() && player.getMainHandStack().getItem() instanceof DrillItem && hardness && needsDiamond) {
                return true;
            }
            return true;
        });

        EnergyStorage.SIDED.registerForBlockEntities(
                (be, direction) -> ((LaserWorkstationEntity) be).energyStorage,
                ModBlockEntities.LASER_BE
        );

        DrillEvents.register();
    }
}
