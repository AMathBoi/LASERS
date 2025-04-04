package net.amathboi.lasers;

import net.amathboi.lasers.Screen.ModScreenHandlers;
import net.amathboi.lasers.block.ModBlocks;
import net.amathboi.lasers.block.entity.ModBlockEntities;
import net.amathboi.lasers.item.ModItemGroups;
import net.amathboi.lasers.item.ModItems;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LASERS implements ModInitializer {
	public static final String MOD_ID = "lasers";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItemGroups.registerItemGroups();

		ModItems.registerModItems();
		ModBlocks.registerModBlocks();

		ModBlockEntities.registerBlockEntities();
		ModScreenHandlers.registerScreenHandlers();
	}
}