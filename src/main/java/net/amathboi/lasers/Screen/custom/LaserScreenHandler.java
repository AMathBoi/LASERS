package net.amathboi.lasers.Screen.custom;

import net.amathboi.lasers.Screen.ModScreenHandlers;
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

    public LaserScreenHandler(int syncId, PlayerInventory playerInventory, BlockEntity blockEntity) {
        super(ModScreenHandlers.LASER_SCREEN_HANDLER, syncId);
        this.inventory = ((Inventory) blockEntity);

        //drill
        this.addSlot(new Slot(inventory, 0, 80, 32) {
            @Override
            public void onTakeItem(PlayerEntity player, ItemStack stack) {
                super.onTakeItem(player, stack);

                if (!stack.isEmpty() && stack.getItem() instanceof DrillItem) {
                    List<ItemStack> upgrades = new ArrayList<>();
                    for (int i = 1; i <= 5; i++) {
                        ItemStack slotStack = inventory.getStack(i);
                        if (!slotStack.isEmpty()) {
                            upgrades.add(slotStack.copy());
                            inventory.setStack(i, ItemStack.EMPTY);
                        }
                    }
                    storeUpgrades(stack, upgrades);
                }
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
                    for (UpgradeType type : UpgradeType.values()) {
                        int slotIndex = LaserScreenHandler.getSlotIndexForUpgradeType(type);
                        if (slotIndex >= 0) {
                            LaserScreenHandler.this.getSlot(slotIndex).setStack(ItemStack.EMPTY);
                        }
                    }
                }
            }

        });
        //red
        this.addSlot(new Slot(inventory, 1, 8, 48) {
            @Override
            public boolean canInsert(ItemStack stack) {
                if (stack.getItem() instanceof UpgradeItem upgradeItem) {
                    return upgradeItem.getUpgradeType() == UpgradeType.RED;
                }
                return false;
            }

            @Override
            public void setStack(ItemStack stack) {
                super.setStack(stack);
                blockEntity.markDirty();
            }

            @Override
            public int getMaxItemCount() {
                return 1;
            }
        });
        //blue
        this.addSlot(new Slot(inventory, 2, 44, 20) {
            @Override
            public boolean canInsert(ItemStack stack) {
                if (stack.getItem() instanceof UpgradeItem upgradeItem) {
                    return upgradeItem.getUpgradeType() == UpgradeType.BLUE;
                }
                return false;
            }

            @Override
            public void setStack(ItemStack stack) {
                super.setStack(stack);
                blockEntity.markDirty();
            }

            @Override
            public int getMaxItemCount() {
                return 1;
            }
        });
        //gray
        this.addSlot(new Slot(inventory, 4, 44, 48) {
            @Override
            public boolean canInsert(ItemStack stack) {
                if (stack.getItem() instanceof UpgradeItem upgradeItem) {
                    return upgradeItem.getUpgradeType() == UpgradeType.GRAY;
                }
                return false;
            }

            @Override
            public void setStack(ItemStack stack) {
                super.setStack(stack);
                blockEntity.markDirty();
            }

            @Override
            public int getMaxItemCount() {
                return 1;
            }
        });
        //yellow
        this.addSlot(new Slot(inventory, 3, 8, 20) {
            @Override
            public boolean canInsert(ItemStack stack) {
                if (stack.getItem() instanceof UpgradeItem upgradeItem) {
                    return upgradeItem.getUpgradeType() == UpgradeType.YELLOW;
                }
                return false;
            }

            @Override
            public void setStack(ItemStack stack) {
                super.setStack(stack);
                blockEntity.markDirty();
            }

            @Override
            public int getMaxItemCount() {
                return 1;
            }
        });
        //battery
        this.addSlot(new Slot(inventory, 5, 134, 56) {
            @Override
            public boolean canInsert(ItemStack stack) {
                if (stack.getItem() instanceof UpgradeItem upgradeItem) {
                    return upgradeItem.getUpgradeType() == UpgradeType.ENERGY;
                }
                return false;
            }

            @Override
            public void setStack(ItemStack stack) {
                super.setStack(stack);
                blockEntity.markDirty();
            }

            @Override
            public int getMaxItemCount() {
                return 1;
            }
        });

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        Slot sourceSlot = this.slots.get(index);
        if (!sourceSlot.hasStack()) {
            return ItemStack.EMPTY;
        }

        ItemStack sourceStack = sourceSlot.getStack();
        ItemStack copyOfSourceStack = sourceStack.copy();

        int containerSlotCount = 5;

        if (index < containerSlotCount) {
            if (!this.insertItem(sourceStack, containerSlotCount, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else {
            boolean moved = false;
            for (int i = 0; i < containerSlotCount; i++) {
                Slot targetSlot = this.slots.get(i);

                if (!targetSlot.hasStack() && targetSlot.canInsert(sourceStack)) {
                    ItemStack oneItem = sourceStack.split(1);
                    targetSlot.setStack(oneItem);
                    targetSlot.markDirty();
                    moved = true;
                    break;
                }
            }

            if (!moved) {
                return ItemStack.EMPTY;
            }
        }

        if (sourceStack.isEmpty()) {
            sourceSlot.setStack(ItemStack.EMPTY);
        } else {
            sourceSlot.markDirty();
        }

        return copyOfSourceStack;
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
