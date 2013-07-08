package com.jplugins.test;

import java.io.File;

import com.jplugins.plugin.PluginLoader;
import com.jplugins.plugin.JPlugin;

public class Test {
    
    public static void main(String[] args) {
        PluginLoader<MyPlugin> p = null;
        
        MyPlugin[] plugins = p.loadJars(new File("plugins"));
        for (MyPlugin plugin : plugins) {
            plugin.onLoad();
        }
    }
    
    public interface MyPluginClass extends JPlugin {
        public void onLoad();
    }
}
