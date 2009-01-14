package net.sf.opengroove.client.oldplugins;

import java.util.ArrayList;

public class AccumulatingExtensionPoint implements
    ExtensionPoint
{
    private String id;
    private ArrayList<ExtensionWrapper> extensions = new ArrayList<ExtensionWrapper>();
    
    @Override
    public void init(ExtensionPointContext context)
    {
        this.id = context.getModel().getId();
    }
    
    public String getId()
    {
        return id;
    }
    
    @Override
    public void registerExtension(PluginInfo pInfo,
        ExtensionInfo info, Extension extension)
    {
        ExtensionWrapper wrapper = new ExtensionWrapper();
        wrapper.setExtension(extension);
        wrapper.setInfo(info);
        extensions.add(wrapper);
    }
    
    public ExtensionWrapper[] getExtensions()
    {
        return extensions.toArray(new ExtensionWrapper[0]);
    }
}
