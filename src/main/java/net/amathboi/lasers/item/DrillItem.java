package net.amathboi.lasers.item;

import net.amathboi.lasers.component.ModDataComponentTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class DrillItem extends Item {
    public DrillItem(Settings settings) {
        super(settings);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType options) {
        super.appendTooltip(stack, context, tooltip, options);

        List<ItemStack> upgrades = stack.getOrDefault(ModDataComponentTypes.DRILL_UPGRADES, List.of());
        if(Screen.hasShiftDown()) {
            if (!upgrades.isEmpty()) {
                MutableText header = (MutableText) Text.translatable("tooltip.lasers.drill_upgrades");
                header.setStyle(Style.EMPTY.withFormatting(Formatting.GRAY));
                tooltip.add(header);


                for (ItemStack up : upgrades) {
                    Formatting color = Formatting.WHITE;
                    if (up.getItem() instanceof UpgradeItem ui) {
                        switch (ui.getUpgradeType()) {
                            case YELLOW -> color = Formatting.YELLOW;
                            case BLUE -> color = Formatting.AQUA;
                            case RED -> color = Formatting.RED;
                            case GRAY -> color = Formatting.GRAY;
                            case ENERGY -> color = Formatting.WHITE;
                        }
                    }

                    MutableText bullet = (MutableText) Text.literal("  • ");
                    bullet.setStyle(Style.EMPTY.withFormatting(Formatting.DARK_GRAY));

                    MutableText name = (MutableText) up.getName().copy();
                    name.setStyle(Style.EMPTY.withFormatting(color));

                    bullet.append(name);
                    tooltip.add(bullet);
                }
            }
        } else {
                tooltip.add(Text.translatable("tooltip.lasers.shift"));
            }
    }
}
