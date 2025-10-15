package net.amathboi.lasers.component;

import com.mojang.serialization.Codec;
import net.amathboi.lasers.LASERS;
import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.UnaryOperator;

public class ModDataComponentTypes {

    public static final ComponentType<List<ItemStack>> DRILL_UPGRADES = register(
            "drill_upgrades",
            builder -> builder.codec(ItemStack.CODEC.listOf()).packetCodec(ItemStack.PACKET_CODEC.collect(PacketCodecs.toList()))
    );

    public static final ComponentType<Long> BATTERY = register(
            "battery",
            longBuilder -> longBuilder.codec(Codec.LONG)
    );

    private static <T> ComponentType<T> register(String name, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(LASERS.MOD_ID, name), builderOperator.apply(ComponentType.builder()).build());
    }

    public static void registerDataComponentTypes() {
        LASERS.LOGGER.info("Registering Data Component Types for " + LASERS.MOD_ID);
    }
}
