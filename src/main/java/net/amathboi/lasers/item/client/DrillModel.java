package net.amathboi.lasers.item.client;

import net.amathboi.lasers.LASERS;
import net.amathboi.lasers.item.DrillItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class DrillModel extends GeoModel<DrillItem> {
    @Override
    public Identifier getModelResource(DrillItem animatable) {
        return Identifier.of(LASERS.MOD_ID, "geo/laser_drill_mk1.geo.json");
    }

    @Override
    public Identifier getTextureResource(DrillItem animatable) {
        return Identifier.of(LASERS.MOD_ID, "textures/item/main.png");
    }

    @Override
    public Identifier getAnimationResource(DrillItem animatable) {
        return Identifier.of(LASERS.MOD_ID, "animations/laser_drill_mk1.animation.json");
    }
}
