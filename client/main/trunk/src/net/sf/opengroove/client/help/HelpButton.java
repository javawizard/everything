package net.sf.opengroove.client.help;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import net.sf.opengroove.client.OpenGroove;

import com.jidesoft.swing.JideButton;
import com.l2fprod.common.swing.JLinkButton;

/**
 * A button that shows up as a link and, when clicked, opens a help viewer to
 * the specified help topic.<br/><br/>
 * 
 * A note to plugin implementors: If you want to get acess to the help viewer
 * that shows the global help (a help viewer is needed when creating this
 * button), then call
 * {@link net.sf.opengroove.client.OpenGroove#getGlobalHelpViewer()}. If you
 * want access to the help viewer that views help for a particular user, call
 * {@link net.sf.opengroove.client.OpenGroove#getHelpViewer(String)}, passing
 * in the userid of the user in question, which will usually be provided for you
 * by the extension point that you're extending.
 * 
 * @author Alexander Boyd
 * 
 */
public class HelpButton extends JideButton
{
    /**
     * creates a help button that, when clicked, shows the help referenced by
     * the help path specified.
     * 
     * @param name
     *            the label, or text, that shows up on this button
     * @param path
     *            the help path that is to be shown. This must always start with
     *            "/help". 
     */
    public HelpButton(final HelpViewer viewer, String name,
        final String path)
    {
        super();
        addActionListener(new ActionListener()
        {
            
            public void actionPerformed(ActionEvent e)
            {
                viewer.showHelpTopic(path);
            }
        });
    }
}
