package net.amathboi.lasers.item;

import net.minecraft.item.Item;

public class UpgradeItem extends Item {
    private final UpgradeSpecific upgradeSpecific;
    private final UpgradeType upgradeType;

    public UpgradeItem(Settings settings, UpgradeSpecific upgradeSpecific, UpgradeType upgradeType) {
        super(settings);
        this.upgradeSpecific = upgradeSpecific;
        this.upgradeType = upgradeType;
    }

    public UpgradeType getUpgradeType() {
        return upgradeType;
    }

    public UpgradeSpecific getUpgradeSpecific() {
        return upgradeSpecific;
    }
}
