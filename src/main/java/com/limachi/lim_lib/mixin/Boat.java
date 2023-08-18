package com.limachi.lim_lib.mixin;

import com.limachi.lim_lib.ISpecialEntityRider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(net.minecraft.world.entity.vehicle.Boat.class)
public abstract class Boat {

    @Shadow @Nullable public abstract LivingEntity getControllingPassenger();

    @Inject(method = "clampRotation(Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
    private void unClampedRotationWhenBoatIsRiddenByBag(net.minecraft.world.entity.Entity passenger, CallbackInfo ci) {
        if (getControllingPassenger() instanceof ISpecialEntityRider && passenger instanceof Player)
            ci.cancel();
    }
}
