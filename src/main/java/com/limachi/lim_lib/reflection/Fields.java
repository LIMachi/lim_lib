package com.limachi.lim_lib.reflection;

import com.limachi.lim_lib.Log;

import java.lang.reflect.Field;

@SuppressWarnings("unused")
public class Fields {
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
}
