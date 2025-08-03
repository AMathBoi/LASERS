package net.amathboi.lasers.item;

import static net.amathboi.lasers.item.UpgradeType.*;

public enum UpgradeSpecific {
    HARDNESS(GRAY),
    SONAR(BLUE),
    EFFICIENCY(RED),
    HASTE(YELLOW),
    FORTUNE(YELLOW),
    BATTERY_MK1(ENERGY);

    private final UpgradeType upgradeType;

    UpgradeSpecific(UpgradeType upgradeType) {
        this.upgradeType = upgradeType;
    }
}
