package com.pression.compressedrespawnanchors.recipe;

import com.pression.compressedrespawnanchors.utils.MiscHelpers;
import net.minecraft.world.item.crafting.*;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;


public class RespawnFuelRecipe implements Recipe<Inventory>{
    private final ResourceLocation id;
    private final Ingredient input;
    private final int charges;
    private final OverloadOutcome overload;
    private final ItemStack droppedItem;

    public RespawnFuelRecipe(ResourceLocation id, Ingredient input, int charges, OverloadOutcome overload, ItemStack droppedItem){
        this.id = id;
        this.input = input;
        this.charges = charges;
        this.overload = overload;
        this.droppedItem = droppedItem;
    }

    @Override public ResourceLocation getId(){
        return id;
    }
    public Ingredient getInput(){
        return input;
    }
    public int getCharges(){
        return charges;
    }
    public OverloadOutcome getOverload(){
        return overload;
    }
    @Override public ItemStack getResultItem(){
        return droppedItem.copy();
    }
    @Override public RecipeSerializer<?> getSerializer(){
        return RecipeTypes.RESPAWN_FUEL_RECIPE_SERIALIZER.get();
    }
    @Override public RecipeType<?> getType(){
        return RecipeTypes.RESPAWN_FUEL_RECIPE_TYPE.get();
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

    public enum OverloadOutcome {
        EXPLOSION,
        DROP_EXCESS,
        NOTHING;
    }

        public static class Serializer implements RecipeSerializer<RespawnFuelRecipe>{
        @Override
        public RespawnFuelRecipe fromJson(ResourceLocation id, JsonObject json) {
            Ingredient input = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "input"));
            int charges = GsonHelper.getAsInt(json, "charges");
            OverloadOutcome overload = OverloadOutcome.NOTHING;
            if(json.has("overload")){
                overload = MiscHelpers.parseOverloadEvent(GsonHelper.getAsString(json, "overload"));
            }
            ItemStack drop = ItemStack.EMPTY;
            if(overload == OverloadOutcome.DROP_EXCESS && json.has("dropped_item")){
                drop = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "dropped_item"));
            }
            return new RespawnFuelRecipe(id, input, charges, overload, drop);
        }

        @Override
        public @Nullable RespawnFuelRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            Ingredient input = Ingredient.fromNetwork(buf);
            int charges = buf.readInt();
            OverloadOutcome overload = MiscHelpers.parseOverloadEvent(buf.readUtf());
            ItemStack drop = buf.readItem();
            return new RespawnFuelRecipe(id, input, charges, overload, drop);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, RespawnFuelRecipe recipe) {
            buf.writeResourceLocation(recipe.getId());
            recipe.getInput().toNetwork(buf);
            buf.writeInt(recipe.getCharges());
            buf.writeUtf(MiscHelpers.getOverloadString(recipe.getOverload()));
            buf.writeItem(recipe.getResultItem());
        }
    }

}