package com.pression.compressedrespawnanchors;

import com.pression.compressedrespawnanchors.recipe.RecipeTypes;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

public final class CompressedRespawnAnchors {
    public static final String MOD_ID = "compressed_respawn_anchors";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static void init() {
        // Write common init code here.
        RecipeTypes.RECIPE_TYPES.register();
        RecipeTypes.RECIPE_SERIALIZERS.register();
    }
}
