package net.amathboi.lasers.Screen;

import net.amathboi.lasers.Screen.custom.LaserScreenHandler;
import net.amathboi.lasers.LASERS;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class ModScreenHandlers {
    public static final ScreenHandlerType<LaserScreenHandler> LASER_SCREEN_HANDLER = Registry.register(Registries.SCREEN_HANDLER, Identifier.of(LASERS.MOD_ID, "laser_screen_handler"),
            new ExtendedScreenHandlerType<>(LaserScreenHandler::new, BlockPos.PACKET_CODEC));

    public static void registerScreenHandlers() {
        LASERS.LOGGER.info("Registering Screen Handlers for " + LASERS.MOD_ID);
    }
}
