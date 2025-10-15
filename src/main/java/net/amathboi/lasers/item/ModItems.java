package net.amathboi.lasers.item;

import net.amathboi.lasers.LASERS;
import net.amathboi.lasers.client.ModKeyBindings;
import net.amathboi.lasers.component.ModDataComponentTypes;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.List;

public class ModItems {

    public static final Item LASER_DRILL_MK1 = registerItem("laser_drill_mk1",
            new DrillItem(
                    ModToolMaterials.LASER_MK1,
                    new Item.Settings().component(ModDataComponentTypes.DRILL_UPGRADES, List.of()).attributeModifiers(PickaxeItem.createAttributeModifiers(ModToolMaterials.LASER_MK1,
                            1, -2.8f)).component(ModDataComponentTypes.BATTERY, 0L)));

    public static final Item BLANK_UPGRADE = registerItem("blank_upgrade", new Item(new Item.Settings()));
    public static final Item EFFICIENCY_UPGRADE = registerItem("efficiency_upgrade", new UpgradeItem(new Item.Settings().maxCount(1), UpgradeSpecific.EFFICIENCY, UpgradeType.RED) {
        @Override
        public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
            if (Screen.hasShiftDown()) {
                tooltip.add(Text.translatable("tooltip.lasers.efficiency_upgrade.shift_down"));
            } else {
                tooltip.add(Text.translatable("tooltip.lasers.shift"));
            }
            super.appendTooltip(stack, context, tooltip, type);
        }
    });
    public static final Item ENERGY_STORAGE_MK1 = registerItem("energy_storage_mk1", new UpgradeItem(new Item.Settings().maxCount(1), UpgradeSpecific.BATTERY_MK1, UpgradeType.ENERGY) {
        @Override
        public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
            if (Screen.hasShiftDown()) {
                tooltip.add(Text.translatable("tooltip.lasers.energy_storage_mk1.shift_down"));
            } else {
                tooltip.add(Text.translatable("tooltip.lasers.shift"));
            }
            super.appendTooltip(stack, context, tooltip, type);
        }
    });
    public static final Item HARDNESS_UPGRADE = registerItem("hardness_upgrade", new UpgradeItem(new Item.Settings().maxCount(1), UpgradeSpecific.HARDNESS, UpgradeType.GRAY) {
        @Override
        public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
            if (Screen.hasShiftDown()) {
                tooltip.add(Text.translatable("tooltip.lasers.hardness_upgrade.shift_down"));
            } else {
                tooltip.add(Text.translatable("tooltip.lasers.shift"));
            }
            super.appendTooltip(stack, context, tooltip, type);
        }
    });
    public static final Item HASTE_UPGRADE = registerItem("haste_upgrade", new UpgradeItem(new Item.Settings().maxCount(1), UpgradeSpecific.HASTE, UpgradeType.YELLOW) {
        @Override
        public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
            if (Screen.hasShiftDown()) {
                tooltip.add(Text.translatable("tooltip.lasers.haste_upgrade.shift_down"));
            } else {
                tooltip.add(Text.translatable("tooltip.lasers.shift"));
            }
            super.appendTooltip(stack, context, tooltip, type);
        }
    });
    public static final Item SONAR_UPGRADE = registerItem("sonar_upgrade", new UpgradeItem(new Item.Settings().maxCount(1), UpgradeSpecific.SONAR, UpgradeType.BLUE) {
        @Override
        public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
            if (Screen.hasShiftDown()) {
                tooltip.add(Text.translatable("tooltip.lasers.sonar_upgrade.shift_down"));
                tooltip.add(Text.translatable("tooltip.lasers.sonar_upgrade.shift_down2"));
                Text keyText = ModKeyBindings.sonarKey.getBoundKeyLocalizedText();
                tooltip.add(Text.translatable("tooltip.lasers.sonar_key", keyText).formatted(Formatting.GRAY));
            } else {
                tooltip.add(Text.translatable("tooltip.lasers.shift"));
            }
            super.appendTooltip(stack, context, tooltip, type);
        }
    });
    public static final Item FORTUNE_UPGRADE = registerItem("fortune_upgrade", new UpgradeItem(new Item.Settings().maxCount(1), UpgradeSpecific.FORTUNE, UpgradeType.YELLOW) {
        @Override
        public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
            if (Screen.hasShiftDown()) {
                tooltip.add(Text.translatable("tooltip.lasers.fortune_upgrade"));
            } else {
                tooltip.add(Text.translatable("tooltip.lasers.shift"));
            }
            super.appendTooltip(stack, context, tooltip, type);
        }
    });

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(LASERS.MOD_ID, name), item);
    }

    public static void registerModItems() {
        LASERS.LOGGER.info("Registering Mod Items for " + LASERS.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.add(LASER_DRILL_MK1);
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.add(BLANK_UPGRADE);
            entries.add(EFFICIENCY_UPGRADE);
            entries.add(ENERGY_STORAGE_MK1);
            entries.add(HARDNESS_UPGRADE);
            entries.add(HASTE_UPGRADE);
            entries.add(SONAR_UPGRADE);
            entries.add(FORTUNE_UPGRADE);
        });
    }
}
