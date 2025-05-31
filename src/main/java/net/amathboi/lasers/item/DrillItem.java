package net.amathboi.lasers.item;

import net.amathboi.lasers.Screen.custom.LaserScreenHandler;
import net.amathboi.lasers.component.ModDataComponentTypes;
import net.amathboi.lasers.energy.DrillEnergyStorage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


import java.util.List;

public class DrillItem extends PickaxeItem {
    public DrillItem(ModToolMaterials material, Settings settings) {
        super(material, settings);
    }

    @Override
    public int getMaxCount() {
        return 1;
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return true;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public int getEnchantability() {
        return 0;
    }

    public static boolean hasEnergyMK1(ItemStack stack) {
        return LaserScreenHandler.loadUpgrades(stack).stream()
                .anyMatch(up -> up.getItem() instanceof UpgradeItem ui
                        && ui.getUpgradeSpecific() == UpgradeSpecific.BATTERY_MK1);
    }

    private static long determineCapacity(ItemStack stack) {
        if (hasEnergyMK1(stack)) {
            return 50_000L;
        }
        return 0L;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType options) {
        super.appendTooltip(stack, context, tooltip, options);

        long currentEnergy = (stack.getOrDefault(ModDataComponentTypes.BATTERY, 0L) / 1000);
        long capacity = (determineCapacity(stack) / 1000);

        if (capacity > 0) {
                MutableText energy = Text.literal(currentEnergy + "k/" + capacity + "k FE");
                energy.setStyle(Style.EMPTY.withFormatting(Formatting.YELLOW));
                tooltip.add(energy);
            }


        List<ItemStack> upgrades = stack.getOrDefault(ModDataComponentTypes.DRILL_UPGRADES, List.of());
        if(Screen.hasShiftDown()) {
            if (!upgrades.isEmpty()) {
                MutableText header = Text.translatable("tooltip.lasers.drill_upgrades");
                header.setStyle(Style.EMPTY.withFormatting(Formatting.GRAY));
                tooltip.add(header);


                for (ItemStack up : upgrades) {
                    Formatting color = Formatting.WHITE;
                    if (up.getItem() instanceof UpgradeItem ui) {
                        switch (ui.getUpgradeType()) {
                            case YELLOW -> color = Formatting.YELLOW;
                            case BLUE -> color = Formatting.AQUA;
                            case RED -> color = Formatting.RED;
                            case GRAY -> color = Formatting.GRAY;
                        }
                    }

                    MutableText bullet = Text.literal("  • ");
                    bullet.setStyle(Style.EMPTY.withFormatting(Formatting.DARK_GRAY));

                    MutableText name = up.getName().copy();
                    name.setStyle(Style.EMPTY.withFormatting(color));

                    bullet.append(name);
                    tooltip.add(bullet);
                }
            }
        } else {
                tooltip.add(Text.translatable("tooltip.lasers.shift"));
            }
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return true;
    }

    private boolean hasEfficiencyUpgrade(ItemStack stack) {
        return LaserScreenHandler.loadUpgrades(stack).stream()
                .anyMatch(up -> up.getItem() instanceof UpgradeItem ui
                        && ui.getUpgradeSpecific() == UpgradeSpecific.EFFICIENCY);
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        long pull = 5L;
        if (hasEfficiencyUpgrade(stack)) {
            pull = 3L;
        }

        if (!world.isClient && miner instanceof PlayerEntity player) {
            DrillEnergyStorage storage = new DrillEnergyStorage(stack);
            try (Transaction tx = Transaction.openOuter()) {
                long extracted = storage.extract(pull, tx);
                if (extracted < pull) {
                    return false;
                }
                tx.commit();
            }
        }
        return true;
    }

    private boolean hasHasteUpgrade(ItemStack stack) {
        return LaserScreenHandler.loadUpgrades(stack).stream()
                .anyMatch(up -> up.getItem() instanceof UpgradeItem ui
                        && ui.getUpgradeSpecific() == UpgradeSpecific.HASTE);
    }

    @Override
    public float getMiningSpeed(ItemStack stack, BlockState state) {
        DrillEnergyStorage storage = new DrillEnergyStorage(stack);

        if (storage.amount >= 5) {
            if (hasHasteUpgrade(stack)) {
                return ModToolMaterials.LASER_MK1.getMiningSpeedMultiplier() * 1.5f;
            }
            return ModToolMaterials.LASER_MK1.getMiningSpeedMultiplier();
        }
        return 0f;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
    }
}
