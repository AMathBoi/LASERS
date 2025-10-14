package net.amathboi.lasers.item.client;

import net.amathboi.lasers.item.DrillItem;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class DrillRenderer extends GeoItemRenderer<DrillItem> {
    public DrillRenderer() {
        super(new DrillModel());
    }

    @Override
    public @Nullable RenderLayer getRenderType(DrillItem animatable, Identifier texture, @Nullable VertexConsumerProvider bufferSource, float partialTick) {
        return RenderLayer.getEntityTranslucent(texture);
    }
}
