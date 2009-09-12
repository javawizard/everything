package jw.bznetwork.client;

import java.util.HashMap;
import java.util.Map;

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
     * Called only once throughout the screen's lifetime. This is called when
     * the screen is first installed into the MainScreen, and can be used to do
     * stuff like set up the widget.
     */
    public void init();
    
    /**
     * Called when this screen becomes the active screen, after another screen
     * was deselected or if this is the first screen to become active.
     */
    public void select(Map<String, String> params);
    
    /**
     * Called when another screen is about to become the active screen.
     */
    public void deselect();
    
    /**
     * Called when this screen is selected, but when this screen is already the
     * active screen. Screens may wish to reset to their default state when this
     * is called.
     */
    public void reselect(Map<String, String> params);
    
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
     * display the screen. The widget returned should be exactly the same object
     * every time this method is called, so it's advisable to initialize the
     * widget in the init method and then store it in a field.
     * 
     * @return
     */
    public Widget getWidget();
    
    /**
     * Called every 20 seconds by bznetwork. Screens can use this to perform
     * periodic actions. The tick number is a number that increases by one every
     * time this method is called. This can be used to achieve delays that are
     * multiple of 20. For example, executing a particular action every 60
     * seconds could be done by dividing this number by 3, and if the resulting
     * number is a whole number (or, equivalently, the remainder is 0), then the
     * action would be executed.<br/><br/>
     * 
     * Right now, this method is only called on the screen that is currently the
     * active screen.
     * 
     * @param number
     *            The tick number. The first tick is 0.
     */
    public void tick(int number);
    
    /**
     * Called when a history state change occurs that targets this page. The
     * parameters specified are the parameters specified in the url.
     * 
     * @param parameters
     */
    public void historyChanged(Map<String, String> parameters);
    
    public void setParent(MainScreen screen);
}
