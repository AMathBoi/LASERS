package net.amathboi.lasers.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.amathboi.lasers.item.DrillItem;

public class ModPackets {
    public static final Identifier SONAR_PACKET_ID = Identifier.of("lasers", "sonar_activate");

    public static void registerC2SPackets() {
        PayloadTypeRegistry.playC2S().register(EmptyPayload.ID, EmptyPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(EmptyPayload.ID, EmptyPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(EmptyPayload.ID, (payload, context) -> {
            if (context.player() != null) {
                ServerPlayerEntity serverPlayer = (ServerPlayerEntity) context.player();
                ItemStack stack = serverPlayer.getMainHandStack();
                if (stack.getItem() instanceof DrillItem) {
                    for (ServerPlayerEntity player : PlayerLookup.tracking(serverPlayer)) {
                        ServerPlayNetworking.send(player, EmptyPayload.INSTANCE);
                    }
                }
            }
        });
    }

    public record EmptyPayload() implements CustomPayload {
        public static final CustomPayload.Id<EmptyPayload> ID = new CustomPayload.Id<>(SONAR_PACKET_ID);
        public static final EmptyPayload INSTANCE = new EmptyPayload();
        public static final PacketCodec<PacketByteBuf, EmptyPayload> CODEC = PacketCodec.unit(INSTANCE);

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }
}