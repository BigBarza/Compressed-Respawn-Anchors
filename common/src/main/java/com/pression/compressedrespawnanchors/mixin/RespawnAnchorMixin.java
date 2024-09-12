package com.pression.compressedrespawnanchors.mixin;

import com.pression.compressedrespawnanchors.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
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

@Mixin(RespawnAnchorBlock.class)
public abstract class RespawnAnchorMixin {

    //These are just placeholders to make it compile. They should use the actual methods on runtime.
    @Shadow private static boolean canBeCharged(BlockState arg) { return false; }
    @Shadow protected abstract void explode(BlockState arg, Level arg2, BlockPos arg3);

    //This is a workaround to capture the item used to (try to) charge the anchor
    @Unique private ItemStack usedItem = ItemStack.EMPTY;

    //Replace the hardcoded check for glowstone dust as a respawn fuel with our own list.
    @Inject(method = "isRespawnFuel", at = @At("HEAD"), cancellable = true)
    private static void respawnFuelCheck(ItemStack item, CallbackInfoReturnable<Boolean> cir){
        cir.setReturnValue(Config.RESPAWN_FUELS.containsKey(item.getItem()));
    }
    //This is also part of the workaround, i can't capture locals with a redirect (God, that sounds wrong out of context)
    @Inject(method = "use", at = @At("HEAD"))
    private void captureItem(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult, CallbackInfoReturnable<InteractionResult> cir){
        usedItem = player.getItemInHand(interactionHand).copy();
    }

    //So this, instead of charging the anchor, should redirect here.
    @Redirect(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/RespawnAnchorBlock;charge(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)V"))
    private void onCharge(Level level, BlockPos pos, BlockState state){
        int charges = Config.RESPAWN_FUELS.get(usedItem.getItem()); //NOTE: We don't need to check if it's a valid fuel, since that check already passed earlier.
        while(canBeCharged(state) && charges > 0){ //It's a bit dirty, but lets us delegate the checking to vanilla code.
            charges--;
            RespawnAnchorBlock.charge(level, pos, state); //This WOULD play multiple sounds. I hope it sounds nice and that we get a free indicator of a beefier recharge.
        }
        if(charges > 0){ //Now, we need handle an overcharge.
            switch (Config.OVERCHARGE_EVENT){
                case "EXPLODE":
                    explode(state, level, pos); //haha, kablooey
                    break;
                case "DROP_EXCESS":
                    dropExcessItems(level, pos, charges);
                    break;
                default: //aka case NOTHING or a somehow messed up config
                    //Nothing happens. You just lose the excess charges.
                    break;
            }
        }
    }

    @Unique
    private static void dropExcessItems(Level level, BlockPos pos, int amount){
        ItemStack item = Config.OVERCHARGE_DROP.copy();
        if(item.isEmpty()) return; //TODO: Yell in the log about this.
        int stackSize = Math.min(amount, item.getMaxStackSize()); //It's a bad idea to spawn oversized stacks.
        item.setCount(stackSize);
        ItemEntity entity = new ItemEntity(level, pos.getX()+0.5, pos.getY()+1 ,pos.getZ()+0.5, item);
        entity.setDeltaMovement(0,0.5,0);
        level.addFreshEntity(entity);
        if(amount > item.getMaxStackSize()) dropExcessItems(level,pos,amount-item.getMaxStackSize()); //Yay, recursion!
    }

}
