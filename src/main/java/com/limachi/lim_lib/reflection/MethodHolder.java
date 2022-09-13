package com.limachi.lim_lib.reflection;

import com.limachi.lim_lib.Log;
import com.limachi.lim_lib.Strings;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

public class MethodHolder {
    Class<?> clazz;
    Object object = null;
    String m1;
    String m2 = null;
    Optional<Class<?>[]> parameterTypes = Optional.empty();
    Class<?>[] noTypes = new Class[0];
    public MethodHolder(Class<?> clazz, Object object, String methodName, String obfuscatedMethodName, Class<?> ... parameterTypes) {
        this.clazz = clazz;
        this.object = object;
        this.m1 = obfuscatedMethodName;
        this.m2 = methodName;
        if (parameterTypes.length > 0)
            this.parameterTypes = Optional.of(parameterTypes);
    }
    public MethodHolder(Class<?> clazz, String methodName, String obfuscatedMethodName, Class<?> ... parameterTypes) {
        this.clazz = clazz;
        this.m1 = obfuscatedMethodName;
        this.m2 = methodName;
        if (parameterTypes.length > 0)
            this.parameterTypes = Optional.of(parameterTypes);
    }
    public MethodHolder(Object object, String methodName, String obfuscatedMethodName, Class<?> ... parameterTypes) {
        this.clazz = object.getClass();
        this.object = object;
        this.m1 = obfuscatedMethodName;
        this.m2 = methodName;
        if (parameterTypes.length > 0)
            this.parameterTypes = Optional.of(parameterTypes);
    }
    public MethodHolder(Class<?> clazz, Object object, String methodName, String obfuscatedMethodName) {
        this.clazz = clazz;
        this.object = object;
        this.m1 = obfuscatedMethodName;
        this.m2 = methodName;
    }
    public MethodHolder(Class<?> clazz, String methodName, String obfuscatedMethodName) {
        this.clazz = clazz;
        this.m1 = obfuscatedMethodName;
        this.m2 = methodName;
    }
    public MethodHolder(Object object, String methodName, String obfuscatedMethodName) {
        this.clazz = object.getClass();
        this.object = object;
        this.m1 = obfuscatedMethodName;
        this.m2 = methodName;
    }
    public MethodHolder(Class<?> clazz, Object object, String methodName, Class<?> ... parameterTypes) {
        this.clazz = clazz;
        this.object = object;
        this.m1 = methodName;
        if (parameterTypes.length > 0)
            this.parameterTypes = Optional.of(parameterTypes);
    }
    public MethodHolder(Class<?> clazz, String methodName, Class<?> ... parameterTypes) {
        this.clazz = clazz;
        this.m1 = methodName;
        if (parameterTypes.length > 0)
            this.parameterTypes = Optional.of(parameterTypes);
    }
    public MethodHolder(Object object, String methodName, Class<?> ... parameterTypes) {
        this.clazz = object.getClass();
        this.object = object;
        this.m1 = methodName;
        if (parameterTypes.length > 0)
            this.parameterTypes = Optional.of(parameterTypes);
    }
    public MethodHolder(Class<?> clazz, Object object, String methodName) {
        this.clazz = clazz;
        this.object = object;
        this.m1 = methodName;
    }
    public MethodHolder(Class<?> clazz, String methodName) {
        this.clazz = clazz;
        this.m1 = methodName;
    }
    public MethodHolder(Object object, String methodName) {
        this.clazz = object.getClass();
        this.object = object;
        this.m1 = methodName;
    }
    @SuppressWarnings("unchecked")
    public <T> T invoke(Object ... parameters) {
        Class<?>[] pt = parameterTypes.isEmpty() ? noTypes : parameterTypes.get();
        if (parameterTypes.isEmpty() && parameters.length > 0) {
            pt = new Class[parameters.length];
            for (int i = 0; i < parameters.length; ++i)
                pt[i] = parameters[i].getClass();
        }
        Method method = Methods.getMethod(clazz, m1, m2, pt);
        try {
            return (T)method.invoke(object, parameters);
        } catch (Exception e) {
            Log.error(getMethodName(), 1, "invokeMethod -> Invalid method for class: " + clazz + (parameters.length > 0 ? " with parameters: " + Arrays.toString(parameters) : ""));
            e.printStackTrace();
            return null;
        }
    }
    public Class<?> getClazz() { return object == null ? clazz : object.getClass(); }
    public String getMethodName() { return m2 == null || m2.isBlank() ? m1 : m2; }
    public String getMethod(boolean withClass) { return parameterTypes.isEmpty() ? Strings.getSimpleMethod(withClass ? getClazz().getCanonicalName() : null, getMethodName()) : Strings.getSimpleMethod(getClazz().getCanonicalName(), getMethodName(), parameterTypes.get()); }
}
