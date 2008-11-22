package net.sf.opengroove.client.settings;

public class SettingValue
{
    private SettingSpec spec;
    private String stringValue;
    private int intValue;
    private long longValue;
    private double doubleValue;
    private boolean booleanValue;
    public SettingSpec getSpec()
    {
        return spec;
    }
    public String getStringValue()
    {
        return stringValue;
    }
    public int getIntValue()
    {
        return intValue;
    }
    public long getLongValue()
    {
        return longValue;
    }
    public double getDoubleValue()
    {
        return doubleValue;
    }
    public boolean isBooleanValue()
    {
        return booleanValue;
    }
    public void setSpec(SettingSpec spec)
    {
        this.spec = spec;
    }
    public void setStringValue(String stringValue)
    {
        this.stringValue = stringValue;
    }
    public void setIntValue(int intValue)
    {
        this.intValue = intValue;
    }
    public void setLongValue(long longValue)
    {
        this.longValue = longValue;
    }
    public void setDoubleValue(double doubleValue)
    {
        this.doubleValue = doubleValue;
    }
    public void setBooleanValue(boolean booleanValue)
    {
        this.booleanValue = booleanValue;
    }
}
