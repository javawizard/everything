package jw.bznetwork.client;

import com.google.gwt.user.client.ui.Widget;

/**
 * A screen that can be installed in a MainScreen.
 * 
 * @author Alexander Boyd
 * 
 */
public interface Screen
{
    /**
     * Called when this screen becomes the active screen, after another screen
     * was deselected or if this is the first screen to become active.
     */
    public void select();
    
    /**
     * Called when another screen is about to become the active screen.
     */
    public void deselect();
    
    /**
     * Called when this screen is selected, but when this screen is already the
     * active screen. Screens may wish to reset to their default state when this
     * is called.
     */
    public void reselect();
    
    /**
     * Returns the name of this screen. This is different than the title. The
     * name is used to get a particular screen object from the MainScreen,
     * whereas the title is what the user actually sees.
     * 
     * @return The name of this screen
     */
    public String getName();
    
    /**
     * Returns the title of this screen. This is what the user sees in the menu.
     * In the future, I'm thinking that this will be specified when the screen
     * is added to the MainScreen, to make things more flexible.
     * 
     * @return The title of this screen
     */
    public String getTitle();
    
    /**
     * Returns this screen's widget. This is the widget that is used to actually
     * display the screen.
     * 
     * @return
     */
    public Widget getWidget();
}
