package net.amathboi.lasers.client;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ModKeyBindings {
    public static KeyBinding sonarKey;

    public static void registerKeyBindings() {
        sonarKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.lasers.sonar",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_Z,
            "category.lasers.main"
        ));
    }
}
