package com.limachi.lim_lib.containers;

import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * helper class to standardise access to slots (in IItemHandlerModifiable, Entity, IInventory and others)
 */
@SuppressWarnings("unused")
public class ProxySlotModifier {
    public static final ProxySlotModifier NULL_SLOT = new ProxySlotModifier(null, null, null, null);

    private final Supplier<ItemStack> getter;
    private final Consumer<ItemStack> setter;
    private final Predicate<ItemStack> itemstackValidator;
    private final Supplier<Integer> maxSize;

    public static <T, R> R onNthEllem(Iterable<T> it, int n, Function<T, R> run, R def) {
        for (T t: it) {
            if (n == 0) return run.apply(t);
            --n;
        }
        return def;
    }

    public ProxySlotModifier(Entity entity, EquipmentSlot slot) {
        this(slot.getType() == EquipmentSlot.Type.HAND ?
                        ()->onNthEllem(entity.getHandSlots(), slot.getIndex(), item->item, ItemStack.EMPTY) :
                        ()->onNthEllem(entity.getArmorSlots(), slot.getIndex(), item->item, ItemStack.EMPTY),
                itemStack -> entity.setItemSlot(slot, itemStack),
                itemStack -> slot == EquipmentSlot.MAINHAND || slot == EquipmentSlot.OFFHAND || Mob.getEquipmentSlotForItem(itemStack) == slot,
                () -> slot.getType() == EquipmentSlot.Type.ARMOR ? 1 : 64);
    }
    public ProxySlotModifier(IItemHandlerModifiable handler, int slot, @Nullable Consumer<IItemHandlerModifiable> setChanged) { this(()->handler.getStackInSlot(slot), itemStack -> { handler.setStackInSlot(slot, itemStack); if (setChanged != null) setChanged.accept(handler); }, itemStack -> handler.isItemValid(slot, itemStack), () -> handler.getSlotLimit(slot)); }
    public ProxySlotModifier(Container handler, int slot, @Nullable Consumer<Container> setChanged) { this(()->handler.getItem(slot), itemStack -> { handler.setItem(slot, itemStack); if (setChanged != null) setChanged.accept(handler); }, itemStack -> handler.canPlaceItem(slot, itemStack), handler::getMaxStackSize); }
    public ProxySlotModifier(Supplier<ItemStack> getter, Consumer<ItemStack> setter) { this(getter, setter, i->!i.isEmpty(), ()->64); }
    public ProxySlotModifier(Consumer<ItemStack> setter) { this(null, setter, i->!i.isEmpty(), ()->64); }
    public ProxySlotModifier(Supplier<ItemStack> getter) { this(getter, null, null, ()->64); }
    public ProxySlotModifier(Supplier<ItemStack> getter, Consumer<ItemStack> setter, Predicate<ItemStack> itemstackValidator, Supplier<Integer> maxSize) {
        this.getter = getter;
        this.setter = setter;
        this.itemstackValidator = itemstackValidator;
        this.maxSize = maxSize;
    }

    public boolean isValid() { return getter != null || setter != null; }

    public boolean isItemStackValid(ItemStack stack) { return itemstackValidator != null && itemstackValidator.test(stack); }

    public int getMaxSize() { return maxSize == null ? 0 : maxSize.get(); }

    public ItemStack get() {
        if (getter == null) return ItemStack.EMPTY; //TODO: should warn there
        return getter.get();
    }

    public void set(ItemStack stack) {
        if (setter == null) return; //TODO: should warn there
        if (stack == null) stack = ItemStack.EMPTY;
        setter.accept(stack);
    }
}
