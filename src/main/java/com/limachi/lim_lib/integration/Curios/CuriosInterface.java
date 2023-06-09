package com.limachi.lim_lib.integration.Curios;

import com.limachi.lim_lib.StackUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import top.theillusivec4.curios.api.CuriosCapability;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public abstract class CuriosInterface {
    public static Optional<IItemHandlerModifiable> getCurioCategory(LivingEntity entity, String category) {
        LazyOptional<ICuriosItemHandler> laz = entity.getCapability(CuriosCapability.INVENTORY);
        if (laz.isPresent())
            return laz.resolve().flatMap(h->h.getStacksHandler(category).map(csh->(IItemHandlerModifiable)csh.getStacks()));
        return Optional.empty();
    }

    public static List<SlotAccess> searchItemInCurio(LivingEntity entity, Class<? extends Item> clazz, Predicate<? super ItemStack> predicate, boolean continueAfterOne) {
        ArrayList<SlotAccess> out = new ArrayList<>();
        LazyOptional<ICuriosItemHandler> laz = entity.getCapability(CuriosCapability.INVENTORY);
        if (laz.isPresent()) {
            laz.resolve().ifPresent(h->{
                for (ICurioStacksHandler sh : h.getCurios().values()) {
                    IDynamicStackHandler d = sh.getStacks();
                    for (int i = 0; i < d.getSlots(); ++i) {
                        ItemStack test = d.getStackInSlot(i);
                        if (clazz.isAssignableFrom(test.getItem().getClass()) && predicate.test(test)) {
                            out.add(StackUtils.slotAccessForItemHandler(d, i));
                            if (!continueAfterOne)
                                break;
                        }
                    }
                    if (!continueAfterOne && !out.isEmpty())
                        break;
                }
            });
        }
        return out;
    }
}
