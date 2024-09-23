package com.pression.compressedrespawnanchors.forge;

import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import com.pression.compressedrespawnanchors.CompressedRespawnAnchors;

@Mod(CompressedRespawnAnchors.MOD_ID)
public final class CRAForge {
    public CRAForge() {
        // Submit our event bus to let Architectury API register our content on the right time.
        EventBuses.registerModEventBus(CompressedRespawnAnchors.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        // Run our common setup.
        CompressedRespawnAnchors.init();
    }
}
