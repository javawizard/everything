package tests;

/**
 * A test that very quickly prints out System.currentTimeMillis() a ton of
 * times. I'm using this to figure out what the maximum precision is.
 * 
 * @author Alexander Boyd
 * 
 */
public class Test004
{
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        for (int i = 0; i < 1000; i++)
        {
            System.out.println(System.currentTimeMillis());
        }
    }
    
}
