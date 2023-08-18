package com.limachi.lim_lib.constructorEnforcer;

import com.google.common.collect.ArrayListMultimap;
import com.limachi.lim_lib.Log;
import com.limachi.lim_lib.Sides;
import com.limachi.lim_lib.Strings;
import com.limachi.lim_lib.constructorEnforcer.enforcers.Default;
import com.limachi.lim_lib.reflection.Classes;
import com.limachi.lim_lib.saveData.AbstractSyncSaveData;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Map;

import static com.limachi.lim_lib.Strings.getSimpleConstructor;

@SuppressWarnings("unused")
public class ConstructorEnforcer {
    private static final HashSet<String> IGNORED_CLASS_PATTERNS = new HashSet<>();
    private static final ArrayListMultimap<Class<?>, Pair<Class<?>[], HashSet<Class<?>>>> CONSTRUCTOR_PATTERNS = ArrayListMultimap.create();
    static {
        ignoreClassName(".*[Mm]ixin.*");
        ignoreClassName(".*[Jj][Ee][Ii].*");
        ignoreClassName(".*[Tt][Oo][Pp].*");
        registerConstructorPattern(Default.class);
        registerConstructorPattern(AbstractSyncSaveData.class, String.class);
        registerConstructorPattern(AbstractContainerMenu.class, int.class, Inventory.class, FriendlyByteBuf.class);
        registerConstructorPattern(BaseContainerBlockEntity.class, BlockPos.class, BlockState.class);
    }

    /**
     * MUST be called OUTSIDE classes that extend/implement the marker interface/abstract class,
     * the best behavior is to call it in a STATIC BLOCK BEFORE in your mod class before the constructor
     * (to make sure it's called before @StaticInit annotations)
     * @param interfaceOrAbstractClass the marker that will be implemented/extended by the tested classes
     * @param parameterTypes optional parameters types, varg of classes (Default interface is already declared with no parameters)
     */
    public static void registerConstructorPattern(Class<?> interfaceOrAbstractClass, Class<?> ... parameterTypes) {
        CONSTRUCTOR_PATTERNS.put(interfaceOrAbstractClass, new Pair<>(parameterTypes, new HashSet<>()));
    }

    /**
     * To make sure this work as intended, like registerConstructorPattern, call this in a static block inside your mod class before the constructor
     * @param regex the pattern (regex) that will be used to match the classes names ('.*[Mm]ixin.*' is already ignored by default)
     */
    public static void ignoreClassName(String regex) { IGNORED_CLASS_PATTERNS.add(regex); }

    /**
     * Test the given class, regardless of it's name. Will only test patterns once per class, even if called more than once.
     * Will exit and print the stack on invalid constructor.
     * @param clazz the class to test.
     */
    public static void testClass(Class<?> clazz) {
        if (Modifier.isAbstract(clazz.getModifiers())) return;
        for (Map.Entry<Class<?>, Pair<Class<?>[], HashSet<Class<?>>>> e : CONSTRUCTOR_PATTERNS.entries()) {
            if (e.getValue().getSecond().contains(clazz)) continue;
            if (e.getKey().isAssignableFrom(clazz))
                try {
                    clazz.getConstructor(e.getValue().getFirst());
                } catch (NoSuchMethodException exception) {
                    Log.error(clazz, "Class is declared as " + Strings.getSimplifiedClassName(e.getKey().getName()) + " but has no valid constructor! Expected at least one constructor of the form: public " + getSimpleConstructor(clazz.getCanonicalName(), e.getValue().getFirst()));
                    exception.printStackTrace();
                    System.exit(-1);
                }
            e.getValue().getSecond().add(clazz);
        }
    }

    /**
     * Will scan all the classes for the given mod
     * (ignoring interface/abstract and classes matching patterns declared with ignoreClassName)
     * and test the constructors (see testClass)
     * @param modId filter to only test classes declared by this mod
     */
    public static void testAllClass(String modId) {
        if (!Sides.isPhysicalClient()) return; //only run this while testing on the physical client side (still test server classes in single player)
        for (ModFileScanData scan : ModList.get().getAllScanData())
            if (scan.getTargets().containsKey(modId))
                for (ModFileScanData.ClassData cd : scan.getClasses()) {
                    String className = cd.clazz().getClassName();
                    boolean skip = false;
                    for (String regex : IGNORED_CLASS_PATTERNS)
                        if (className.matches(regex)) {
                            skip = true;
                            break;
                        }
                    if (skip) continue;
                    Class<?> clazz = Classes.classByName(className, null);
                    if (clazz == null) {
                        System.exit(-1);
                        return;
                    }
                    testClass(clazz);
                }
    }
}
