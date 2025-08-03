package net.amathboi.lasers.item;

import net.amathboi.lasers.LASERS;
import net.amathboi.lasers.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {

    public static final ItemGroup LASERS_GROUP = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(LASERS.MOD_ID, "laser_drill_mk1"),
            FabricItemGroup.builder().icon(() -> new ItemStack(ModBlocks.LASER_WORKSTATION))
                    .displayName(Text.translatable("itemgroup.lasers.lasers_items"))
                    .entries((displayContext, entries) -> {
                        entries.add(ModItems.LASER_DRILL_MK1);
                        entries.add(ModBlocks.LASER_WORKSTATION);

                        entries.add(ModItems.BLANK_UPGRADE);
                        entries.add(ModItems.EFFICIENCY_UPGRADE);
                        entries.add(ModItems.ENERGY_STORAGE_MK1);
                        entries.add(ModItems.HARDNESS_UPGRADE);
                        entries.add(ModItems.HASTE_UPGRADE);
                        entries.add(ModItems.SONAR_UPGRADE);
                        entries.add(ModItems.FORTUNE_UPGRADE);
                    }).build());

    public static void registerItemGroups() {
        LASERS.LOGGER.info("Registering Item Groups for " + LASERS.MOD_ID);
    }
}
