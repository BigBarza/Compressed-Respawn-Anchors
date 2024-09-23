package com.pression.compressedrespawnanchors.recipe;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;
import com.mojang.datafixers.types.templates.List;
import com.pression.compressedrespawnanchors.CompressedRespawnAnchors;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;


public class RespawnFuelRecipe implements Recipe<Inventory>{
    private final ResourceLocation id;
    private final ItemStack input;
    private final int charges;
    private final OverloadEvent overload;
    private final ItemStack droppedItem;

    public RespawnFuelRecipe(ResourceLocation id, ItemStack input, int charges, OverloadEvent overload, ItemStack droppedItem){
        this.id = id;
        this.input = input;
        this.charges = charges;
        this.overload = overload;
        this.droppedItem = droppedItem;
    }

    @Override public ResourceLocation getId(){
        return id;
    }

    @Override public ItemStack getResultItem(){
        return droppedItem;
    }
    @Override public RecipeSerializer<?> getSerializer(){return RecipeTypes.RESPAWN_FUEL_RECIPE_SERIALIZER.get();}
    @Override public RecipeType<?> getType(){
        return RecipeTypes.RESPAWN_FUEç_RECIPE_TYPE.get();
    }
    @Override public boolean matches(Inventory inv, Level world){
        return false;
    }
    @Override public ItemStack assemble(Inventory inv){
        return ItemStack.EMPTY;
    }
    @Override public boolean canCraftInDimensions(int w, int h){
        return false;
    }

    public OverloadEvent parseOverloadEvent(String s){
        switch(s){
            case "explode":
                return OverloadEvent.EXPLOSION;
            case "drop_excess":
                return OverloadEvent.DROP_EXCESS;
            case "nothing":
                return OverloadEvent.NOTHING;
            default:
                CompressedRespawnAnchors.LOGGER.warn("Invalid overload event "+s+", defaulting to 'nothing'");
                return OverloadEvent.NOTHING;
        }
    }

    public enum OverloadEvent {
        EXPLOSION,
        DROP_EXCESS,
        NOTHING;
    }

        public static class Serializer implements RecipeSerializer<RespawnFuelRecipe>{
        @Override
        public RespawnFuelRecipe fromJson(ResourceLocation id, JsonObject json) {
            
        }

        @Override
        public @Nullable RespawnFuelRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, RespawnFuelRecipe recipe) {
            
        }
    }

}