package com.limachi.lim_lib.integration.Curios;

import com.limachi.lim_lib.ModBase;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.items.IItemHandlerModifiable;
//import top.theillusivec4.curios.api.SlotTypeMessage;

import java.util.*;
import java.util.function.Predicate;

@SuppressWarnings({"unused", "ConstantConditions"})
@Mod.EventBusSubscriber(modid = ModBase.COMMON_ID, value = Dist.CLIENT)
public class CuriosIntegration {
    public static final boolean isPresent = ModList.get().isLoaded("curios");
    protected static final HashSet<ResourceLocation> icons = new HashSet<>();
    protected static final HashMap<String, Pair<ResourceLocation, Integer>> slots = new HashMap<>();

    public static void registerIcon(ResourceLocation icon) { icons.add(icon); }
    public static void registerSlot(String slot, ResourceLocation icon, int size) { slots.put(slot, new Pair<>(icon, size)); }

    public static void enqueueIMC(final InterModEnqueueEvent event) {
//        if (isPresent)
//            for (Map.Entry<String, Pair<ResourceLocation, Integer>> s : slots.entrySet()) {
//                InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, ()->new SlotTypeMessage.Builder(s.getKey()).priority(-10).icon(s.getValue().getFirst()).size(s.getValue().getSecond()).build());
//            }
    }

//    @SubscribeEvent
//    public static void atlasEvent(TextureStitchEvent.Pre event) {
//        for (ResourceLocation icon : icons)
//            event.addSprite(icon);
//    }

    public static boolean equipOnFirstValidSlot(LivingEntity entity, String slot_category, ItemStack stack) {
        return false;
        /*
        if (!isPresent) return false;
        Optional<Boolean> ok = CuriosInterface.getCurioCategory(entity, slot_category).map(h->{
            for (int i = 0; i < h.getSlots(); ++i)
                if (h.getStackInSlot(i).isEmpty()) {
                    h.setStackInSlot(i, stack);
                    return h.getStackInSlot(i) == stack;
                }
            return false;
        });
        return ok.isPresent() && ok.get();
         */
    }

    public static Optional<IItemHandlerModifiable> getCurioCategory(LivingEntity entity, String category) {
//        if (!isPresent) return Optional.empty();
//        return CuriosInterface.getCurioCategory(entity, category);
        return Optional.empty();
    }

    public static SlotAccess searchItem(Entity entity, Class<? extends Item> clazz, Predicate<? super ItemStack> predicate) {
        List<SlotAccess> res = searchItem(entity, clazz, predicate, false);
        if (res.size() > 0)
            return res.get(0);
        return SlotAccess.NULL;
    }

    public static SlotAccess searchItemByExactStack(Entity entity, ItemStack stack) {
        List<SlotAccess> search = searchItem(entity, stack.getItem().getClass(), s -> s == stack, false);
        if (!search.isEmpty())
            return search.get(0);
        return SlotAccess.NULL;
    }

    private static boolean addMatch(ArrayList<SlotAccess> out, int index, Container inv, Class<? extends Item> clazz, Predicate<? super ItemStack> predicate) {
        ItemStack test = inv.getItem(index);
        if (clazz.isAssignableFrom(test.getItem().getClass()) && predicate.test(test)) {
            out.add(SlotAccess.forContainer(inv, index));
            return true;
        }
        return false;
    }

    public static List<SlotAccess> searchItem(Entity entity, Class<? extends Item> clazz, Predicate<? super ItemStack> predicate, boolean continueAfterOne) {
        ArrayList<SlotAccess> out = new ArrayList<>();
        if (entity instanceof Player player) {
            Inventory inv = player.getInventory();
            addMatch(out, inv.selected, inv, clazz, predicate);
            if (continueAfterOne || out.isEmpty())
                for (int i = Inventory.SLOT_OFFHAND; i < inv.getContainerSize(); ++i)
                    if (addMatch(out, i, inv, clazz, predicate) && !continueAfterOne)
                        break;
//            if (isPresent && (continueAfterOne || out.isEmpty()))
//                out.addAll(CuriosInterface.searchItemInCurio(player, clazz, predicate, continueAfterOne));
            if (continueAfterOne || out.isEmpty())
                for (int i = Inventory.SLOT_OFFHAND - inv.armor.size(); i < Inventory.SLOT_OFFHAND; ++i)
                    if (addMatch(out, i, inv, clazz, predicate) && !continueAfterOne)
                        break;
            if (continueAfterOne || out.isEmpty())
                for (int i = 0; i < Inventory.INVENTORY_SIZE; ++i) {
                    if (i == inv.selected)
                        continue;
                    if (addMatch(out, i, inv, clazz, predicate) && !continueAfterOne)
                        break;
                }
        } else {
//            if (isPresent && entity instanceof LivingEntity living)
//                out.addAll(CuriosInterface.searchItemInCurio(living, clazz, predicate, continueAfterOne));
            if (continueAfterOne || out.isEmpty())
                if (continueAfterOne || out.isEmpty()) {
                    for (int i = 0; i < 500; ++i) {
                        SlotAccess sa = entity.getSlot(i);
                        ItemStack test = sa.get();
                        if (clazz.isAssignableFrom(test.getItem().getClass()) && predicate.test(test)) {
                            out.add(sa);
                            if (!continueAfterOne)
                                break;
                        }
                    }
                }
        }
        return out;
    }
}