package net.amathboi.lasers.item;

import net.amathboi.lasers.component.ModDataComponentTypes;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


import java.util.List;

public class DrillItem extends PickaxeItem {
    public DrillItem(ModToolMaterials material, Settings settings) {
        super(material, settings);
    }

    @Override
    public int getMaxCount() {
        return 1;
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return true;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public int getEnchantability() {
        return 0;
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType options) {
        super.appendTooltip(stack, context, tooltip, options);

        List<ItemStack> upgrades = stack.getOrDefault(ModDataComponentTypes.DRILL_UPGRADES, List.of());
        if(Screen.hasShiftDown()) {
            if (!upgrades.isEmpty()) {
                MutableText header = Text.translatable("tooltip.lasers.drill_upgrades");
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
                        }
                    }

                    MutableText bullet = Text.literal("  • ");
                    bullet.setStyle(Style.EMPTY.withFormatting(Formatting.DARK_GRAY));

                    MutableText name = up.getName().copy();
                    name.setStyle(Style.EMPTY.withFormatting(color));

                    bullet.append(name);
                    tooltip.add(bullet);
                }
            }
        } else {
                tooltip.add(Text.translatable("tooltip.lasers.shift"));
            }
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return true;
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        return true;
    }

    @Override
    public float getMiningSpeed(ItemStack stack, BlockState state) {
        return ModToolMaterials.LASER_MK1.getMiningSpeedMultiplier();
    }
}
