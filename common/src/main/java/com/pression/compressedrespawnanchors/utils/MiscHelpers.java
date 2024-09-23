package com.pression.compressedrespawnanchors.utils;

import com.pression.compressedrespawnanchors.CompressedRespawnAnchors;
import com.pression.compressedrespawnanchors.recipe.RespawnFuelRecipe;

public class MiscHelpers {
    public static RespawnFuelRecipe.OverloadOutcome parseOverloadEvent(String s){
        if(s == null) return RespawnFuelRecipe.OverloadOutcome.NOTHING;
        return switch (s) {
            case "explode" -> RespawnFuelRecipe.OverloadOutcome.EXPLOSION;
            case "drop_excess" -> RespawnFuelRecipe.OverloadOutcome.DROP_EXCESS;
            case "nothing" -> RespawnFuelRecipe.OverloadOutcome.NOTHING;
            default -> {
                CompressedRespawnAnchors.LOGGER.warn("Invalid overload event {}, defaulting to 'nothing'", s);
                yield RespawnFuelRecipe.OverloadOutcome.NOTHING;
            }
        };
    }
    public static String getOverloadString(RespawnFuelRecipe.OverloadOutcome overload){
        switch (overload){
            case EXPLOSION -> {
                return "explode";
            }
            case DROP_EXCESS -> {
                return "drop_excess";
            }
            default -> {
                return "nothing";
            }
        }
    }
}
