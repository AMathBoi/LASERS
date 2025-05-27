package net.amathboi.lasers.block.entity.custom;

import net.amathboi.lasers.Screen.custom.LaserScreenHandler;
import net.amathboi.lasers.block.entity.ImplementedInventory;
import net.amathboi.lasers.block.entity.ModBlockEntities;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static net.amathboi.lasers.Screen.custom.LaserScreenHandler.loadUpgrades;
import static net.amathboi.lasers.Screen.custom.LaserScreenHandler.storeUpgrades;

public class LaserWorkstationEntity extends BlockEntity implements ImplementedInventory, ExtendedScreenHandlerFactory<BlockPos> {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(6, ItemStack.EMPTY);

    public LaserWorkstationEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.LASER_BE, pos, state);
    }

    public void onDrillInserted() {
        ItemStack drill = inventory.get(0);
        List<ItemStack> upgrades = loadUpgrades(drill);

        for (int i = 0; i < 5; i++) {
            if (i < upgrades.size()) {
                inventory.set(i + 1, upgrades.get(i).copy());
            } else {
                inventory.set(i + 1, ItemStack.EMPTY);
            }
        }
        markDirty();
    }

    // Called before drill is removed from slot 0
    public void onDrillRemoved() {
        ItemStack drill = inventory.get(0);
        List<ItemStack> upgrades = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            if (!inventory.get(i).isEmpty()) {
                upgrades.add(inventory.get(i).copy());
                inventory.set(i, ItemStack.EMPTY);
            }
        }

        storeUpgrades(drill, upgrades);
        markDirty();
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, inventory, registryLookup);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        Inventories.readNbt(nbt, inventory, registryLookup);
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayerEntity serverPlayerEntity) {
        return this.pos;
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Laser Workstation");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new LaserScreenHandler(syncId, playerInventory, this.pos);
    }
}
