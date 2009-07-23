package jw.bznetwork.client;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;

/**
 * The main BZNetwork screen. This class is built to be independent of BZNetwork
 * itself, so that it could be used for any application where a screen that
 * shows a header, a menu, and a list of links in the upper-right corner is
 * needed.<br/><br/>
 * 
 * Screens are installed into the main screen by implementing the Screen
 * interface and adding instances of that interface to the main screen. Each of
 * these then shows up in the menu.<br/><br/>
 * 
 * Links are added to the upper-right corner by providing an HTML string to use
 * and a ClickListener that should be called when the link is clicked.<br/><br/>
 * 
 * The menu, which is used to select the screen to view and to see the
 * currently-selected screen, is accessed by using the Menu link. This is a link
 * automatically added by the main screen to the upper-right corner, as the
 * first link. Clicking on this opens a dropdown menu below the link, which
 * shows one link per screen, with the active screen shown in bold.
 * 
 * @author Alexander Boyd
 * 
 */
public class MainScreen extends Composite
{
    /**
     * Creates a new MainScreen.
     * 
     * @param header The text that should be shown in the header
     * @param headerScreenName True to show the name of the screen
     * @param links
     * @param listeners
     * @param screens
     */
    public MainScreen(String header, boolean headerScreenName, String[] links,
            ClickListener[] listeners, Screen[] screens)
    {
        
    }
}
