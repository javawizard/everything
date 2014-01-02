package net.sf.opengroove.common.vcard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Properties;

/**
 * A class that represents a vCard. It contains methods for parsing and
 * outputting the vCard file format.
 * 
 * @author Alexander Boyd
 * 
 */
public class VCard
{
    private Properties properties = new Properties();
    
    private VCard()
    {
    }
    
    /**
     * Reads until it encounters BEGIN:VCARD in the stream, and then parses from
     * then on until END:VCARD, or the end of the stream. If there is no
     * BEGIN:VCARD in the stream before the end, null is returned.
     * 
     * @param sourceReader
     * @return
     */
    public static VCard parseSingle(
        BufferedReader sourceReader) throws IOException
    {
        String line;
        while ((line = sourceReader.readLine()) != null
            && !line.equalsIgnoreCase("BEGIN:VCARD"))
            ;
        if (line == null)
            return null;
        VCard card = new VCard();
        String lastProperty = null;
        while ((line = sourceReader.readLine()) != null
            && !line.equalsIgnoreCase("END:VCARD"))
        {
            if (line.trim().equals(""))
                continue;
            if (!line.startsWith(" "))
            {
                /*
                 * Property line
                 */
                String[] split = line.split("\\:", 2);
                lastProperty = split[0];
                card.properties.setProperty(split[0],
                    split[1].replace("\\:", ":"));
            }
            else
            {
                /*
                 * Continuation of a property
                 */
                if (lastProperty == null)
                {
                    System.out
                        .println("line \""
                            + line
                            + "\" appears to be a continuation of a property, but there are no properties declared before it");
                }
                String value = line.substring(1);
                value = value.replace("\\:", ":");
                card.properties.setProperty(lastProperty,
                    card.properties
                        .getProperty(lastProperty)
                        + value);
            }
        }
        return card;
    }
    
    /**
     * Reads as many VCards off of the reader as there are. It calls
     * parseSingle() until null is returned, and then returns all of the cards
     * that it got out of that method.
     * 
     * @param sourceReader
     * @return
     * @throws IOException
     */
    public static VCard[] parse(BufferedReader sourceReader)
        throws IOException
    {
        BufferedReader reader = new BufferedReader(
            sourceReader);
        ArrayList<VCard> cards = new ArrayList<VCard>();
        VCard card;
        while ((card = parseSingle(sourceReader)) != null)
            cards.add(card);
        return cards.toArray(new VCard[0]);
    }
}
