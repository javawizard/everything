package net.sf.opengroove.client.plugins;

public class AccumulatingExtensionWrapper
{
    private Extension extension;
    private ExtensionInfo info;
    public Extension getExtension()
    {
        return extension;
    }
    public ExtensionInfo getInfo()
    {
        return info;
    }
    public void setExtension(Extension extension)
    {
        this.extension = extension;
    }
    public void setInfo(ExtensionInfo info)
    {
        this.info = info;
    }
}
