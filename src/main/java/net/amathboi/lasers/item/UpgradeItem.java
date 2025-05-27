package net.amathboi.lasers.item;

import net.minecraft.item.Item;

public class UpgradeItem extends Item {
    private final UpgradeType upgradeType;

    public UpgradeItem(Settings settings, UpgradeType upgradeType) {
        super(settings);
        this.upgradeType = upgradeType;
    }

    public UpgradeType getUpgradeType() {
        return upgradeType;
    }
}
