package net.amathboi.lasers.util;

import net.amathboi.lasers.LASERS;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> INCORRECT_FOR_LASER_MK1_TOOL = createTag("incorrect_for_laser_mk1_tool");

        private static TagKey<Block> createTag(String name) {
            return TagKey.of(RegistryKeys.BLOCK, Identifier.of(LASERS.MOD_ID, name));
        }

        public static void registerModTags() {
            LASERS.LOGGER.info("Registering Tags for " + LASERS.MOD_ID);
        }
    }
}
