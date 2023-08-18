package com.limachi.lim_lib.registries;

import com.google.common.collect.ArrayListMultimap;
import com.limachi.lim_lib.ModBase;
import com.limachi.lim_lib.capabilities.ICopyCapOnDeath;
import com.limachi.lim_lib.reflection.Classes;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = ModBase.COMMON_ID)
public class CapabilityRegister {
    public record CapRegister<Cap, Provider extends InternalProvider>(ResourceLocation key, Class<Cap> cap, Provider provider, boolean canKeepOnDeath) {}

    private static final HashMap<Capability<?>, CapRegister<?, ?>> CAPABILITIES = new HashMap<>();

    public static Map<Capability<?>, CapRegister<?, ?>> registeredCapabilities() {
        return Collections.unmodifiableMap(CAPABILITIES);
    }

    private static final ArrayListMultimap<Class<?>, Capability<?>> ENTITY_CAP = ArrayListMultimap.create();
    private static final ArrayListMultimap<Class<?>, Capability<?>> LEVEL_CAP = ArrayListMultimap.create();
    private static final ArrayListMultimap<Class<?>, Capability<?>> ITEM_STACK_CAP = ArrayListMultimap.create();
    private static final ArrayListMultimap<Class<?>, Capability<?>> BLOCK_ENTITY_CAP = ArrayListMultimap.create();
    private static final ArrayListMultimap<Class<?>, Capability<?>> LEVEL_CHUNK_CAP = ArrayListMultimap.create();

    private static final ArrayList<Capability<?>> KEEP_ON_DEATH = new ArrayList<>();

    protected interface InternalProvider extends ICapabilityProvider {
        void invalidate();
        InternalProvider create(Object target);
    }

    public static <Cap> Capability<Cap> create(String modId, String name, Class<Cap> clazz, CapabilityToken<Cap> token, Class<?> ... filters) {
        final Capability<Cap> out = CapabilityManager.get(token);
        final ResourceLocation key = new ResourceLocation(modId, name);
        boolean mayCopy = false;

        for (Class<?> filter : filters) {
            if (Entity.class.isAssignableFrom(filter)) {
                ENTITY_CAP.put(filter, out);
                mayCopy = true;
            }
            if (Level.class.isAssignableFrom(filter))
                LEVEL_CAP.put(filter, out);
            if (ItemStack.class.isAssignableFrom(filter))
                ITEM_STACK_CAP.put(filter, out);
            if (BlockEntity.class.isAssignableFrom(filter))
                BLOCK_ENTITY_CAP.put(filter, out);
            if (LevelChunk.class.isAssignableFrom(filter))
                LEVEL_CHUNK_CAP.put(filter, out);
        }

        boolean isCopy = ICopyCapOnDeath.class.isAssignableFrom(clazz);
        final boolean canKeepOnDeath = mayCopy && isCopy;

        if (isCopy)
            KEEP_ON_DEATH.add(out);

        class Provider1 implements InternalProvider {
            private final Cap instance;

            public Provider1(Object target) {
                if (target != null) {
                    if (Classes.getAssignableConstructor(clazz, target.getClass()) != null)
                        instance = Classes.newClass(clazz, target);
                    else
                        instance = Classes.newClass(clazz);
                }
                else
                    instance = null;
            }

            @Override
            public Provider1 create(Object target) {
                return new Provider1(target);
            }

            protected Cap getInstance() {
                return instance;
            }

            private final LazyOptional<Cap> lCap = LazyOptional.of(this::getInstance);

            @Override
            public void invalidate() {
                if (!canKeepOnDeath)
                    lCap.invalidate();
            }

            @Override
            public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                return out.orEmpty(cap, lCap);
            }
        }

        if (INBTSerializable.class.isAssignableFrom(clazz)) {

            class Provider2 extends Provider1 implements INBTSerializable<CompoundTag> {

                public Provider2(Object target) {
                    super(target);
                }

                @Override
                public Provider2 create(Object target) {
                    return new Provider2(target);
                }

                @SuppressWarnings("unchecked")
                @Override
                public CompoundTag serializeNBT() {
                    return ((INBTSerializable<CompoundTag>)getInstance()).serializeNBT();
                }

                @SuppressWarnings("unchecked")
                @Override
                public void deserializeNBT(CompoundTag nbt) {
                    ((INBTSerializable<CompoundTag>)getInstance()).deserializeNBT(nbt);
                }
            }

            CAPABILITIES.put(out, new CapRegister<>(key, clazz, new Provider2(null), canKeepOnDeath));
        } else
            CAPABILITIES.put(out, new CapRegister<>(key, clazz, new Provider1(null), canKeepOnDeath));
        return out;
    }

    @SuppressWarnings({"UnstableApiUsage", "rawtypes"})
    private static void filteredCapabilityAttacher(Capability<?> cap, AttachCapabilitiesEvent<? extends ICapabilityProviderImpl> event) {
        final CapRegister<?, ?> reg = CAPABILITIES.get(cap);
        ICapabilityProviderImpl target = event.getObject();
        if (!target.getCapability(cap).isPresent()) {
            InternalProvider provider = reg.provider.create(target);
            event.addCapability(reg.key, provider);
            event.addListener(provider::invalidate);
        }
    }

    @SuppressWarnings({"UnstableApiUsage", "rawtypes"})
    public static void manuallyAttachCapability(CapabilityToken<?> cap, AttachCapabilitiesEvent<? extends ICapabilityProviderImpl> event) {
        filteredCapabilityAttacher(CapabilityManager.get(cap), event);
    }

    @SubscribeEvent
    public static void attachEntityCapability(AttachCapabilitiesEvent<Entity> event) {
        final Class<?> c = event.getObject().getClass();
        ENTITY_CAP.keySet().stream().filter(t->t.isAssignableFrom(c)).forEach(t->{
            ENTITY_CAP.get(t).forEach(cap->filteredCapabilityAttacher(cap, event));
        });
    }

    @SubscribeEvent
    public static void attachLevelCapability(AttachCapabilitiesEvent<Level> event) {
        Class<?> c = event.getObject().getClass();
        LEVEL_CAP.keySet().stream().filter(t->t.isAssignableFrom(c)).forEach(t->{
            LEVEL_CAP.get(t).forEach(cap->filteredCapabilityAttacher(cap, event));
        });
    }

    @SubscribeEvent
    public static void attachItemStackCapability(AttachCapabilitiesEvent<ItemStack> event) {
        Class<?> c = event.getObject().getClass();
        ITEM_STACK_CAP.keySet().stream().filter(t->t.isAssignableFrom(c)).forEach(t->{
            ITEM_STACK_CAP.get(t).forEach(cap->filteredCapabilityAttacher(cap, event));
        });
    }

    @SubscribeEvent
    public static void attachBlockEntityCapability(AttachCapabilitiesEvent<BlockEntity> event) {
        Class<?> c = event.getObject().getClass();
        BLOCK_ENTITY_CAP.keySet().stream().filter(t->t.isAssignableFrom(c)).forEach(t->{
            BLOCK_ENTITY_CAP.get(t).forEach(cap->filteredCapabilityAttacher(cap, event));
        });
    }

    @SubscribeEvent
    public static void attachLevelChunkCapability(AttachCapabilitiesEvent<LevelChunk> event) {
        Class<?> c = event.getObject().getClass();
        LEVEL_CHUNK_CAP.keySet().stream().filter(t->t.isAssignableFrom(c)).forEach(t->{
            LEVEL_CHUNK_CAP.get(t).forEach(cap->filteredCapabilityAttacher(cap, event));
        });
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @SubscribeEvent
    public static void keepOnDeath(PlayerEvent.Clone event) {
        if (event.isWasDeath() && KEEP_ON_DEATH.size() > 0) {
            Player original = event.getOriginal();
            original.reviveCaps();
            Player player = event.getEntity();
            for (Capability<?> cap : KEEP_ON_DEATH)
                if (CAPABILITIES.get(cap).canKeepOnDeath)
                    original.getCapability(cap).ifPresent(pc->{
                        player.getCapability(cap).ifPresent(nc->((ICopyCapOnDeath)nc).copy(((ICopyCapOnDeath)pc)));
                    });
            original.invalidateCaps();
        }
    }

    @Mod.EventBusSubscriber(modid = ModBase.COMMON_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegCap {

        private static boolean once = true;

        @SubscribeEvent
        public static void registerCapability(RegisterCapabilitiesEvent event) {
            if (once) {
                for (CapRegister<?, ?> c : CAPABILITIES.values())
                    event.register(c.cap());
                once = false;
            }
        }
    }
}
