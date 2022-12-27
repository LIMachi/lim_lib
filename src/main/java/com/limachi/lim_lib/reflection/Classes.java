package com.limachi.lim_lib.reflection;

import com.limachi.lim_lib.Log;
import com.limachi.lim_lib.Strings;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.objectweb.asm.Type;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class Classes {
    /**
     * <pre>
     * Get the fully qualified name of the class that called this function. Since this does not directly use the calling
     * class itself, it is safe to be called in class constructor.
     * </pre>
     */
    public static String getClassName() { return Thread.currentThread().getStackTrace()[1].getClassName(); }
    public static String getClassName(int depth) { return Thread.currentThread().getStackTrace()[1 + depth].getClassName(); }

    /**
     * <pre>
     * Get the mod id of the mod that use this class, no guarantees, might return null or another unexpected mod id
     * @return
     * </pre>
     */
    public static String getModId() { return getModId(1); }
    public static String getModId(int depth) {
        String t = Thread.currentThread().getStackTrace()[1 + depth].getClassName();
        for (ModFileScanData fsd : ModList.get().getAllScanData())
            for (ModFileScanData.ClassData cd : fsd.getClasses())
                if (t.equals(cd.clazz().getClassName()))
                    return fsd.getTargets().keySet().stream().findAny().orElse(null);
        return null;
    }

    /**
     * <pre>
     * Get the simplified name of the class that called this function. Since this does not directly use the calling
     * class itself, it is safe to be called in class constructor.
     * </pre>
     */
    public static String getSimpleClassName() { return Strings.getSimplifiedClassName(Thread.currentThread().getStackTrace()[2].getClassName()); }
    public static String getSimpleClassName(int depth) { return Strings.getSimplifiedClassName(Thread.currentThread().getStackTrace()[2 + depth].getClassName()); }

    /**
     * <pre>
     * Try to get a class by its name or by its obfuscated name, auto cast it.
     * Class statics will not be initialized.
     * @param obName the obfuscated name of the class, should be used in final mod
     * @param name the un-obfuscated name of the class, should be used in dev
     * @return the class on success, or null and message log on error.
     * </pre>
     */
    @SuppressWarnings("unchecked")
    public static <T extends Class<O>, O> T classByName(String obName, String name) {
        try {
            return (T)Class.forName(obName);
        } catch (Exception ignored) {}
        try {
            return (T)Class.forName(name);
        } catch (Exception e) {
            Log.error(name != null ? name : obName, 1, "classByName -> Invalid class name");
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Class<O>, O> T classByType(Type type) {
        try {
            return (T) Class.forName(type.getClassName());
        } catch (ClassNotFoundException e) {
            Log.error(type, 1, "classByType -> Could not get class");
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    protected static <T> Constructor<T> getAssignableConstructor(Class<T> clazz, Class<?>[] parametersTypes) {
        try {
            return clazz.getConstructor(parametersTypes);
        } catch (Exception ignored) {}
        for (Constructor<?> tc : clazz.getConstructors()) {
            if (Types.paramTypesAreAssignable(tc.getParameterTypes(), parametersTypes)) {
                return (Constructor<T>)tc;
            }
        }
        return null;
    }

    /**
     * <pre>
     * try to get a constructor accessor for the given clazz.
     * @param clazz the class from which the constructor accessor will be extracted.
     * @param parametersTypes var arg of parameters types that the constructor was declared with.
     * @return the constructor (set to accessible) on success, or null and message log on error.
     * </pre>
     */
    @SuppressWarnings("ConstantConditions")
    public static <T> Constructor<T> getConstructor(Class<T> clazz, Class<?> ... parametersTypes) {
        try {
            Constructor<T> out = getAssignableConstructor(clazz, parametersTypes);
            out.setAccessible(true);
            return out;
        } catch (Exception e) {
            Log.error(clazz, 1, "getConstructor -> Could not get constructor with parameter types: " + Arrays.toString(parametersTypes));
            e.printStackTrace();
            return null;
        }
    }

    /**
     * <pre>
     * try to create a new instance of the given class.
     * @param clazz the class type of the returned instance.
     * @param parameters var arg of parameters that the class constructor will be invoked with.
     * @return a new instance of the class on success, or null and message log on error.
     * </pre>
     */
    @SuppressWarnings("ConstantConditions")
    public static <T> T newClass(Class<T> clazz, Object ... parameters) {
        Class<?>[] parametersTypes = new Class[parameters.length];
        for (int i = 0; i < parameters.length; ++i)
            parametersTypes[i] = parameters[i].getClass();
        try {
            Constructor<T> out = getAssignableConstructor(clazz, parametersTypes);
            out.setAccessible(true);
            return out.newInstance(parameters);
        } catch (Exception e) {
            Log.error(clazz, 1, "newClass -> Could not create a new instance with parameters: " + Arrays.toString(parameters));
            e.printStackTrace();
            return null;
        }
    }

    /**
     * <pre>
     * create a supplier that will, when called, try to create a new instance of the given class.
     * useful for registration.
     * @param clazz the class type of the returned instance.
     * @param parameters var arg of parameters that the class constructor will be invoked with.
     * @return a supplier that will itself return:
     * a new instance of the class on success, or null and message log on error.
     * </pre>
     */
    @SuppressWarnings("ConstantConditions")
    public static <T> Supplier<T> newClassSupplier(Class<T> clazz, Object ... parameters) {
        return () -> {
            Class<?>[] parametersTypes = new Class[parameters.length];
            for (int i = 0; i < parameters.length; ++i)
                parametersTypes[i] = parameters[i].getClass();
            try {
                Constructor<T> out = getAssignableConstructor(clazz, parametersTypes);
                out.setAccessible(true);
                return out.newInstance(parameters);
            } catch (Exception e) {
                Log.error(clazz, 1, "newClassSupplier -> Could not create a new instance with parameters: " + Arrays.toString(parameters));
                e.printStackTrace();
                return null;
            }
        };
    }
}
