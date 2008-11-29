package net.sf.opengroove.es.pong;

/**
 * A simple game of pong. It listens on port 45678 for a telnet connection, and
 * it serves a game of pong over the connection using ansi escape sequences. The
 * ball only moves at a 45 degree angle. The ball is essentially a zero
 * displayed on the screen. The blocks that you have to knock out are shown at
 * the top, and are each a block of space characters with a white background
 * color (or other color), 2 by 2 characters in size. Blocks always have 1
 * character's worth of padding between them (IE no two blocks will ever touch).
 * The paddle is shown as a bunch of white-background spaces, 2 chars high and
 * TBD chars wide. If the ball strikes the paddle at a corner, it will rebound
 * the exact way it came. If the ball strikes the paddle on the top, it will
 * bounce off at a reflective angle. If the ball strikes the paddle on the side,
 * it will bounce off at a reflective angle, which essentially still results in
 * a loss of the ball. If the ball reflects off of the paddle within 200ms of
 * the paddle moving, the ball will be moved one cell in the direction of the
 * paddle before reflecting.
 * 
 * Actions that modify
 * 
 * @author Alexander Boyd
 * 
 */
public class Pong
{
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        
    }
    
}
