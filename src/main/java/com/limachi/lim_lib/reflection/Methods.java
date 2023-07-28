package com.limachi.lim_lib.reflection;

import com.limachi.lim_lib.Log;

import java.lang.reflect.Method;
import java.util.Arrays;

@SuppressWarnings("unused")
public class Methods {
    protected static Method getAssignableMethod(Class<?> clazz, String obName, String name, Class<?>[] parametersTypes) {
        try {
            return clazz.getMethod(obName, parametersTypes);
        } catch (Exception ignored) {}
        try {
            return clazz.getMethod(name, parametersTypes);
        } catch (Exception ignored) {}
        for (Method tm : clazz.getMethods()) {
            if ((tm.getName().equals(obName) || tm.getName().equals(name)) && Types.paramTypesAreAssignable(tm.getParameterTypes(), parametersTypes)) {
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
            if (parameters[i] == null) {
                Log.error(name != null ? name : obName, 1, "invokeMethod -> Cannot invoke method with null parameters for class: " + clazz + (parameters.length > 0 ? " (parameters: " + Arrays.toString(parameters) : ")"));
                return null;
            } else
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
            return (T)out.invoke(object, parameters);
        } catch (Exception e) {
            Log.error(name != null ? name : obName, 1, "invokeMethod -> Invalid method for object: " + object + (parameters.length > 0 ? " with parameters: " + Arrays.toString(parameters) : ""));
            e.printStackTrace();
            return null;
        }
    }
}
