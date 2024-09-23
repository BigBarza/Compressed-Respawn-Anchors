package com.pression.compressedrespawnanchors.mixin;

import com.pression.compressedrespawnanchors.recipe.RecipeTypes;
import com.pression.compressedrespawnanchors.recipe.RespawnFuelRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(RespawnAnchorBlock.class)
public abstract class RespawnAnchorMixin {

    //These are just placeholders to make it compile. They should use the actual methods on runtime.
    @Shadow private static boolean canBeCharged(BlockState arg) { return false; }
    @Shadow protected abstract void explode(BlockState arg, Level arg2, BlockPos arg3);

    //This is a workaround to capture the recipe.
    @Unique private RespawnFuelRecipe capturedRecipe = null;

    //I hate this. These static methods get in my way. I'm just overriding this since we have our own checks for "being a respawn fuel"
    @Inject(method = "isRespawnFuel", at = @At("HEAD"), cancellable = true)
    private static void respawnFuelCheck(ItemStack item, CallbackInfoReturnable<Boolean> cir){
        cir.setReturnValue(true);
    }
    //This is also part of the workaround, i can't capture locals with a redirect (God, that sounds wrong out of context)
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void captureRecipe(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult, CallbackInfoReturnable<InteractionResult> cir){
        List<RespawnFuelRecipe> recipes = level.getRecipeManager().getAllRecipesFor(RecipeTypes.RESPAWN_FUEL_RECIPE_TYPE.get());
        capturedRecipe = null;
        for(RespawnFuelRecipe recipe : recipes){
            if(recipe.getInput().test(player.getItemInHand(interactionHand).copy())){
                capturedRecipe = recipe;
                return;
            }
            //IF we're here, we did't find a recipe. We need to reset the previously saved recipe and abort the interaction.
            System.out.println(capturedRecipe == null ? "RECIPE IS NULL" : capturedRecipe.getId());
            if(capturedRecipe == null) cir.setReturnValue(InteractionResult.FAIL); //This replaces the check in isRespawnFuel.
        }
    }

    //So this, instead of charging the anchor, should redirect here.
    @Redirect(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/RespawnAnchorBlock;charge(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V"))
    private void onCharge(Level level, BlockPos pos, BlockState state){
        //I hope it's safe to assume there is a recipe set if we get here. Just in case though...
        if(capturedRecipe == null) return;
        int charges = capturedRecipe.getCharges();
        while(canBeCharged(state) && charges > 0){ //It's a bit dirty, but lets us delegate the checking to vanilla code.
            charges--;
            RespawnAnchorBlock.charge(level, pos, state); //This WOULD play multiple sounds. I hope it sounds nice and that we get a free indicator of a beefier recharge.
        }
        if(charges > 0){ //Now, we need handle an overcharge.
            switch (capturedRecipe.getOverload()){
                case EXPLOSION:
                    explode(state, level, pos); //haha, kablooey
                    break;
                case DROP_EXCESS:
                    dropExcessItems(capturedRecipe.getResultItem(), level, pos, charges);
                    break;
                default: //aka case NOTHING or a somehow messed up config
                    //Nothing happens. You just lose the excess charges.
                    break;
            }
        }
    }

    @Unique
    private static void dropExcessItems(ItemStack item, Level level, BlockPos pos, int amount){
        if(item.isEmpty()) return;
        int stackSize = Math.min(amount, item.getMaxStackSize()); //It's a bad idea to spawn oversized stacks.
        item.setCount(stackSize);
        ItemEntity entity = new ItemEntity(level, pos.getX()+0.5, pos.getY()+1 ,pos.getZ()+0.5, item);
        entity.setDeltaMovement(0,0.5,0);
        level.addFreshEntity(entity);
        if(amount > item.getMaxStackSize()) dropExcessItems(item, level,pos,amount-item.getMaxStackSize()); //Yay, recursion!
    }

}
