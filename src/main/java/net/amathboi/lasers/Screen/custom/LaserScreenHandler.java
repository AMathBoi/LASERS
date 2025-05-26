package net.amathboi.lasers.Screen.custom;

import net.amathboi.lasers.Screen.ModScreenHandlers;
import net.amathboi.lasers.item.ModItems;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;

public class LaserScreenHandler extends ScreenHandler {
    private final Inventory inventory;

    public LaserScreenHandler(int syncId, PlayerInventory playerInventory, BlockPos pos) {
        this(syncId, playerInventory, playerInventory.player.getWorld().getBlockEntity(pos));
    }

    public LaserScreenHandler(int syncId, PlayerInventory playerInventory, BlockEntity blockEntity) {
        super(ModScreenHandlers.LASER_SCREEN_HANDLER, syncId);
        this.inventory = ((Inventory) blockEntity);

        //yellow
        this.addSlot(new Slot(inventory, 0, 8, 20) {
            @Override
            public int getMaxItemCount() {
                return 1;
            }
        });
        //red
        this.addSlot(new Slot(inventory, 1, 8, 47) {
            @Override
            public int getMaxItemCount() {
                return 1;
            }
        });
        //blue
        this.addSlot(new Slot(inventory, 2, 44, 20) {
            @Override
            public int getMaxItemCount() {
                return 1;
            }
        });
        //gray
        this.addSlot(new Slot(inventory, 3, 44, 47) {
            @Override
            public int getMaxItemCount() {
                return 1;
            }
        });
        //drill
        this.addSlot(new Slot(inventory, 4, 80, 32) {
            @Override
            public int getMaxItemCount() {
                return 1;
            }

            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.isOf(ModItems.LASER_DRILL_MK1);
            }
        });
        //battery
        this.addSlot(new Slot(inventory, 5, 134, 56));

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
            // Moving from container to player inventory
            if (!this.insertItem(sourceStack, containerSlotCount, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else {
            // Moving from player inventory to container (max 1 item per slot)
            boolean moved = false;
            for (int i = 0; i < containerSlotCount; i++) {
                Slot targetSlot = this.slots.get(i);

                if (!targetSlot.hasStack() && targetSlot.canInsert(sourceStack)) {
                    // Take one item from source and place it in target
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
    //this is for shift left click, does all items, should be specific to slot

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

}
