package net.amathboi.lasers.Screen.custom;

import net.amathboi.lasers.Screen.ModScreenHandlers;
import net.amathboi.lasers.block.entity.custom.LaserWorkstationEntity;
import net.amathboi.lasers.component.ModDataComponentTypes;
import net.amathboi.lasers.item.DrillItem;
import net.amathboi.lasers.item.ModItems;
import net.amathboi.lasers.item.UpgradeItem;
import net.amathboi.lasers.item.UpgradeType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class LaserScreenHandler extends ScreenHandler {
    private final Inventory inventory;

    public LaserScreenHandler(int syncId, PlayerInventory playerInventory, BlockPos pos) {
        this(syncId, playerInventory, playerInventory.player.getWorld().getBlockEntity(pos));
    }

    private static final int FIRST_UPGRADE_SLOT = 1;

    private static int getSlotIndexForUpgradeType(UpgradeType type) {
        return switch (type) {
            case RED -> FIRST_UPGRADE_SLOT;
            case BLUE -> FIRST_UPGRADE_SLOT + 1;
            case YELLOW -> FIRST_UPGRADE_SLOT + 2;
            case GRAY -> FIRST_UPGRADE_SLOT + 3;
            case ENERGY -> FIRST_UPGRADE_SLOT + 4;
        };
    }

    private void updateDrillUpgrades() {
        ItemStack drillStack = inventory.getStack(0);
        if (drillStack.getItem() instanceof DrillItem) {
            List<ItemStack> upgrades = new ArrayList<>();
            for (int i = 1; i <= 5; i++) {
                ItemStack upgrade = inventory.getStack(i);
                if (!upgrade.isEmpty()) {
                    upgrades.add(upgrade.copy());
                }
            }
            storeUpgrades(drillStack, upgrades);
            // Mark the block entity as dirty to save changes
            if (inventory instanceof LaserWorkstationEntity workstation) {
                workstation.markDirty();
            }
        }
    }

    public LaserScreenHandler(int syncId, PlayerInventory playerInventory, BlockEntity blockEntity) {
        super(ModScreenHandlers.LASER_SCREEN_HANDLER, syncId);

        if (!(blockEntity instanceof LaserWorkstationEntity laserWorkstationEntity)) {
            throw new IllegalStateException("Expected LaserWorkstationEntity at screen handler position");
        }

        this.inventory = laserWorkstationEntity;

        //drill
        this.addSlot(new Slot(inventory, 0, 60, 34) {
            @Override
            public void onTakeItem(PlayerEntity player, ItemStack stack) {
                // Clear all upgrade slots when drill is taken
                for (int i = 1; i <= 5; i++) {
                    inventory.setStack(i, ItemStack.EMPTY);
                }
                super.onTakeItem(player, stack);
            }

            @Override
            public int getMaxItemCount() {
                return 1;
            }

            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.isOf(ModItems.LASER_DRILL_MK1);
            }

            @Override
            public void setStack(ItemStack newDrill) {
                ItemStack old = this.getStack().copy();
                super.setStack(newDrill);

                boolean inserted = old.isEmpty() && !newDrill.isEmpty();
                boolean removed = !old.isEmpty() && newDrill.isEmpty();

                if (inserted && newDrill.getItem() instanceof DrillItem) {
                    List<ItemStack> upgrades = loadUpgrades(newDrill);
                    for (ItemStack upgrade : upgrades) {
                        if (upgrade.getItem() instanceof UpgradeItem upgradeItem) {
                            UpgradeType type = upgradeItem.getUpgradeType();
                            int slotIndex = LaserScreenHandler.getSlotIndexForUpgradeType(type);
                            if (slotIndex >= 0) {
                                Slot slot = LaserScreenHandler.this.getSlot(slotIndex);
                                if (!slot.hasStack()) {
                                    slot.setStack(upgrade.copy());
                                }
                            }
                        }
                    }
                } else if (removed && old.getItem() instanceof DrillItem) {
                    // Clear all upgrade slots when drill is removed
                    for (UpgradeType type : UpgradeType.values()) {
                        int slotIndex = LaserScreenHandler.getSlotIndexForUpgradeType(type);
                        if (slotIndex >= 0) {
                            LaserScreenHandler.this.getSlot(slotIndex).setStack(ItemStack.EMPTY);
                        }
                    }
                }
            }
        });

        // Create upgrade slots using the helper method with correct indices and coordinates
        this.addSlot(createUpgradeSlot(1, 8, 48, UpgradeType.RED));     // red - index 1 at (8, 48)
        this.addSlot(createUpgradeSlot(2, 112, 20, UpgradeType.BLUE));   // blue - index 2 at (44, 20)
        this.addSlot(createUpgradeSlot(3, 8, 20, UpgradeType.YELLOW));  // yellow - index 3 at (8, 20)
        this.addSlot(createUpgradeSlot(4, 112, 48, UpgradeType.GRAY));   // gray - index 4 at (44, 48)
        this.addSlot(createUpgradeSlot(5, 138, 56, UpgradeType.ENERGY)); // energy - index 5 at (134, 56)

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
    }

    private Slot createUpgradeSlot(int index, int x, int y, UpgradeType type) {
        return new Slot(inventory, index, x, y) {
            @Override
            public boolean canInsert(ItemStack stack) {
                if (!(inventory.getStack(0).getItem() instanceof DrillItem)) {
                    return false;
                }
                if (stack.getItem() instanceof UpgradeItem upgradeItem) {
                    return upgradeItem.getUpgradeType() == type;
                }
                return false;
            }

            @Override
            public void setStack(ItemStack stack) {
                ItemStack oldStack = this.getStack().copy();
                super.setStack(stack);
                
                // If the stack changed (either inserted or removed an upgrade)
                if (!ItemStack.areEqual(oldStack, stack)) {
                    updateDrillUpgrades();
                    inventory.markDirty();
                }
            }

            @Override
            public int getMaxItemCount() {
                return 1;
            }

            @Override
            public ItemStack takeStack(int amount) {
                ItemStack stack = super.takeStack(amount);
                if (!stack.isEmpty()) {
                    updateDrillUpgrades();
                    inventory.markDirty();
                }
                return stack;
            }
        };
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (!slot.hasStack()) {
            return itemStack;
        }

        ItemStack sourceStack = slot.getStack();
        itemStack = sourceStack.copy();
        int containerSlotCount = 6; // Drill slot + 5 upgrade slots

        // If clicking on upgrade slots (1-5)
        if (index > 0 && index < containerSlotCount) {
            // Try to move to player inventory
            if (!this.insertItem(sourceStack, containerSlotCount, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
            slot.onQuickTransfer(sourceStack, itemStack);
            updateDrillUpgrades(); // Update drill's NBT after removing upgrade
        } 
        // If clicking on drill slot (0)
        else if (index == 0) {
            // Clear upgrades first
            for (int i = 1; i < containerSlotCount; i++) {
                inventory.setStack(i, ItemStack.EMPTY);
            }
            // Then move the drill
            if (!this.insertItem(sourceStack, containerSlotCount, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
            slot.onQuickTransfer(sourceStack, itemStack);
        }
        // If clicking in player inventory
        else if (index >= containerSlotCount) {
            // If it's a drill, try to insert into drill slot
            if (sourceStack.getItem() instanceof DrillItem) {
                if (!this.insertItem(sourceStack, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            }
            // If it's an upgrade, try to insert into appropriate upgrade slot
            else if (sourceStack.getItem() instanceof UpgradeItem upgradeItem) {
                int slotIndex = getSlotIndexForUpgradeType(upgradeItem.getUpgradeType());
                if (slotIndex > 0 && !this.insertItem(sourceStack, slotIndex, slotIndex + 1, false)) {
                    return ItemStack.EMPTY;
                }
                updateDrillUpgrades(); // Update drill's NBT after adding upgrade
            }
            // For other items, try to insert into hotbar or main inventory
            else {
                if (index < this.slots.size() - 9) {
                    if (!this.insertItem(sourceStack, this.slots.size() - 9, this.slots.size(), false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.insertItem(sourceStack, containerSlotCount, this.slots.size() - 9, false)) {
                    return ItemStack.EMPTY;
                }
            }
        }

        if (sourceStack.isEmpty()) {
            slot.setStack(ItemStack.EMPTY);
        } else {
            slot.markDirty();
        }

        return itemStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    private void addPlayerInventory(PlayerInventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(PlayerInventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    public static void storeUpgrades(ItemStack drill, List<ItemStack> upgrades) {
        if (drill.getItem() instanceof DrillItem) {
            List<ItemStack> realUpgrades = upgrades.stream()
                    .filter(stack -> !stack.isEmpty())
                    .map(ItemStack::copy)
                    .toList();

            drill.set(ModDataComponentTypes.DRILL_UPGRADES, realUpgrades);
        }

    }

    public static List<ItemStack> loadUpgrades(ItemStack drill) {
        return drill.getOrDefault(ModDataComponentTypes.DRILL_UPGRADES, List.of());
    }
}
