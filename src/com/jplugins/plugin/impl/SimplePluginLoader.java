package com.jplugins.plugin.impl;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.jplugins.exception.PluginLoadException;
import com.jplugins.plugin.JPlugin;
import com.jplugins.plugin.LoadType;
import com.jplugins.plugin.PluginLoader;

public class SimplePluginLoader<T extends JPlugin> implements PluginLoader<T> {
    private ArrayList<URL> urls = new ArrayList<URL>();
    private ClassLoader loader = URLClassLoader.newInstance(new URL[] {}, getClass().getClassLoader());
    protected LoadType type;
    protected Class<?>[] args;

    /**
     * Load this {@link PluginLoader} with the default {@link ClassLoader} object
     */
    public SimplePluginLoader() { }

    /**
     * Load this {@link PluginLoader} with a custom {@link ClassLoader} object
     * @param loader
     */
    public SimplePluginLoader(ClassLoader loader) {
        this.loader = loader;
    }

    @Override
    public T[] loadJar(File file) throws PluginLoadException {
        return loadJar(file, false);

    }

    @SuppressWarnings("unchecked")
    public T[] loadJar(File file, boolean update) throws PluginLoadException {
        if (file == null)
            throw new InvalidParameterException("The file parameter is null!");
        if (file.isDirectory())
            throw new InvalidParameterException("The file provided in the parameter is a directory!");

        JarFile jfile = null;
        ArrayList<T> plugins = new ArrayList<T>();
        try {
            jfile = new JarFile(file);
        } catch (IOException e) {
            throw new PluginLoadException("Failed to create JarFile object!", e);
        }

        if (jfile != null) {
            Enumeration<JarEntry> entries = jfile.entries();
            if (update) {
                try {
                    removePath(file);
                } catch (MalformedURLException e) {
                    throw new PluginLoadException("Failed to remove file from ClassLoader", e);
                } finally {
                    try {
                        jfile.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                addPath(file);
            } catch (Exception e) {
                throw new PluginLoadException("Failed to add file to ClassLoader", e);
            } finally {
                try {
                    jfile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (entries != null) {
                while (entries.hasMoreElements()) {
                    JarEntry fileName = entries.nextElement();
                    if (fileName.getName().endsWith(".class")) {
                        try {
                            String fullName = fileName.getName();
                            int lastSlash = fullName.indexOf('/');
                            String path = fullName.substring(0, lastSlash + 1);

                            String name = fullName.substring(path.length(), fullName.length() - ".class".length());
                            T plugin = loadPlugin(file, path.replace('/', '.') + name);
                            if (plugin == null)
                                continue;
                            plugins.add(plugin);
                            if (type == LoadType.LOAD_FIRST)
                                break;
                        } catch (Exception e) {
                            throw new PluginLoadException("Failed to load class from jar!", e);
                        } finally {
                            try {
                                jfile.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        try {
            if (jfile != null)
                jfile.close();
        } catch (IOException e) {
            throw new PluginLoadException("Failed to close JarFile object!", e);
        }
        if (type == LoadType.LOAD_LAST) {
            for (int i = 0; i < plugins.size() - 1; i++) {
                plugins.remove(i);
            }
        }

        return (T[]) plugins.toArray(new JPlugin[plugins.size()]);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T loadPlugin(File file, String classPath) throws PluginLoadException {
        try {
            Class<?> class_ = Class.forName(classPath, true, loader);

            if (Modifier.isAbstract(class_.getModifiers()))
                return null;

            if (JPlugin.class.isAssignableFrom(class_)) {
                Class<? extends JPlugin> pluginClass = class_.asSubclass(JPlugin.class);
                Constructor<? extends JPlugin> noArgsConstructor = null;
                try {
                    noArgsConstructor = pluginClass.getConstructor();
                } catch (Exception e) {
                    throw new PluginLoadException("A no-arg constructor was not found!", e);
                }

                JPlugin plugin = noArgsConstructor.newInstance();

                return (T) plugin;
            } else
                return null;
        } catch (Exception e) {
            throw new PluginLoadException("Error loading plugin \"" + classPath + "\"", e);
        }
    }

    @Override
    public ClassLoader getClassLoader() {
        return loader;
    }

    @Override
    public void setClassLoader(ClassLoader loader) {
        this.loader = loader;
    }

    @Override
    public T[] loadJars(File directory) {
        if (!directory.isDirectory())
            throw new InvalidParameterException("The directory provided is not a directory!");
        File[] files = directory.listFiles();
        return loadJars(files);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T[] loadJars(File[] files) {
        ArrayList<T> plugins = new ArrayList<T>();
        for (File f : files) {
            if (f.getName().endsWith(".jar")) {
                try {
                    plugins.add((T) Arrays.asList(loadJar(f)));
                } catch (PluginLoadException e) {
                    e.printStackTrace();
                }
            }
        }
        
        return (T[]) plugins.toArray(new JPlugin[plugins.size()]);
    }

    @Override
    public void unloadJar(File plugin) {
        try {
            removePath(plugin);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setLoadingType(LoadType type) {
        this.type = type;
    }

    @Override
    public LoadType getLoadingType(LoadType type) {
        return type;
    }

    @SuppressWarnings({ "deprecation" })
    private void removePath(File f) throws MalformedURLException {
        urls.remove(f.toURL());
        loader = URLClassLoader.newInstance(urls.toArray(new URL[urls.size()]), getClass().getClassLoader());
    }

    @SuppressWarnings("deprecation")
    private void addPath(File f) throws Exception {
        URL u = f.toURL();
        Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] {URL.class });
        method.setAccessible(true);
        method.invoke(loader, u);
        urls.add(u);
    }
}
