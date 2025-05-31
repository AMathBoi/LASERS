package net.amathboi.lasers.block.custom;

import com.mojang.serialization.MapCodec;
import net.amathboi.lasers.block.entity.custom.LaserWorkstationEntity;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class LaserWorkstation extends BlockWithEntity implements BlockEntityProvider {

    public static final MapCodec<LaserWorkstation> CODEC = LaserWorkstation.createCodec(LaserWorkstation::new);

    public LaserWorkstation(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new LaserWorkstationEntity(pos, state);
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if(state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if(blockEntity instanceof LaserWorkstationEntity) {
                ItemScatterer.spawn(world, pos, ((LaserWorkstationEntity) blockEntity));
                world.updateComparators(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if(world.getBlockEntity(pos) instanceof LaserWorkstationEntity laserWorkstationEntity) {
            if (!world.isClient()) {
                player.openHandledScreen(laserWorkstationEntity);
            }
        }
        return ItemActionResult.SUCCESS;
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            World world,
            BlockState state,
            BlockEntityType<T> type
    ) {
        if (world.isClient) return null;

        return (world1, pos, state1, entity) -> {
            if (entity instanceof LaserWorkstationEntity lwe) {
                lwe.tick(world1, pos, state1);
            }
        };
    }
}


