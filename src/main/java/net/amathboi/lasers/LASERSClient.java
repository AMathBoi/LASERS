package net.amathboi.lasers;

import net.amathboi.lasers.Screen.ModScreenHandlers;
import net.amathboi.lasers.Screen.custom.LaserScreen;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class LASERSClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HandledScreens.register(ModScreenHandlers.LASER_SCREEN_HANDLER, LaserScreen::new);
        LASERS.LOGGER.info("Client Initialized testing");
    }
}
