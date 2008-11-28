package net.sf.opengroove.es.utils;

import java.io.IOException;
import java.io.OutputStream;

/**
 * A class that can wrap an output stream
 * 
 * @author Alexander Boyd
 * 
 */
public class InteractiveConsole
{
    private OutputStream out;
    
    public static final int BLACK = 0;
    public static final int RED = 1;
    public static final int GREEN = 2;
    public static final int YELLOW = 3;
    public static final int BLUE = 4;
    public static final int MAGENTA = 5;
    public static final int CYAN = 6;
    public static final int WHITE = 7;
    
    public static enum Direction
    {
        UP("A"), DOWN("B"), FORWARD("C"), BACK("D");
        private String code;
        
        private Direction(String code)
        {
            this.code = code;
        }
        
        public String getCode()
        {
            return code;
        }
    }
    
    public OutputStream getStream()
    {
        return out;
    }
    
    public InteractiveConsole(OutputStream out)
    {
        this.out = out;
    }
    
    private static final String CSI_PREFIX = "\u001B[";
    
    public void sendEscapeSequence(String sequence)
        throws IOException
    {
        byte[] bytes = (CSI_PREFIX + sequence).getBytes();
        out.write(bytes);
    }
    
    public void setSpecial(int code) throws IOException
    {
        sendEscapeSequence("" + code + "m");
    }
    
    public void setup()
    {
        
    }
    
    public void move(Direction direction, int cells)
        throws IOException
    {
        sendEscapeSequence("" + cells + direction.getCode());
    }
    
    public void downLine(int lines) throws IOException
    {
        sendEscapeSequence("" + lines + "E");
    }
    
    public void upLine(int lines) throws IOException
    {
        sendEscapeSequence("" + lines + "F");
    }
    
    public void setCol(int col) throws IOException
    {
        sendEscapeSequence(";" + col + "H");
    }
    
    public void setRow(int row) throws IOException
    {
        sendEscapeSequence("" + row + ";H");
    }
    
    public void setPos(int row, int col) throws IOException
    {
        sendEscapeSequence("" + row + ";" + col + "H");
    }
    
    public void clearScreen() throws IOException
    {
        sendEscapeSequence("2J");
    }
    
    public void clearToEndLine() throws IOException
    {
        sendEscapeSequence("K");
    }
    
    public void write(String string) throws IOException
    {
        out.write(string.getBytes());
    }
    
    public void setColor(int color) throws IOException
    {
        setSpecial(30 + color);
    }
    
    public void setBackground(int color) throws IOException
    {
        setSpecial(40 + color);
    }
    
    public void setBold(boolean bold) throws IOException
    {
        if (bold)
            setSpecial(1);
        else
            setSpecial(22);
    }
    
}
