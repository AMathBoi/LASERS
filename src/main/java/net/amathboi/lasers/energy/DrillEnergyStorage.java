package net.amathboi.lasers.energy;

import net.amathboi.lasers.Screen.custom.LaserScreenHandler;
import net.amathboi.lasers.component.ModDataComponentTypes;
import net.amathboi.lasers.item.UpgradeItem;
import net.amathboi.lasers.item.UpgradeSpecific;
import net.minecraft.item.ItemStack;
import team.reborn.energy.api.base.SimpleEnergyStorage;

public class DrillEnergyStorage extends SimpleEnergyStorage {
    private final ItemStack stack;

    private static final long BASE_CAPACITY   = 0L;
    private static final long UPGRADED_MK1    = 5_000L;
    private static final long MAX_INSERT      = 1_000L;
    private static final long MAX_EXTRACT     = 1_000L;   // ← allow extraction

    public DrillEnergyStorage(ItemStack stack) {
        super(determineCapacity(stack), MAX_INSERT, MAX_EXTRACT);
        this.stack  = stack;
        this.amount = Math.min(getStoredEnergy(stack), capacity);
    }

    private static long determineCapacity(ItemStack stack) {
        boolean hasMk1 = LaserScreenHandler.loadUpgrades(stack).stream()
                .anyMatch(up -> up.getItem() instanceof UpgradeItem ui
                        && ui.getUpgradeSpecific() == UpgradeSpecific.BATTERY_MK1);
        return hasMk1 ? UPGRADED_MK1 : BASE_CAPACITY;
    }

    private static long getStoredEnergy(ItemStack stack) {
        return stack.getOrDefault(ModDataComponentTypes.BATTERY, 0L);
    }

    @Override
    protected void onFinalCommit() {
        stack.set(ModDataComponentTypes.BATTERY, this.amount);
    }
}
