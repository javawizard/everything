/*
 * Created on Sep 10, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package nl.captcha.text;

import java.util.Iterator;
import java.util.Properties;
import java.util.Random;
import java.util.StringTokenizer;

/**
 * @author Administrator
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DefaultTextCreator implements TextProducer
{
    
    Random generator = new Random();
    private int capLength = 5;
    private char[] captchars = new char[] { 'a', 'b', 'c',
        'd', 'e', '2', '3', '4', '5', '6', '7', '8', 'g',
        'f', 'y', 'n', 'm', 'n', 'p', 'w', 'x' };
    
    public void setCharArray(char[] chars)
    {
        this.captchars = chars;
    }
    
    public void setProperties(Properties props)
    {
        
        if (props != null
            && props.containsKey("cap.char.arr"))
        {
            String charString = props
                .getProperty("cap.char.arr");
            if (charString != null
                && !charString.equals(""))
            {
                
                StringTokenizer token = new StringTokenizer(
                    charString, ",");
                this.captchars = new char[token
                    .countTokens()];
                int cnt = 0;
                while (token.hasMoreTokens())
                {
                    captchars[cnt] = ((String) token
                        .nextElement()).toCharArray()[0];
                    cnt++;
                }
                
            }
            
            String l = props.getProperty("cap.char.arr.l");
            if (l != null && !l.equals(""))
            {
                try
                {
                    capLength = Integer.parseInt(l);
                }
                catch (Exception e)
                {
                    // TODO: handle exception
                }
                if (capLength < 2)
                    capLength = 5;
            }
        }
        
    }
    
    public String getText()
    {
        int car = captchars.length - 1;
        
        String capText = "";
        for (int i = 0; i < capLength; i++)
        {
            capText += captchars[generator.nextInt(car) + 1];
            if(capText.length() > 1 && capText.charAt(0) == capText.charAt(1))
                //eliminate duplicate letters
                return getText();
        }
        
        return capText;
        
    }
    
}
