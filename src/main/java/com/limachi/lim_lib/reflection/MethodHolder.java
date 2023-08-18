package com.limachi.lim_lib.reflection;

import com.limachi.lim_lib.Log;
import com.limachi.lim_lib.Strings;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.Arrays;

@SuppressWarnings("unused")
public class MethodHolder {

    protected Class<?> clazz;
    protected Object object = null;
    protected String m1;
    protected String m2 = null;
    protected Class<?>[] optionalParameterTypes = null;

    private static final Class<?>[] noTypes = new Class[0];

    public static MethodHolder fromFirstMatching(Class<?> clazz, String methodName, String obfuscatedMethodName) {
        for (Method m : clazz.getMethods())
            if (m.getName().equals(obfuscatedMethodName) || m.getName().equals(methodName))
                return new MethodHolder(clazz, m.getName(), m.getParameterTypes());
        return null;
    }

    public MethodHolder(Class<?> clazz, Object object, String methodName, String obfuscatedMethodName, Class<?> ... parameterTypes) {
        this.clazz = clazz;
        this.object = object;
        this.m1 = obfuscatedMethodName;
        this.m2 = methodName;
        if (parameterTypes.length > 0)
            this.optionalParameterTypes = parameterTypes;
    }

    public MethodHolder(Class<?> clazz, String methodName, String obfuscatedMethodName, Class<?> ... parameterTypes) {
        this.clazz = clazz;
        this.m1 = obfuscatedMethodName;
        this.m2 = methodName;
        if (parameterTypes.length > 0)
            this.optionalParameterTypes = parameterTypes;
    }

    public MethodHolder(String className, String obfuscatedClassName, String methodName, String obfuscatedMethodName, Class<?> ... parameterTypes) {
        this.clazz = Classes.classByName(obfuscatedClassName, className);
        this.m1 = obfuscatedMethodName;
        this.m2 = methodName;
        if (parameterTypes.length > 0)
            this.optionalParameterTypes = parameterTypes;
    }

    public static MethodHolder byPath(@Nonnull String obfuscatedMethodPath, @Nullable String methodPath, Class<?> ... parameterTypes) {
        String[] s = obfuscatedMethodPath.split(":");
        if (s.length <= 1) {
            Log.error("Invalid method path: `" + obfuscatedMethodPath + "`, should have a : separator between class path and method name");
            return new MethodHolder((Class<?>)null, null);
        }
        String obfuscatedClassName = s[0];
        String obfuscatedMethodName = s[s.length - 1];
        String className = null;
        String methodName = null;
        if (methodPath != null) {
            s = methodPath.split(":");
            if (s.length <= 1)
                Log.error("Invalid alternate method path: `" + methodPath + "`, should have a : separator between class path and method name");
            else {
                className = s[0];
                methodName = s[s.length - 1];
            }
        }
        return new MethodHolder(className, obfuscatedClassName, methodName, obfuscatedMethodName, parameterTypes);
    }

    public static MethodHolder byPath(@Nonnull String obfuscatedMethodPath, @Nullable String methodPath) {
        String[] s = obfuscatedMethodPath.split(":");
        if (s.length <= 1) {
            Log.error("Invalid method path: `" + obfuscatedMethodPath + "`, should have a : separator between class path and method name");
            return new MethodHolder((Class<?>)null, null);
        }
        String obfuscatedClassName = s[0];
        String obfuscatedMethodName = s[s.length - 1];
        String className = null;
        String methodName = null;
        if (methodPath != null) {
            s = methodPath.split(":");
            if (s.length <= 1)
                Log.error("Invalid alternate method path: `" + methodPath + "`, should have a : separator between class path and method name");
            else {
                className = s[0];
                methodName = s[s.length - 1];
            }
        }
        return new MethodHolder(className, obfuscatedClassName, methodName, obfuscatedMethodName);
    }

    public MethodHolder(Object object, String methodName, String obfuscatedMethodName, Class<?> ... parameterTypes) {
        this.clazz = object.getClass();
        this.object = object;
        this.m1 = obfuscatedMethodName;
        this.m2 = methodName;
        if (parameterTypes.length > 0)
            this.optionalParameterTypes = parameterTypes;
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

    public MethodHolder(String className, String obfuscatedClassName, String methodName, String obfuscatedMethodName) {
        this.clazz = Classes.classByName(obfuscatedClassName, className);
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
            this.optionalParameterTypes = parameterTypes;
    }

    public MethodHolder(Class<?> clazz, String methodName, Class<?> ... parameterTypes) {
        this.clazz = clazz;
        this.m1 = methodName;
        if (parameterTypes.length > 0)
            this.optionalParameterTypes = parameterTypes;
    }

    public MethodHolder(String className, String methodName, Class<?> ... parameterTypes) {
        this.clazz = Classes.classByName(className, null);
        this.m1 = methodName;
        if (parameterTypes.length > 0)
            this.optionalParameterTypes = parameterTypes;
    }

    public MethodHolder(Object object, String methodName, Class<?> ... parameterTypes) {
        this.clazz = object.getClass();
        this.object = object;
        this.m1 = methodName;
        if (parameterTypes.length > 0)
            this.optionalParameterTypes = parameterTypes;
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

    public MethodHolder(String className, String methodName) {
        this.clazz = Classes.classByName(className, null);
        this.m1 = methodName;
    }

    public MethodHolder(Object object, String methodName) {
        this.clazz = object.getClass();
        this.object = object;
        this.m1 = methodName;
    }

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public <T> T invoke(Object ... parameters) {
        Class<?>[] pt = optionalParameterTypes == null ? noTypes : optionalParameterTypes;
        if (optionalParameterTypes == null && parameters.length > 0) {
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

    public String getMethod(boolean withClass) { return optionalParameterTypes == null ? Strings.getSimpleMethod(withClass ? getClazz().getCanonicalName() : null, getMethodName()) : Strings.getSimpleMethod(getClazz().getCanonicalName(), getMethodName(), optionalParameterTypes); }

    public Class<?>[] getOptionalParameterTypes() { return optionalParameterTypes; }
}
