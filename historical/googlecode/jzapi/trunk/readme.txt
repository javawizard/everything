To write a java plugin, you create a class that has two static methods: load(String[]) and unload(). When you load the java bzfs plugin itself, you specify two arguments: the classpath that the class resides at, and the name of the class, with packages delimited by forward slashes instead of periods. For example, to run the class com.example.Plugin, located in the myPlugin folder relative to where bzfs is running from, with the jar file sample.jar on the classpath, you'd use

-loadplugin plugins/java/java.dll,sample.jar;myPlugin,com/example/Plugin

to get your plugin up and running. You can include additional params beyond that, and these will be tokenized on the comma character and passed to the load method. For example, if the above loadplugin command line switch were to be re-written to look like

-loadplugin plugins/java/java.dll,sample.jar;myPlugin,com/example/Plugin,someparam,another

then load(String[]) would be called with a string array of length 2, containing "someparam" and "another".

load() should call methods on BzfsAPI to set itself up.

TODO: UPDATE ABOVE DOCS TO REPRESENT ADDITION OF MULTIPLE JAVA PLUGINS LOADED AT A TIME

One important thing to note is that all java plugins within an instance of bzfs are run in the same vm. The classpath for all of these plugins is specified when the java vm plugin is loaded. A useful function of this is to have one java plugin register a class loader to BzfsLoader, and all plugins loaded after that can be classes within that class loader. For example, classes stored across the network could be loaded simply by adding some sort of network class loader first.