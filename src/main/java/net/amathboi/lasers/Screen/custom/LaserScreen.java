package net.amathboi.lasers.Screen.custom;

import com.mojang.blaze3d.systems.RenderSystem;
import net.amathboi.lasers.LASERS;
import net.amathboi.lasers.component.ModDataComponentTypes;
import net.amathboi.lasers.item.DrillItem;
import net.amathboi.lasers.item.UpgradeItem;
import net.amathboi.lasers.item.UpgradeSpecific;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.client.gui.tooltip.Tooltip;

import java.util.List;

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

    private static final int INFO_BUTTON_SIZE = 20;
    private static final int INFO_BUTTON_X = -20;
    private static final int INFO_BUTTON_Y = 5;
    private boolean showInfo = false;
    private ButtonWidget infoButton;

    private static final int INFO_PANEL_WIDTH = 125;
    private static final int INFO_PANEL_PADDING = 5;
    private static final int LINE_HEIGHT = 10;
    private static final List<Text> INFO_TEXT = List.of(
            Text.literal("§6§lLaser Workstation Guide"),
            Text.literal("1. Place drill in the middle slot"),
            Text.literal("2. Add battery(required)"),
            Text.literal("3. Remove the drill to"),
            Text.literal("   implement upgrades"),
            Text.literal("4. Different upgrades provide"),
            Text.literal("   different benefits"),
            Text.literal(""),
            Text.literal("§7Click the ? to close")
    );

    private static final float TEXT_SCALE = 0.75f;

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
            long capacity = hasMk1 ? 5_000L : 0L;

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

        if (this.infoButton.visible) {
            int buttonX = this.infoButton.getX();
            int buttonY = this.infoButton.getY();

            int color = this.infoButton.isHovered() ? 0x80FFFFFF : 0x40FFFFFF;
            context.fill(buttonX, buttonY,
                    buttonX + INFO_BUTTON_SIZE,
                    buttonY + INFO_BUTTON_SIZE,
                    color);

            context.drawCenteredTextWithShadow(
                    this.textRenderer,
                    "?",
                    buttonX + INFO_BUTTON_SIZE/2,
                    buttonY + (INFO_BUTTON_SIZE - this.textRenderer.fontHeight) / 2,
                    0x000000
            );
        }

        if (showInfo && this.infoButton.visible) {
            int buttonX = this.infoButton.getX();
            int buttonY = this.infoButton.getY();
            int panelX = buttonX + INFO_BUTTON_SIZE - INFO_PANEL_WIDTH;  // Align right edge with button
            int panelY = buttonY + INFO_BUTTON_SIZE;
            int panelHeight = (int)((INFO_TEXT.size() * LINE_HEIGHT * TEXT_SCALE) + (2 * INFO_PANEL_PADDING));

            panelX = Math.max(panelX, 5);

            context.fill(
                    panelX, panelY,
                    panelX + INFO_PANEL_WIDTH, panelY + panelHeight,
                    0xCC000000
            );

            context.drawBorder(
                    panelX, panelY,
                    INFO_PANEL_WIDTH, panelHeight,
                    0xFFFFFFFF
            );

            for (int i = 0; i < INFO_TEXT.size(); i++) {
                context.getMatrices().push();
                context.getMatrices().scale(TEXT_SCALE, TEXT_SCALE, 1f);
                float xPos = (panelX + INFO_PANEL_PADDING) / TEXT_SCALE;
                float yPos = (panelY + INFO_PANEL_PADDING + (i * (LINE_HEIGHT * TEXT_SCALE))) / TEXT_SCALE;

                context.drawTextWithShadow(
                        this.textRenderer,
                        INFO_TEXT.get(i),
                        (int)xPos,
                        (int)yPos,
                        0xFFFFFF
                );
                context.getMatrices().pop();
            }
        }

        this.drawMouseoverTooltip(context, mouseX, mouseY);

    }

    @Override
    protected void init() {
        super.init();
        this.showInfo = false;  // Reset panel state when screen is opened

        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;

        this.infoButton = ButtonWidget.builder(
                        Text.literal("?"),
                        button -> this.toggleInfo()
                )
                .dimensions(
                        x + INFO_BUTTON_X,
                        y + INFO_BUTTON_Y,
                        INFO_BUTTON_SIZE,
                        INFO_BUTTON_SIZE
                )
                .tooltip(Tooltip.of(Text.translatable("gui.lasers.laser_workstation.info_button.tooltip")))
                .build();

        this.addDrawableChild(this.infoButton);
    }

    private void toggleInfo() {
        this.showInfo = !this.showInfo;
    }
}

