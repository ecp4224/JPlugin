package com.jplugins.plugin;

import java.io.File;

import com.jplugins.exception.PluginLoadException;

/**
 * This class handles the loading and unloading of {@link JPlugin} objects. 
 * @author Eddie
 *
 * @param <T> The jPlugin class to load
 */
public interface PluginLoader<T extends JPlugin> {
    
    /**
     * Load a jar file. Depending on the {@link LoadType}, this method may return
     * all plugins in the jar file or the first one found.
     * @param file
     *            The jar file to load
     * @return
     *        Plugins found
     */
    public T[] loadJar(File file) throws PluginLoadException;
    
    /**
     * Load a single {@link JPlugin} from a jar file. If the class path provided in the parameter was not
     * found, then a {@link PluginLoadException} will be thrown.
     * @param file
     * @param classPath
     * @return
     */
    public T loadPlugin(File file, String classPath) throws PluginLoadException;
    
    /**
     * Get the {@link ClassLoader} that a {@link JPlugin} will be loaded in to.
     * @return
     *        The {@link ClassLoader} object
     */
    public ClassLoader getClassLoader();
    
    /**
     * Set the {@link ClassLoader} that a {@link JPlugin} will be loaded in to. 
     * @param loader
     */
    public void setClassLoader(ClassLoader loader);
    
    /**
     * Load all the .jar files found in the directory provided in the parameter. If the {@link File} object provided is not
     * a directory, then a {@link InvalidParameterException} will be thrown. </br> 
     * This method will use the {@link PluginLoader#loadJar(File)} method to load each file found in the directory.
     * @param directory
     *                 The directory to load the .jar files from
     * @return
     *       An array of {@link JPlugin}
     */
    public T[] loadJars(File directory);
    
    /**
     * Load an array of files. Each file provided will use the {@link PluginLoader#loadJar(File)} method to load the file.
     * @param files
     * @return
     */
    public T[] loadJars(File[] files);
    
    /**
     * Unload a plugin from memory and remove it from the {@link ClassLoader}
     * @param plugin
     */
    public void unloadPlugin(T plugin);
    
    /**
     * Set the {@link LoadType} this {@link PluginLoader} will use when loading Jar files.
     * @param type
     */
    public void setLoadingType(LoadType type);
    
    /**
     * Get the {@link LoadType} this {@link PluginLoader} is using.
     * @param type
     * @return
     *        The {@link LoadType}
     */
    public LoadType getLoadingType(LoadType type);
}
