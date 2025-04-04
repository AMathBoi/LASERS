package net.amathboi.lasers.block.entity;

import net.amathboi.lasers.LASERS;
import net.amathboi.lasers.block.ModBlocks;
import net.amathboi.lasers.block.entity.custom.LaserWorkstationEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static final BlockEntityType<LaserWorkstationEntity> LASER_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(LASERS.MOD_ID, "laser_be"), BlockEntityType.Builder.create(LaserWorkstationEntity::new, ModBlocks.LASER_WORKSTATION).build(null));

    public static void registerBlockEntities() {
        LASERS.LOGGER.info("Registering Block Entities for " + LASERS.MOD_ID);
    }
}
