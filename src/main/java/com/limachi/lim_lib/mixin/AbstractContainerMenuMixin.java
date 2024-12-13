package com.limachi.lim_lib.mixin;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractContainerMenu.class)
public class AbstractContainerMenuMixin {

    //remap the getcount of an item stack being split so that it does not produce full stacks if the count is above the max size of the stack
    @Redirect(method = "doClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getCount()I", ordinal = 6))
    public int getCount(ItemStack instance) {
        return Math.min(instance.getCount(), instance.getMaxStackSize());
    }
}
