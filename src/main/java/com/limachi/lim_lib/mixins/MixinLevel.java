package com.limachi.lim_lib.mixins;

//import com.limachi.lim_lib.redstone.IRedstoneEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

@Mixin(Level.class)
public abstract class MixinLevel {
//    @Shadow public abstract List<Entity> getEntities(@Nullable Entity p_46536_, AABB p_46537_, Predicate<? super Entity> p_46538_);
//
//    @Inject(at = @At("RETURN"), method = "Lnet/minecraft/world/level/Level;getSignal(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)I", cancellable = true)
//    protected void  onGetSignal(BlockPos pos, Direction dir, CallbackInfoReturnable<Integer> out) {
//        int power = out.getReturnValue();
//        if (power < 15) {
//            AtomicInteger i = new AtomicInteger(power);
//            this.getEntities(null, new AABB(pos).inflate(0.01), e -> e instanceof IRedstoneEntity).forEach(e -> i.set(Integer.max(((IRedstoneEntity) e).getSignal(pos, dir), i.get())));
//            out.setReturnValue(i.get());
//        }
//    }
}
