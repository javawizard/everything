package net.sf.opengroove.common.components;

import java.awt.Component;

import javax.swing.JPanel;

/**
 * A panel that lays out it's components based on an html string passed to it at
 * the time of construction. Internally, it uses a JTextPane to display
 * components. The HTML string can contain keys, which are of the format
 * ${keyname styletext}, where keyname is the name of the key, and styletext is
 * the text that should have the component replace it. This is useful to apply
 * CSS styles directly to the element in question. Components are then added via
 * the add(String,Component) method, which takes the keyname as it's first
 * argument. No other add methods should be called, as this will mess up the
 * layout of the panel.<br/><br/>
 * 
 * For example, the following code would present essentially the same view as if
 * the panel were replaced by a standard JPanel with a BorderLayout, and the
 * button added to the center of the panel:<br/><br/>
 * 
 * HTMLPanel panel = new HTMLPanel("&lt;div&gt;${testkey &lt;div
 * style='width:100%;height:100%'/&gt;}&lt;/div&gt;"); JButton button = new
 * JButton("Test button"); panel.add(button,"testkey");<br/><br/>
 * 
 * @author Alexander Boyd
 * 
 */
public class HTMLPanel extends JPanel
{
    public void add(Component component, String key)
    {
        
    }
}
