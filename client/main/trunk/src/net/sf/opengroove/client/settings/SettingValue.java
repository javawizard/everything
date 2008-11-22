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
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result
            + (booleanValue ? 1231 : 1237);
        long temp;
        temp = Double.doubleToLongBits(doubleValue);
        result = prime * result
            + (int) (temp ^ (temp >>> 32));
        result = prime * result + intValue;
        result = prime * result
            + (int) (longValue ^ (longValue >>> 32));
        result = prime * result
            + ((spec == null) ? 0 : spec.hashCode());
        result = prime
            * result
            + ((stringValue == null) ? 0 : stringValue
                .hashCode());
        return result;
    }
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final SettingValue other = (SettingValue) obj;
        if (booleanValue != other.booleanValue)
            return false;
        if (Double.doubleToLongBits(doubleValue) != Double
            .doubleToLongBits(other.doubleValue))
            return false;
        if (intValue != other.intValue)
            return false;
        if (longValue != other.longValue)
            return false;
        if (spec == null)
        {
            if (other.spec != null)
                return false;
        }
        else if (!spec.equals(other.spec))
            return false;
        if (stringValue == null)
        {
            if (other.stringValue != null)
                return false;
        }
        else if (!stringValue.equals(other.stringValue))
            return false;
        return true;
    }
}
