package net.amathboi.lasers.block.entity.custom;

import net.amathboi.lasers.Screen.custom.LaserScreenHandler;
import net.amathboi.lasers.block.entity.ImplementedInventory;
import net.amathboi.lasers.block.entity.ModBlockEntities;
import net.amathboi.lasers.energy.DrillEnergyStorage;
import net.amathboi.lasers.energy.WorkstationEnergyStorage;
import net.amathboi.lasers.item.DrillItem;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
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
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class LaserWorkstationEntity extends BlockEntity implements ImplementedInventory, ExtendedScreenHandlerFactory<BlockPos> {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(6, ItemStack.EMPTY);
    public final WorkstationEnergyStorage energyStorage;

    public LaserWorkstationEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.LASER_BE, pos, state);
        this.energyStorage = new WorkstationEnergyStorage(50_000, 2_000L, 1_000L);
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

    public void tick(World world, BlockPos pos, BlockState state) {
        if (world.isClient) return;

        ItemStack drill = inventory.get(0);
        if (drill.getItem() instanceof DrillItem) {
            DrillEnergyStorage drillEnergy = new DrillEnergyStorage(drill);

            try (Transaction tx = Transaction.openOuter()) {
                long pulled   = energyStorage.extract(1_000L, tx);
                long accepted = drillEnergy.insert(pulled, tx);
                if (accepted > 0) tx.commit();
            }
        }
    }
}
