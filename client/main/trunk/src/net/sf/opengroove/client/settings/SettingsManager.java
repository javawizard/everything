package net.sf.opengroove.client.settings;

import java.util.HashMap;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.l2fprod.common.swing.JButtonBar;

/**
 * A class that manages settings for a particular user. It is supplied upon
 * creation with a SettingStore object, a proxy storage bean which is typically
 * stored as part of a LocalUser or as part of the general local storage. It
 * makes available a dialog which can be used
 * 
 * @author Alexander Boyd
 * 
 */
public class SettingsManager
{
    
    public class Setting
    {
        private SettingSpec spec;
        private SettingType type;
    }
    
    private static HashMap<String, SettingType> registeredTypes = new HashMap<String, SettingType>();
    
    private SettingStore store;
    
    private JDialog frame;
    
    /*
     * PICK UP HERE 11/22/2008: for now, the options dialog can rebuild itself
     * after settings are added. This isn't very effecient, but I don't feel
     * like coding anything else right now. Settings aren't supposed to be added
     * to the manager while the dialog is showing anyway.
     * 
     * Settings are added, specifying a tab, subnav, and group to add to. The
     * tab, subnav, and group must already exist. They can be created by a call
     * to createTab (which also accepts an icon as input), createSubnav
     * (specifying the parent tab's id), and createGroup. Those take an
     * identifier, a name, and for groups some help text. Perhaps, then, under
     * this "only add, never remove", a full rebuild isn't necessary. We can
     * track a tabbedpane for each tab, a panel for each subnav, and a panel for
     * each group, and just add to those. They can be referenced by 3 multilayer
     * hashmaps that map ids to tabbedpanes or panels.
     */

}
