package com.limachi.lim_lib;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Supplier;

/**
 * <pre>
 * Helper class to handle reflection.
 * Most methods have in mind that class/field/method names might be obfuscated,
 * and so require you to provide both un-obfuscated and obfuscated names.
 * Obfuscated name will always be tried first to improve the performances of finished mods.
 * If you use reflected names for class/field/method, just put those in the 'obName' parameters and leave 'name' empty/null.
 * Methods and constructor will try to find an exact match for the parameters type first, and if they don't find one, they
 * will test all constructors/methods for valid parameter casts.
 * </pre>
 */
@SuppressWarnings("unused")
public class Reflection {

    /**
     * <pre>
     * Allow construction of an enum from an object cast to an anonymous enum (we know that object is an enum, and we
     * know that we can store the instance, but we can't qualify the class entirely, used in method reflections)
     * </pre>
     */
    @SuppressWarnings("unchecked")
    public static <T extends Enum<?>> T anonymousEnumBuilder(Class<T> clazz, int ordinal) {
        if (clazz.isEnum()) {
            try {
                return ((T[])clazz.getMethod("values").invoke(null))[ordinal];
            } catch (Exception ignored) {}
        }
        Log.error(clazz, 1, "anonymousEnumBuilder -> Could not create instance of Enum class with ordinal: " + ordinal);
        return null;
    }

    /**
     * <pre>
     * Get the fully qualified name of the class that called this function. Since this does not directly use the calling
     * class itself, it is safe to be called in class constructor.
     * </pre>
     */
    public static String getClassName() { return Thread.currentThread().getStackTrace()[1].getClassName(); }

    /**
     * <pre>
     * Get the simplified name of the class that called this function. Since this does not directly use the calling
     * class itself, it is safe to be called in class constructor.
     * </pre>
     */
    public static String getSimpleClassName() { return Strings.getSimplifiedClassName(Thread.currentThread().getStackTrace()[2].getClassName()); }

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

    /**
     * <pre>
     * try to get a field accessor by name or by its obfuscated name in the given class.
     * @param clazz the class from which we'll retrieve the static field accessor
     * @param obName the obfuscated name of the field, should be used in final mod
     * @param name the un-obfuscated name of the field, should be used in dev
     * @return the field (set to accessible) on success, or null and message log on error.
     * </pre>
     */
    public static Field getField(Class<?> clazz, String obName, String name) {
        try {
            Field out = clazz.getField(obName);
            out.setAccessible(true);
            return out;
        } catch (Exception ignored) {}
        try {
            Field out = clazz.getField(name);
            out.setAccessible(true);
            return out;
        } catch (Exception e) {
            Log.error(name != null ? name : obName, 1, "getField -> Invalid field for class: " + clazz);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * <pre>
     * try to get a field accessor by name or by its obfuscated name in the given object.
     * @param object the object from which we'll retrieve the field accessor
     * @param obName the obfuscated name of the field, should be used in final mod
     * @param name the un-obfuscated name of the field, should be used in dev
     * @return the field (set to accessible) on success, or null and message log on error.
     * </pre>
     */
    public static Field getField(Object object, String obName, String name) {
        try {
            Field out = object.getClass().getField(obName);
            out.setAccessible(true);
            return out;
        } catch (Exception ignored) {}
        try {
            Field out = object.getClass().getField(name);
            out.setAccessible(true);
            return out;
        } catch (Exception e) {
            Log.error(name != null ? name : obName, 1, "getField -> Invalid field for object: " + object);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * <pre>
     * try to get the value of a static field by name or by its obfuscated name in the given class.
     * @param clazz the class from which we'll retrieve the static field value
     * @param obName the obfuscated name of the static field, should be used in final mod
     * @param name the un-obfuscated name of the static field, should be used in dev
     * @return the value (casted) on success, or null and message log on error.
     * </pre>
     */
    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(Class<?> clazz, String obName, String name) {
        try {
            Field out = clazz.getField(obName);
            out.setAccessible(true);
            return (T)out.get(null);
        } catch (Exception ignored) {}
        try {
            Field out = clazz.getField(name);
            out.setAccessible(true);
            return (T)out.get(null);
        } catch (Exception e) {
            Log.error(name != null ? name : obName, 1, "getFieldValue -> Invalid or locked field for class: " + clazz);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * <pre>
     * try to get the value of a field by name or by its obfuscated name in the given object.
     * @param object the object from which we'll retrieve the field value
     * @param obName the obfuscated name of the field, should be used in final mod
     * @param name the un-obfuscated name of the field, should be used in dev
     * @return the value (casted) on success, or null and message log on error.
     * </pre>
     */
    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(Object object, String obName, String name) {
        try {
            Field out = object.getClass().getField(obName);
            out.setAccessible(true);
            return (T)out.get(null);
        } catch (Exception ignored) {}
        try {
            Field out = object.getClass().getField(name);
            out.setAccessible(true);
            return (T)out.get(null);
        } catch (Exception e) {
            Log.error(name != null ? name : obName, 1, "getFieldValue -> Invalid or locked field for object: " + object);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * <pre>
     * try to set the value of a static field by name or by its obfuscated name in the given class.
     * @param clazz the class that will have its static field set to value
     * @param value the value to store inside the given static field
     * @param obName the obfuscated name of the static field, should be used in final mod
     * @param name the un-obfuscated name of the static field, should be used in dev
     * @return true on success, or false and message log on error.
     * </pre>
     */
    public static boolean setFieldValue(Class<?> clazz, Object value, String obName, String name) {
        try {
            Field out = clazz.getField(obName);
            out.setAccessible(true);
            out.set(null, value);
            return true;
        } catch (Exception ignored) {}
        try {
            Field out = clazz.getField(name);
            out.setAccessible(true);
            out.set(null,value);
            return true;
        } catch (Exception e) {
            Log.error(name != null ? name : obName, 1, "setFieldValue -> Invalid or locked field for class: " + clazz);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * <pre>
     * try to get the value of a field by name or by its obfuscated name in the given object.
     * @param object the object that will have its field set to value
     * @param value the value to store inside the given field
     * @param obName the obfuscated name of the field, should be used in final mod
     * @param name the un-obfuscated name of the field, should be used in dev
     * @return true on success, or null and message log on error.
     * </pre>
     */
    public static boolean setFieldValue(Object object, Object value, String obName, String name) {
        try {
            Field out = object.getClass().getField(obName);
            out.setAccessible(true);
            out.set(object, value);
            return true;
        } catch (Exception ignored) {}
        try {
            Field out = object.getClass().getField(name);
            out.setAccessible(true);
            out.set(object,value);
            return true;
        } catch (Exception e) {
            Log.error(name != null ? name : obName, 1, "getFieldValue -> Invalid or locked field for object: " + object);
            e.printStackTrace();
            return false;
        }
    }

    public static class VargMethod {
        Class<?> clazz;
        Object object;
        String m1;
        String m2;
        public VargMethod(Class<?> clazz, String methodName, String obfuscatedMethodName) {
            this.clazz = clazz;
            this.object = null;
            this.m1 = obfuscatedMethodName;
            this.m2 = methodName;
        }
        public VargMethod(Object object, String methodName, String obfuscatedMethodName) {
            this.clazz = object.getClass();
            this.object = object;
            this.m1 = obfuscatedMethodName;
            this.m2 = methodName;
        }
        public VargMethod(Class<?> clazz, String methodName) {
            this.clazz = clazz;
            this.object = null;
            this.m1 = methodName;
            this.m2 = null;
        }
        public VargMethod(Object object, String methodName) {
            this.clazz = object.getClass();
            this.object = object;
            this.m1 = methodName;
            this.m2 = null;
        }
        public <T> T invoke(Object ... parameters) {
            return object == null ? invokeMethod(clazz, m1, m2, parameters) : invokeMethod(object, m1, m2, parameters);
        }
    }

    public static HashMap<Class<?>, Class<?>> CASTABLE_PRIMITIVES = new HashMap<>();
    protected static void castablePrimitive(Class<?> c1, Class<?> c2) {
        CASTABLE_PRIMITIVES.put(c1, c2);
        CASTABLE_PRIMITIVES.put(c2, c1);
    }
    static {
        castablePrimitive(Boolean.class, boolean.class);
        castablePrimitive(Byte.class, byte.class);
        castablePrimitive(Character.class, char.class);
        castablePrimitive(Short.class, short.class);
        castablePrimitive(Integer.class, int.class);
        castablePrimitive(Long.class, long.class);
        castablePrimitive(Float.class, float.class);
        castablePrimitive(Double.class, double.class);
    }

    protected static boolean canCast(Class<?> to, Class<?> from) {
        return to.isAssignableFrom(from) || CASTABLE_PRIMITIVES.get(to).isAssignableFrom(from) || to.isAssignableFrom(CASTABLE_PRIMITIVES.get(from));
    }

    protected static boolean paramTypesAreAssignable(Class<?>[] to, Class<?>[] from) {
        if (to.length != from.length) return false;
        for (int i = 0; i < to.length; ++i)
            if (!canCast(to[i], from[i])) //FIXME: does not work with primitives vs object of castable type (ex int/Integer)
                return false;
        return true;
    }

    protected static Method getAssignableMethod(Class<?> clazz, String obName, String name, Class<?>[] parametersTypes) {
        try {
            return clazz.getMethod(obName, parametersTypes);
        } catch (Exception ignored) {}
        try {
            return clazz.getMethod(name, parametersTypes);
        } catch (Exception ignored) {}
        for (Method tm : clazz.getMethods()) {
            if ((tm.getName().equals(obName) || tm.getName().equals(name)) && paramTypesAreAssignable(tm.getParameterTypes(), parametersTypes)) {
                return tm;
            }
        }
        return null;
    }

    /**
     * <pre>
     * try to get a static method accessor by name or by its obfuscated name in the given clazz.
     * @param clazz the class from which we'll retrieve the static method accessor
     * @param obName the obfuscated name of the static method, should be used in final mod
     * @param name the un-obfuscated name of the static method, should be used in dev
     * @param parametersTypes var arg of parameters types that the method was declared with
     * @return the method (set to accessible) on success, or null and message log on error.
     * </pre>
     */
    @SuppressWarnings("ConstantConditions")
    public static Method getMethod(Class<?> clazz, String obName, String name, Class<?> ... parametersTypes) {
        try {
            Method out = getAssignableMethod(clazz, obName, name, parametersTypes);
            out.setAccessible(true);
            return out;
        } catch (Exception e) {
            Log.error(name != null ? name : obName, 1, "getMethod -> Invalid method for class: " + clazz);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * <pre>
     * try to get a method accessor by name or by its obfuscated name in the given object.
     * @param object the object from which we'll retrieve the method accessor
     * @param obName the obfuscated name of the method, should be used in final mod
     * @param name the un-obfuscated name of the method, should be used in dev
     * @param parametersTypes var arg of parameters types that the method was declared with
     * @return the method (set to accessible) on success, or null and message log on error.
     * </pre>
     */
    @SuppressWarnings("ConstantConditions")
    public static Method getMethod(Object object, String obName, String name, Class<?> ... parametersTypes) {
        try {
            Method out = getAssignableMethod(object.getClass(), obName, name, parametersTypes);
            out.setAccessible(true);
            return out;
        } catch (Exception e) {
            Log.error(name != null ? name : obName, 1, "getMethod -> Invalid method for object: " + object);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * <pre>
     * try to invoke a static method by name or by its obfuscated name in the given clazz.
     * @param clazz the class from which we'll invoke the static method
     * @param obName the obfuscated name of the static method, should be used in final mod
     * @param name the un-obfuscated name of the static method, should be used in dev
     * @param parameters var arg of parameters that the method will be invoked with
     * @return the return value of the method on success, or null and message log on error.
     * </pre>
     */
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    public static <T> T invokeMethod(Class<?> clazz, String obName, String name, Object ... parameters) {
        Class<?>[] parametersTypes = new Class[parameters.length];
        for (int i = 0; i < parameters.length; ++i)
            parametersTypes[i] = parameters[i].getClass();
        try {
            Method out = getAssignableMethod(clazz, obName, name, parametersTypes);
            out.setAccessible(true);
            return (T)out.invoke(null, parameters);
        } catch (Exception e) {
            Log.error(name != null ? name : obName, 1, "invokeMethod -> Invalid method for class: " + clazz + (parameters.length > 0 ? " with parameters: " + Arrays.toString(parameters) : ""));
            e.printStackTrace();
            return null;
        }
    }

    /**
     * <pre>
     * try to invoke a method by name or by its obfuscated name in the given object.
     * @param object the object from which we'll invoke the method
     * @param obName the obfuscated name of the method, should be used in final mod
     * @param name the un-obfuscated name of the method, should be used in dev
     * @param parameters var arg of parameters that the method will be invoked with
     * @return the return value of the method on success, or null and message log on error.
     * </pre>
     */
    @SuppressWarnings({"ConstantConditions", "unchecked"})
    public static <T> T invokeMethod(Object object, String obName, String name, Object ... parameters) {
        Class<?>[] parametersTypes = new Class[parameters.length];
        for (int i = 0; i < parameters.length; ++i)
            parametersTypes[i] = parameters[i].getClass();
        try {
            Method out = getAssignableMethod(object.getClass(), obName, name, parametersTypes);
            out.setAccessible(true);
            return (T)out.invoke(null, parameters);
        } catch (Exception e) {
            Log.error(name != null ? name : obName, 1, "invokeMethod -> Invalid method for object: " + object + (parameters.length > 0 ? " with parameters: " + Arrays.toString(parameters) : ""));
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
            if (paramTypesAreAssignable(tc.getParameterTypes(), parametersTypes)) {
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
