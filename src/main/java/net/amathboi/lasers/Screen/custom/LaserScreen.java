package net.amathboi.lasers.Screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import net.amathboi.lasers.LASERS;
import net.amathboi.lasers.component.ModDataComponentTypes;
import net.amathboi.lasers.item.DrillItem;
import net.amathboi.lasers.item.UpgradeItem;
import net.amathboi.lasers.item.UpgradeSpecific;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.entity.player.PlayerInventory;

public class LaserScreen extends HandledScreen<LaserScreenHandler> {

    public static final Identifier GUI_TEXTURE = Identifier.of(LASERS.MOD_ID, "textures/gui/lasers/laser_workstation_gui.png");
    public static final Identifier EMPTY       = Identifier.of(LASERS.MOD_ID, "textures/gui/lasers/empty_bar.png");
    public static final Identifier FULL        = Identifier.of(LASERS.MOD_ID, "textures/gui/lasers/full_bar.png");

    private static final int BACKGROUND_WIDTH  = 176;
    private static final int BACKGROUND_HEIGHT = 166;

    private static final int BAR_WIDTH  = 5;
    private static final int BAR_HEIGHT = 64;

    private static final int BAR_X = 163;
    private static final int BAR_Y = 8;

    public LaserScreen(LaserScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth  = BACKGROUND_WIDTH;
        this.backgroundHeight = BACKGROUND_HEIGHT;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);

        int x = (this.width  - this.backgroundWidth)  / 2;
        int y = (this.height - this.backgroundHeight) / 2;

        context.drawTexture(GUI_TEXTURE, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);

        RenderSystem.setShaderTexture(0, EMPTY);
        context.drawTexture(EMPTY,
                x + BAR_X,
                y + BAR_Y,
                0, 0,
                BAR_WIDTH, BAR_HEIGHT);

        ItemStack drillStack = this.handler.getSlot(0).getStack();
        if (drillStack.getItem() instanceof DrillItem) {
            long currentEnergy = drillStack.getOrDefault(ModDataComponentTypes.BATTERY, 0L);

            boolean hasMk1 = LaserScreenHandler.loadUpgrades(drillStack).stream()
                    .anyMatch(up -> up.getItem() instanceof UpgradeItem ui
                            && ui.getUpgradeSpecific() == UpgradeSpecific.BATTERY_MK1);
            long capacity = hasMk1 ? 50_000L : 0L;

            if (capacity > 0 && currentEnergy > 0) {
                double fillFrac = (double) currentEnergy / (double) capacity;
                if (fillFrac < 0) fillFrac = 0;
                if (fillFrac > 1) fillFrac = 1;
                int filledHeight = (int) Math.round(fillFrac * BAR_HEIGHT);
                int srcV      = BAR_HEIGHT - filledHeight;
                int destY     = y + BAR_Y     + (BAR_HEIGHT - filledHeight);

                RenderSystem.setShaderTexture(0, FULL);
                context.drawTexture(FULL,
                        x + BAR_X,
                        destY,
                        0,
                        srcV,
                        BAR_WIDTH,
                        filledHeight);
            }
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }
}

