package net.sf.opengroove.client.plugins;

public class Version implements Comparable<Version>
{
    private int major = 0;
    private int minor = 0;
    private int releaseCanidate = 0;
    private int beta = 0;
    private int nightly = 0;
    
    public Version(String string)
    {
        String[] components = string.split("\\.");
        if (components.length > 5)
            throw new IllegalArgumentException(
                "Invalid version string " + string);
        major = Integer.parseInt(components[0]);
        if (components.length > 1)
            minor = Integer.parseInt(components[1]);
        if (components.length > 2)
            releaseCanidate = Integer
                .parseInt(components[2]);
        if (components.length > 3)
            beta = Integer.parseInt(components[3]);
        if (components.length > 4)
            nightly = Integer.parseInt(components[4]);
    }
    
    public String toString()
    {
        String string = toFullString();
        for (int i = 0; i < 4; i++)
        {
            if (string.endsWith(".0"))
                string = string.substring(0, string
                    .length() - 2);
        }
        return string;
    }
    
    public String toFullString()
    {
        return "" + major + "." + minor + "."
            + releaseCanidate + "." + beta + "." + nightly;
    }
    
    public int getMajor()
    {
        return major;
    }
    
    public int getMinor()
    {
        return minor;
    }
    
    public int getReleaseCanidate()
    {
        return releaseCanidate;
    }
    
    public int getBeta()
    {
        return beta;
    }
    
    public int getNightly()
    {
        return nightly;
    }
    
    @Override
    public int compareTo(Version o)
    {
        if (major > o.major)
            return 1;
        else if (major < o.major)
            return -1;
        else if (minor > o.minor)
            return 1;
        else if (minor < o.minor)
            return -1;
        else if (releaseCanidate > o.releaseCanidate)
            return 1;
        else if (releaseCanidate < o.releaseCanidate)
            return -1;
        else if (beta > o.beta)
            return 1;
        else if (beta < o.beta)
            return -1;
        else if (nightly > o.nightly)
            return 1;
        else if (nightly < o.nightly)
            return -1;
        return 0;
    }
    
    /**
     * Compares the version specified with this version. The returned value
     * follows the same notation as described by the Comparator.compareTo()
     * method. The precision indicates how fine of a version component to
     * compare. For example, using a precision of 1 would result in only the
     * major number being compared, and a version with the same major but
     * different minor, release canidate, beta, or nightly version numbers would
     * still be counted as the same. A precision of 2 would compare only the
     * major and minor numbers.
     * 
     * @param o
     * @param precision
     * @return
     */
    public int compareTo(Version o, int precision)
    {
        Version tc = trim(precision);
        Version oc = o.trim(precision);
        return tc.compareTo(oc);
    }
    
    public Version trim(int precision)
    {
        String string = toFullString();
        if (precision < 1 || precision > 5)
            throw new IllegalArgumentException(
                "The precision argument must be within 1 and 5, but it was "
                    + precision);
        for (int i = 0; i < (5 - precision); i++)
        {
            int index = string.lastIndexOf(".");
            string = string.substring(0, index);
        }
        return new Version(string);
    }
}
