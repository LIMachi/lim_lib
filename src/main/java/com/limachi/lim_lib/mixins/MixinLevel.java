package com.limachi.lim_lib.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//@Mixin(Level.class)
//public abstract class MixinLevel {
//    @Shadow public abstract ResourceKey<Level> dimension();
//
//    @Inject(at = @At("RETURN"), method = "Lnet/minecraft/world/level/Level;getSignal(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)I")
//    protected void  onGetSignal(BlockPos pos, Direction dir, CallbackInfoReturnable<Integer> out) {
//
//    }
//}