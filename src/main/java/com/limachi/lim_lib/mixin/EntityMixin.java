package com.limachi.lim_lib.mixin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.limachi.lim_lib.ISpecialEntityRider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow private ImmutableList<Entity> passengers;

    @Shadow private Level level;

    @Unique
    private final Entity this$mixin = (Entity)(Object)this;

    /**
     * When a player is added to a list of passengers, they will be put in front of any other entities (except if a player is already the first passenger).
     * With this (hopefully not too) intrusive mixin, I allow a player to stay behind a bag by swapping back the seats if the bag is in second position and the player in first
     */
    @Inject(method = "addPassenger(Lnet/minecraft/world/entity/Entity;)V", at = @At("RETURN"))
    protected void allowSpecialRiderToBeControllingPassenger(Entity rider, CallbackInfo ci) {
        if (!level.isClientSide && rider instanceof Player && passengers.size() > 1 && passengers.get(0) == rider && passengers.get(1) instanceof ISpecialEntityRider specialRider && specialRider.canControl(this$mixin)) {
            List<net.minecraft.world.entity.Entity> list = Lists.newArrayList(this.passengers);
            list.set(0, passengers.get(1));
            list.set(1, rider);
            passengers = ImmutableList.copyOf(list);
        }
    }
}
