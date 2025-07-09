package net.amathboi.lasers.mixin;

import net.amathboi.lasers.item.DrillItem;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    @Inject(method = "swingHand", at = @At("HEAD"), cancellable = true)
    private void onSwingHand(CallbackInfo ci) {
        ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
        ItemStack stack = player.getMainHandStack();

        if (stack.getItem() instanceof DrillItem) {
            ci.cancel();
        }
    }
}
