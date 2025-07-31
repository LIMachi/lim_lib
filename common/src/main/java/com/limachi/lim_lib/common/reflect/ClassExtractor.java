package com.limachi.lim_lib.common.reflect;

import com.limachi.lim_lib.common.utils.StackTrace;

import org.objectweb.asm.ClassReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

@SuppressWarnings({"unchecked", "unused", "UnusedReturnValue"})
public class ClassExtractor {
    protected final List<Class<?>> classes = new ArrayList<>();

    /**
     * search by all means from which jar was this class loaded (note: it assumes that it was loaded either from a valid jar, or from a dev environment following the standard build procedure of architectury)
     */
    public static String getBestJarPath(Class<?> clazz) {
        String test = URLDecoder.decode(clazz.getProtectionDomain().getCodeSource().getLocation().getFile(), StandardCharsets.UTF_8);
        if (test.contains(".jar")) {
            String[] t = test.split("\\.jar");
            if (t.length > 1)
                test = t[0] + ".jar";
            File file = new File(test);
            if (file.isFile())
                try {
                    new JarFile(file);
                    return test;
                } catch (IOException e) {}
        }
        String[] t = test.split("/");
        for (int i = t.length - 1; i >= 0; i--) {
            if (t[i].equals("devlibs") || t[i].equals("libs") || t[i].equals("classes") || t[i].equals("build")) {
                StringBuilder truncated = new StringBuilder();
                for (int j = 0; j <= i; ++j)
                    truncated.append(t[j]).append("/");
                boolean classes = t[i].equals("classes");
                if (t[i].equals("build")) {
                    truncated.append("classes/");
                    classes = true;
                }
                if (classes)
                    return truncated.append("java/main/").toString();
                File[] children = new File(truncated.toString()).listFiles();
                int bestLen = Integer.MAX_VALUE;
                File best = null;
                for (File child : children) {
                    int len = child.getName().length();
                    if (len < bestLen) {
                        bestLen = len;
                        best = child;
                    }
                }
                if (best != null)
                    return best.getPath();
            }
        }
        return null;
    }

    public static JarFile getJarFile(String path) {
        String[] t = path.split("\\.jar");
        if (t.length > 1)
            path = t[0] + ".jar";
        try {
            File jarFile = new File(path);
            if (jarFile.exists() && jarFile.getName().endsWith(".jar"))
                return new JarFile(jarFile);
        } catch (Throwable ignore) {}
        return null;
    }

    public static File getFolder(String path) {
        File folder = new File(path);
        if (folder.isDirectory())
            return folder;
        return null;
    }

    public ClassExtractor() { extract(null, null, null, false); }
    public ClassExtractor(String matcher) { extract(null, matcher, null, false); }
    public ClassExtractor(String matcher, Predicate<ClassReader> skip) { extract(null, matcher, skip, false); }
    public ClassExtractor(Predicate<ClassReader> skip) { extract(null, null, skip, false); }
    public ClassExtractor(Class<?> clazz) { extract(clazz, null, null, false); }
    public ClassExtractor(Class<?> clazz, String matcher) { extract(clazz, matcher, null, false); }
    public ClassExtractor(Class<?> clazz, String matcher, Predicate<ClassReader> skip) { extract(clazz, matcher, skip, false); }
    public ClassExtractor(Class<?> clazz, Predicate<ClassReader> skip) { extract(clazz, null, skip, false); }

    private static final Pattern DEFAULT_MATCHER = Pattern.compile(".*");
    private Pattern pattern = DEFAULT_MATCHER;
    private Path topFolder = null;
    private Class<?> clazz = ClassExtractor.class;
    private ClassLoader loader = Thread.currentThread().getContextClassLoader();

    public <T extends ClassExtractor> T extract(Class<?> clazz, String matcher, Predicate<ClassReader> skip, boolean clear) {
        Pattern pattern;
        if (clazz == null) {
            clazz = ClassExtractor.class;
            loader = Thread.currentThread().getContextClassLoader();
        } else
            loader = clazz.getClassLoader();
        if (clear)
            classes.clear();
        if (matcher == null || matcher.isBlank())
            pattern = Pattern.compile(".*");
        else
            pattern = Pattern.compile(matcher);
        String path = getBestJarPath(clazz);
        JarFile jarFile = getJarFile(path);
        if (jarFile != null) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();
                if (entryName.endsWith(".class") && !entry.isDirectory() && pattern.matcher(entryName).find()) {
                    String name = entryName.replace("/", ".").replace("\\", ".").replace(".class", "");
                    try {
                        if (skip != null && skip.test(new ClassReader(jarFile.getInputStream(entry)))) {
                            System.out.println("skipped: " + entryName);
                            continue;
                        }
                        classes.add(clazz.getClassLoader().loadClass(name));
                    } catch (Throwable error) {
                        System.err.println("class " + name + " error:");
                        System.err.println(new StackTrace(error));
                    }
                }
            }
            try {
                jarFile.close();
            } catch (IOException ignore) {}
        } else {
            File folder = getFolder(path);
            if (folder != null) {
                this.pattern = pattern;
                topFolder = folder.toPath();
                this.clazz = clazz;
                recursiveExtractor(folder, skip);
                if (path.endsWith("classes/java/main/")) {
                    folder = getFolder(path.replace("/neoforge/build/", "/common/build/").replace("/fabric/build/", "/common/build/"));
                    if (folder != null) {
                        topFolder = folder.toPath();
                        recursiveExtractor(folder, skip);
                    }
                }
            }
        }
        return (T)this;
    }

    protected void recursiveExtractor(File folder, Predicate<ClassReader> skip) {
        for (File file : Objects.requireNonNull(folder.listFiles()))
            if (file.isDirectory())
                recursiveExtractor(file, skip);
            else if (file.getName().endsWith(".class")) {
                var p = topFolder.relativize(file.toPath());
                String name = p.toString();
                if (pattern.matcher(name).find()) {

                    String pn = name.replace("/", ".").replace("\\", ".").replace(".class", "");
                    try {
                        if (skip != null && skip.test(new ClassReader(new FileInputStream(file)))) {
                            System.out.println("skipped: " + pn);
                            continue;
                        }
                        classes.add(clazz.getClassLoader().loadClass(pn));
                    } catch (Throwable ignore) {
                        System.err.println(ignore);
                    }
                }
            }
    }

    public List<Class<?>> extractedClasses() {
        return classes;
    }
}
