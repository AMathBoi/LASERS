package net.amathboi.lasers;

import net.amathboi.lasers.Screen.ModScreenHandlers;
import net.amathboi.lasers.Screen.custom.LaserScreen;
import net.amathboi.lasers.client.ModKeyBindings;
import net.amathboi.lasers.network.ModPackets;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class LASERSClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HandledScreens.register(ModScreenHandlers.LASER_SCREEN_HANDLER, LaserScreen::new);
        ModKeyBindings.registerKeyBindings();
        ModPackets.registerC2SPackets();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (ModKeyBindings.sonarKey.wasPressed() && client.player != null) {
                ClientPlayNetworking.send(ModPackets.EmptyPayload.INSTANCE);
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(ModPackets.EmptyPayload.ID, (payload, context) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null) {
                client.execute(() -> {
                    triggerSonarEffect(client.world, client.player);
                });
            }
        });
    }

    public static void triggerSonarEffect(World world, PlayerEntity player) {
        if (world.isClient) {
            ClientPlayNetworking.send(ModPackets.EmptyPayload.INSTANCE);
        }
    }
}