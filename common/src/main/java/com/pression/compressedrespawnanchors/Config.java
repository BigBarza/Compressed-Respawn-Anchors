package com.pression.compressedrespawnanchors;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

//The loader-specific implementations should update this, so that everything else can draw from it.
public class Config {
    public static Map<Item, Integer> RESPAWN_FUELS = new HashMap<>();
    public static ItemStack OVERCHARGE_DROP = ItemStack.EMPTY;
    public static String OVERCHARGE_EVENT = "NOTHING"; //Possible values should be NOTHING, DROP_EXCESS or EXPLODE
}
