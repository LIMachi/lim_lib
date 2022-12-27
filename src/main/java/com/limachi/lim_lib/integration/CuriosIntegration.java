package com.limachi.lim_lib.integration;

import com.limachi.lim_lib.containers.ProxySlotModifier;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

import java.util.*;
import java.util.function.Predicate;

@SuppressWarnings({"unused", "ConstantConditions"})
@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class CuriosIntegration {
    private InterModEnqueueEvent event;
    protected static final HashSet<ResourceLocation> icons = new HashSet<>();
    protected static final HashMap<String, Pair<ResourceLocation, Integer>> slots = new HashMap<>();

    public static void registerIcon(ResourceLocation icon) { icons.add(icon); }
    public static void registerSlot(String slot, ResourceLocation icon, int size) { slots.put(slot, new Pair<>(icon, size)); }

    @SubscribeEvent
    public static void atlasEvent(TextureStitchEvent.Pre event) {
        for (ResourceLocation icon : icons)
            event.addSprite(icon);
    }

    public static void enqueueIMC(final InterModEnqueueEvent event) {
        for (Map.Entry<String, Pair<ResourceLocation, Integer>> s : slots.entrySet()) {
            InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, ()->new SlotTypeMessage.Builder(s.getKey()).priority(-10).icon(s.getValue().getFirst()).size(s.getValue().getSecond()).build());
        }
    }

    public static boolean equipOnFirstValidSlot(LivingEntity entity, String slot_category, ItemStack stack) {
        Optional<ICuriosItemHandler> oih = CuriosApi.getCuriosHelper().getCuriosHandler(entity).resolve();
        if (oih.isEmpty()) return false;
        Optional<ICurioStacksHandler> osh = oih.get().getStacksHandler(slot_category);
        if (osh.isEmpty()) return false;
        IDynamicStackHandler sh = osh.get().getStacks();
        for (int i = 0; i < sh.getSlots(); ++i)
            if (sh.getStackInSlot(i).isEmpty()) {
                sh.setStackInSlot(i, stack);
                return true;
            }
        return false;
    }

    public static ProxySlotModifier searchItem(Entity entity, Class<? extends Item> clazz, Predicate<? super ItemStack> predicate) {
        List<ProxySlotModifier> res = searchItem(entity, clazz, predicate, false);
        if (res.size() > 0)
            return res.get(0);
        return ProxySlotModifier.NULL_SLOT;
    }

    /** FIXME: should add search through cosmetic slots and compatibility with armor and extra slots of living entity
     * search item in following order (for a player): main hand, off hand, curios (all), armor (all), belt (all), inventory (all)
     * search item in following order (non player entity): main hand, off hand, curios (all, if living entity), armor (all)
     * @param entity the entity to search
     * @param clazz the type of itemclass to match (put 'Item.class' to test all item types)
     * @param predicate a predicate to do finer testing (put '()->true' to accept any item matching the class)
     * @param continueAfterOne if true, continues until all the recursions finishes and return a list of matching items
     * @return a list of setter and getters for the found stacks
     */
    public static List<ProxySlotModifier> searchItem(Entity entity, Class<? extends Item> clazz, Predicate<? super ItemStack> predicate, boolean continueAfterOne) {
        ArrayList<ProxySlotModifier> out = new ArrayList<>();
        boolean isPlayer = entity instanceof Player;
        Iterator<ItemStack> it = Collections.emptyIterator();
        Inventory inv = entity instanceof Player ? ((Player)entity).getInventory() : null;

        if (isPlayer) {
            if (inv == null)
                return out;
            if (clazz.isInstance(inv.getSelected().getItem()) && predicate.test(inv.getSelected())) {
                int current = inv.selected;
                out.add(new ProxySlotModifier(() -> inv.getItem(current), stack -> inv.setItem(current, stack)));
            }
        } else {
            if (entity.getHandSlots() != null) { //Yes, somehow I managed to get an entity with null hand slots...
                Iterator<ItemStack> tit = entity.getHandSlots().iterator();
                ItemStack test = tit.hasNext() ? tit.next() : ItemStack.EMPTY;
                if (clazz.isInstance(test.getItem()) && predicate.test(test))
                    out.add(new ProxySlotModifier(() -> entity.getHandSlots().iterator().next(), stack -> entity.setItemSlot(EquipmentSlot.MAINHAND, stack)));
            }
        }
        if (!continueAfterOne && out.size() > 0)
            return out;

        int s;
        int d;
        if (isPlayer) {
            s = inv.offhand.size();
            d = inv.getContainerSize() - s;
            if (clazz.isInstance(inv.offhand.get(0).getItem()) && predicate.test(inv.offhand.get(0))) {
                int slot = d;
                out.add(new ProxySlotModifier(() -> inv.offhand.get(0), stack -> inv.setItem(slot, stack)));
            }
        } else {
            if (entity.getHandSlots() != null) { //Yes, somehow I managed to get an entity with null hand slots...
                it = entity.getHandSlots().iterator();
                if (it.hasNext()) {
                    it.next();
                    if (it.hasNext()) {
                        ItemStack test = it.next();
                        if (clazz.isInstance(test.getItem()) && predicate.test(test))
                            out.add(new ProxySlotModifier(() -> {
                                Iterator<ItemStack> i = entity.getHandSlots().iterator();
                                i.next();
                                return i.next();
                            }, stack -> entity.setItemSlot(EquipmentSlot.OFFHAND, stack)));
                    }
                }
            }
        }
        if (!continueAfterOne && out.size() > 0)
            return out;

        if (entity instanceof LivingEntity) {
            Optional<ImmutableTriple<String, Integer, ItemStack>> cs;
            ArrayList<ImmutableTriple<String, Integer, ItemStack>> blackList = new ArrayList<>();
            while ((cs = CuriosApi.getCuriosHelper().findEquippedCurio(stack->{
                for (ImmutableTriple<String, Integer, ItemStack> ignore: blackList)
                    if (stack == ignore.getRight())
                        return false;
                return clazz.isInstance(stack.getItem()) && predicate.test(stack);
            }, (LivingEntity) entity)).isPresent() && continueAfterOne)
                blackList.add(cs.get());
            if (continueAfterOne)
                for (ImmutableTriple<String, Integer, ItemStack> found : blackList)
                    out.add(new ProxySlotModifier(()->CuriosApi.getCuriosHelper().getEquippedCurios((LivingEntity) entity).resolve().get().getStackInSlot(found.getMiddle()), stack->CuriosApi.getCuriosHelper().getEquippedCurios((LivingEntity) entity).resolve().get().setStackInSlot(found.getMiddle(), stack)));
            if (cs.isPresent()) {
                ImmutableTriple<String, Integer, ItemStack> found = cs.get();
                out.add(new ProxySlotModifier(()->CuriosApi.getCuriosHelper().getEquippedCurios((LivingEntity) entity).resolve().get().getStackInSlot(found.getMiddle()), stack->CuriosApi.getCuriosHelper().getEquippedCurios((LivingEntity) entity).resolve().get().setStackInSlot(found.getMiddle(), stack)));
            }
            if (!continueAfterOne && out.size() > 0)
                return out;
        }

        if (isPlayer) {
            s = inv.armor.size();
            d = inv.getContainerSize() - inv.armor.size() - s;
        } else  {
            s = 4;
            d = 0;
            if (entity.getArmorSlots() == null) //Yes, somehow I managed to get an entity with null armor slots...
                return out;
            it = entity.getArmorSlots().iterator();
        }
        for (int i = 0; i < s; ++i) {
            int ind = d + i;
            ItemStack test = isPlayer ? inv.getItem(ind) : it.hasNext() ? it.next() : null;
            if (test != null && clazz.isInstance(test.getItem()) && predicate.test(test)) {
                if (isPlayer)
                    out.add(new ProxySlotModifier(()->inv.getItem(ind), stack->inv.setItem(ind, stack)));
                else
                    out.add(new ProxySlotModifier(()->{
                        Iterator<ItemStack> ar = entity.getArmorSlots().iterator();
                        for (int j = 0; j < ind; ++j)
                            ar.next();
                        return ar.next();
                    }, stack-> entity.setItemSlot(EquipmentSlot.byTypeAndIndex(EquipmentSlot.Type.ARMOR, ind), stack)));
                if (!continueAfterOne)
                    return out;
            }
        }

        if (isPlayer) {
            s = inv.items.size();
            for (int i = 0; i < s; ++i) {
                if (i == inv.selected) continue;
                int ind = i;
                ItemStack test = inv.getItem(ind);
                if (clazz.isInstance(test.getItem()) && predicate.test(test)) {
                    out.add(new ProxySlotModifier(()->inv.getItem(ind), stack->inv.setItem(ind, stack)));
                    if (!continueAfterOne)
                        return out;
                }
            }
        }

        return out;
    }
}