package com.pression.compressedrespawnanchors.recipe;

import com.pression.compressedrespawnanchors.CompressedRespawnAnchors;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class RecipeTypes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(CompressedRespawnAnchors.MOD_ID, Registry.RECIPE_TYPE_REGISTRY);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(CompressedRespawnAnchors.MOD_ID, Registry.RECIPE_SERIALIZER_REGISTRY);

        public static final RegistrySupplier<RecipeType<RespawnFuelRecipe>> RESPAWN_FUEL_RECIPE_TYPE = RECIPE_TYPES.register("respawn_anchor_fuel", () -> new RecipeType<RespawnFuelRecipe>() {
        @Override
        public String toString() {
            return new ResourceLocation(CompressedRespawnAnchors.MOD_ID, "respawn_anchor_fuel").toString();
        }
    });
    
    public static final RegistrySupplier<RecipeSerializer<RespawnFuelRecipe>> RESPAWN_FUEL_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("respawn_anchor_fuel", RespawnFuelRecipe.Serializer::new);
}
