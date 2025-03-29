package net.amathboi.lasers.item;

import net.amathboi.lasers.LASERS;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {

    public static final Item LASER_DRILL_MK1 = registerItem("laser_drill_mk1", new Item(new Item.Settings()));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(LASERS.MOD_ID, name), item);
    }

    public static void registerModItems() {
        LASERS.LOGGER.info("Registering Mod Items for " + LASERS.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.add(LASER_DRILL_MK1);
        });
    }
}
