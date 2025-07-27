package net.amathboi.lasers;

import net.amathboi.lasers.Screen.ModScreenHandlers;
import net.amathboi.lasers.Screen.custom.LaserScreen;
import net.amathboi.lasers.client.ModKeyBindings;
import net.amathboi.lasers.client.SonarEffect;
import net.amathboi.lasers.item.DrillItem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class LASERSClient implements ClientModInitializer {
    private static boolean wasKeyPressed = false;
    
    @Override
    public void onInitializeClient() {
        ModKeyBindings.registerKeyBindings();
        
        HandledScreens.register(ModScreenHandlers.LASER_SCREEN_HANDLER, LaserScreen::new);
        ModScreenHandlers.registerScreenHandlers();

        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null && client.world != null) {
                SonarEffect.render(drawContext, tickDelta.getTickDelta(true));
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (ModKeyBindings.sonarKey.wasPressed()) {
                ClientPlayerEntity player = client.player;
                if (player != null) {
                    for (Hand hand : Hand.values()) {
                        ItemStack stack = player.getStackInHand(hand);
                        if (stack.getItem() instanceof DrillItem drillItem) {
                            if (drillItem.tryActivateSonar(stack, player)) {
                                SonarEffect.scanForOres(player.getWorld(), player);
                            }
                            break;
                        }
                    }
                }
            }
        });
    }
}