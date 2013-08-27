JPlugin
=======

A universal plugin loader for Java

#Usage
Using JPlugin with your program is very easy! Simply use the SimplePluginLoader to load the jars and load your plugins

Example:

        SimplePluginLoader<MyPluginClass> loader = new SimplePluginLoader<MyPluginClass>();
        
        MyPluginClass[] plugins = loader.loadJars(new File("plugins"));
        
Just be sure your MyPluginClass interface extends the JPlugin interface

    public interface Plugin extends JPlugin {
        ...
    }
    
If your Plugin class is an abstract class, just implement the JPlugin class instead.
