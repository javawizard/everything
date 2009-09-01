package jw.bznetwork.client;

import com.google.gwt.user.client.Timer;

public class TickTimer extends Timer
{
    private MainScreen mainScreen;
    
    private int tickNumber = 0;
    
    @Override
    public void run()
    {
        try
        {
            Screen screen = mainScreen.getSelectedScreen();
            if(screen != null)
                screen.tick(tickNumber);
            tickNumber += 1;
        }
        finally
        {
            start();
        }
    }
    
    public TickTimer(MainScreen mainScreen)
    {
        super();
        this.mainScreen = mainScreen;
    }
    
    public void start()
    {
        schedule(10 * 1000);
    }
    
}
