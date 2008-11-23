package net.sf.opengroove.client.settings;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import net.sf.opengroove.client.OpenGroove;
import net.sf.opengroove.client.settings.types.CheckboxType;
import net.sf.opengroove.client.settings.types.TextType;
import net.sf.opengroove.common.ui.ComponentUtils;

import com.l2fprod.common.swing.JButtonBar;

/**
 * A class that manages settings for a particular user. It is supplied upon
 * creation with a SettingStore object, a proxy storage bean which is typically
 * stored as part of a LocalUser or as part of the general local storage. It
 * makes available a dialog which can be shown to the user to allow the user to
 * change the settings contained.<br/><br/>
 * 
 * SettingsManager registers some types in it's static initializer. Those are
 * described below, along with the class that implements the type (see the
 * respective classes for documentation on a particular type).<br/> <table
 * border="1" cellspacing="0" cellpadding="1">
 * <tr>
 * <th>Type</th>
 * <th>Class</th>
 * </tr>
 * <tr>
 * <td>text</td>
 * <td>{@link TextType}</td>
 * </tr>
 * <tr>
 * <td>checkbox</td>
 * <td>{@link CheckboxType}</td>
 * </tr>
 * </table>
 * 
 * @author Alexander Boyd
 * 
 */
public class SettingsManager
{
    
    public class Setting
    {
        /**
         * The spec of this setting
         */
        private SettingSpec spec;
        /**
         * This setting's type
         */
        private SettingType type;
        /**
         * The value of this setting as of the last time that the status dialog
         * was saved. This can be compared with the current value of a setting
         * when closing the dialog to see what settings have changed.
         */
        private SettingValue loadedValue;
        private String name;
        private JComponent component;
        private SettingParameters parameters;
        /**
         * The listeners that have registered an interest in this setting. They
         * will be notified when this setting changes.
         */
        private ArrayList<SettingListener> listeners = new ArrayList<SettingListener>();
    }
    
    private ArrayList<Setting> currentSettings = new ArrayList<Setting>();
    
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

    private HashMap<String, JTabbedPane> tabMap = new HashMap<String, JTabbedPane>();
    private HashMap<String, JComponent> tabComponents = new HashMap<String, JComponent>();
    private HashMap<String, HashMap<String, JPanel>> subnavMap = new HashMap<String, HashMap<String, JPanel>>();
    private HashMap<String, HashMap<String, JComponent>> subnavComponents = new HashMap<String, HashMap<String, JComponent>>();
    private HashMap<String, HashMap<String, HashMap<String, JPanel>>> groupMap = new HashMap<String, HashMap<String, HashMap<String, JPanel>>>();
    private HashMap<String, HashMap<String, HashMap<String, JComponent>>> groupComponents = new HashMap<String, HashMap<String, HashMap<String, JComponent>>>();
    private ButtonGroup tabButtonGroup = new ButtonGroup();
    private JPanel contentPanel = new JPanel();
    private JPanel contentOuterPanel = new JPanel();
    private JPanel mainPanel = new JPanel();
    private JPanel lowerPanel = new JPanel();
    private JPanel buttonsPanel = new JPanel();
    private JButtonBar tabBar = new JButtonBar(
        JButtonBar.VERTICAL);
    
    public SettingsManager(Window dialogParent,
        String dialogTitle, SettingStore store)
    {
        this.store = store;
        this.frame = new JDialog(dialogParent);
        frame.setTitle(dialogTitle);
        frame.setIconImage(OpenGroove.getWindowIcon());
        frame.setModal(true);
        mainPanel.setLayout(new BorderLayout());
        contentPanel.setLayout(new BorderLayout());
        contentOuterPanel.setLayout(new BorderLayout());
        lowerPanel.setLayout(new BorderLayout());
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel,
            BoxLayout.X_AXIS));
        mainPanel.add(contentOuterPanel,
            BorderLayout.CENTER);
        JLabel topLabel = new JLabel("OpenGroove Settings");
        topLabel.setFont(Font.decode("").deriveFont(24f)
            .deriveFont(Font.BOLD));
        topLabel.setHorizontalAlignment(topLabel.CENTER);
        topLabel.setBorder(new EmptyBorder(8, 8, 8, 8));
        mainPanel.add(topLabel, BorderLayout.NORTH);
        mainPanel.add(lowerPanel, BorderLayout.SOUTH);
        lowerPanel.add(buttonsPanel, BorderLayout.EAST);
        contentOuterPanel.add(contentPanel,
            BorderLayout.CENTER);
        contentOuterPanel.add(tabBar, BorderLayout.WEST);
        frame.getContentPane()
            .setLayout(new BorderLayout());
        frame.getContentPane().add(mainPanel,
            BorderLayout.CENTER);
        frame.setSize(550, 600);
        loadButtons();
    }
    
    static
    {
        loadDefaultTypes();
    }
    
    /**
     * Loads and registers the default setting types.
     */
    private static void loadDefaultTypes()
    {
        registerType("checkbox", new CheckboxType());
        registerType("text", new TextType());
    }
    
    public static void registerType(String string,
        SettingType textType)
    {
        registeredTypes.put(string, textType);
    }
    
    /**
     * Loads the ok, cancel, and changes buttons.
     */
    private void loadButtons()
    {
        JButton cancel = new JButton("Cancel");
        JButton changes = new JButton("Changes");
        JButton ok = new JButton("OK");
        cancel
            .setToolTipText(ComponentUtils
                .htmlTipWrap("If you click this, all of the changes you "
                    + "have made to your settings since you opened "
                    + "this dialog will be discarded."));
        changes
            .setToolTipText(ComponentUtils
                .htmlTipWrap("Not sure what you've changed? "
                    + "Click on this button to see the changes "
                    + "you've made."));
        ok
            .setToolTipText(ComponentUtils
                .htmlTipWrap("Saves your settings and closes this dialog."));
        buttonsPanel.add(ok);
        buttonsPanel.add(changes);
        buttonsPanel.add(cancel);
        cancel.addActionListener(new ActionListener()
        {
            
            public void actionPerformed(ActionEvent e)
            {
                frame.hide();
                /*
                 * We don't need to do any setting value changes; those will be
                 * done when the dialog is shown next time.
                 */
            }
        });
        changes.addActionListener(new ActionListener()
        {
            
            public void actionPerformed(ActionEvent e)
            {
                JOptionPane
                    .showMessageDialog(
                        frame,
                        "This isn't supported yet. We'll work to add it. "
                            + "If you really want it, contact us (see the "
                            + "'contact us' menu item in the launchbar)");
            }
        });
        ok.addActionListener(new ActionListener()
        {
            
            public void actionPerformed(ActionEvent e)
            {
                /*
                 * First thing to do is to scan and validate that all setting
                 * inputs are valid.
                 */
                for (Setting setting : new ArrayList<Setting>(
                    currentSettings))
                {
                    String validString = setting.type
                        .validate(setting.component,
                            setting.parameters);
                    if (validString != null)
                    {
                        JOptionPane
                            .showMessageDialog(
                                frame,
                                "<html>The setting:<br/><br/>"
                                    + setting.name
                                    + "<br/><br/>Is not valid for this reason:<br/><br/>"
                                    + validString);
                        return;
                    }
                }
                /*
                 * We need to scan for changes in all of the settings (by
                 * copying the loaded value, having the type store the value,
                 * and comparing them for changes), and store those that have
                 * changed and notify their listeners.
                 */
                for (Setting setting : new ArrayList<Setting>(
                    currentSettings))
                {
                    SettingValue oldValue = setting.loadedValue;
                    SettingValue newValue = (SettingValue) oldValue
                        .clone();
                    setting.type.storeValue(
                        setting.component, newValue,
                        setting.parameters);
                    if (oldValue.equals(newValue))
                        /*
                         * This setting wasn't changed.
                         */
                        return;
                    /*
                     * The setting has been changed. We'll store it and then
                     * notify the setting's listeners.
                     */
                    SettingStoredValue storedValue = store
                        .getSettingValue(setting.spec
                            .getTabId(), setting.spec
                            .getSubnavId(), setting.spec
                            .getGroupId(), setting.spec
                            .getSettingId());
                    if (storedValue == null)
                    {
                        storedValue = store
                            .createSettingValue();
                        storedValue.setTabId(setting.spec
                            .getTabId());
                        storedValue
                            .setSubnavId(setting.spec
                                .getSubnavId());
                        storedValue.setGroupId(setting.spec
                            .getGroupId());
                        storedValue
                            .setSettingId(setting.spec
                                .getSettingId());
                        store.getSettings()
                            .add(storedValue);
                    }
                    newValue.copyTo(storedValue);
                    /*
                     * The value is now stored. All that's left is to notify the
                     * setting's listeners.
                     */
                    Object newValueObject = setting.type
                        .getValue(newValue,
                            setting.parameters);
                    for (SettingListener listener : new ArrayList<SettingListener>(
                        setting.listeners))
                    {
                        try
                        {
                            listener.settingChanged(
                                setting.spec,
                                newValueObject);
                        }
                        catch (Exception exception)
                        {
                            exception.printStackTrace();
                        }
                    }
                }
                frame.hide();
            }
        });
    }
    
    public synchronized void addTab(String id, Icon icon,
        String name, String description)
    {
        if (tabComponents.containsKey(id)
            || tabMap.containsKey(id))
            return;
        final JToggleButton tabButton = new JToggleButton(
            name);
        if (icon != null)
        {
            tabButton.setIcon(icon);
            tabButton
                .setVerticalTextPosition(tabButton.BOTTOM);
            tabButton
                .setHorizontalTextPosition(tabButton.CENTER);
        }
        tabButtonGroup.add(tabButton);
        tabBar.add(tabButton);
        if (tabBar.getComponentCount() == 1)
            tabButton.setSelected(true);
        /*
         * Now we create a tabbed pane and put it in the tab map and the tab
         * components map. We'll also add it to the content panel if this is the
         * first one added.
         */
        final JTabbedPane pane = new JTabbedPane();
        tabMap.put(id, pane);
        tabComponents.put(id, pane);
        if (tabBar.getComponentCount() == 1)
            contentPanel.add(pane);
        subnavMap.put(id, new HashMap<String, JPanel>());
        groupMap.put(id,
            new HashMap<String, HashMap<String, JPanel>>());
        subnavComponents.put(id,
            new HashMap<String, JComponent>());
        tabButton.addActionListener(new ActionListener()
        {
            
            public void actionPerformed(ActionEvent e)
            {
                contentPanel.removeAll();
                contentPanel.add(pane);
                contentPanel.invalidate();
                contentPanel.validate();
                contentPanel.repaint();
            }
        });
        groupComponents
            .put(
                id,
                new HashMap<String, HashMap<String, JComponent>>());
    }
    
    public synchronized void addSubnav(String tabId,
        String id, String name, String description)
    {
        if (subnavMap.get(tabId) == null)
            throw new IllegalArgumentException(
                "Invalid tab");
        if (subnavMap.get(tabId).get(id) != null)
            return;
        JTabbedPane tab = tabMap.get(tabId);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel,
            BoxLayout.Y_AXIS));
        JPanel outerPanel = new JPanel();
        outerPanel.setLayout(new BorderLayout());
        outerPanel.add(panel, BorderLayout.NORTH);
        subnavMap.get(tabId).put(id, panel);
        subnavComponents.get(tabId).put(id, outerPanel);
        groupMap.get(tabId).put(id,
            new HashMap<String, JPanel>());
        groupComponents.get(tabId).put(id,
            new HashMap<String, JComponent>());
        tab.add(name, outerPanel);
        /*
         * Add the default group
         */
        addGroup(tabId, id, "", "", "");
    }
    
    public synchronized void addGroup(String tabId,
        String subnavId, String id, String name,
        String description)
    {
        if (groupMap.get(tabId) == null)
            throw new IllegalArgumentException(
                "nonexistant tab");
        if (groupMap.get(tabId).get(subnavId) == null)
            throw new IllegalArgumentException(
                "nonexistant subnav");
        if (groupMap.get(tabId).get(subnavId).get(id) != null)
            return;
        JPanel subnav = subnavMap.get(tabId).get(subnavId);
        JPanel groupPanel = new JPanel();
        JPanel internalGroupPanel = new JPanel();
        /*
         * groupPanel contains the components and expand/collapse stuff,
         * internalGroupPanel will hold the actual settings.
         * 
         * For now, we'll just add a titled border. In the future, I'll add some
         * sort of expandable/collabsible header.
         */
        groupPanel.setLayout(new BorderLayout());
        internalGroupPanel.setLayout(new BoxLayout(
            internalGroupPanel, BoxLayout.Y_AXIS));
        groupPanel.setMaximumSize(new Dimension(
            Integer.MAX_VALUE, Integer.MAX_VALUE));
        groupPanel.setAlignmentX(0);
        groupPanel.setAlignmentY(0);
        groupPanel.add(internalGroupPanel,
            BorderLayout.CENTER);
        if (!name.equals(""))
            groupPanel.setBorder(new CompoundBorder(
                new CompoundBorder(new EmptyBorder(3, 2, 5,
                    2), new TitledBorder(new LineBorder(
                    Color.GRAY), name)), new EmptyBorder(2,
                    2, 2, 2)));
        else
            groupPanel.setBorder(new CompoundBorder(
                new CompoundBorder(new EmptyBorder(3, 2, 5,
                    2), new EmptyBorder(1, 1, 1, 1)),
                new EmptyBorder(2, 2, 2, 2)));
        groupComponents.get(tabId).get(subnavId).put(id,
            groupPanel);
        groupMap.get(tabId).get(subnavId).put(id,
            groupPanel);
        subnav.add(groupPanel);
    }
    
    public synchronized void addSetting(String tabId,
        String subnavId, String groupId, String id,
        String name, String description, SettingType type,
        SettingParameters parameters)
    {
        
    }
    
    /**
     * This method shows the dialog for this SettingsManager over the window
     * passed to the settingsmanager's constructor. This method will block until
     * the user clicks one of the buttons and hides the dialog.
     */
    public void showDialog()
    {
        loadSettingComponents();
        frame.setLocationRelativeTo(frame.getOwner());
        frame.show();
        frame.dispose();
    }
    
    private synchronized void loadSettingComponents()
    {
        for (Setting setting : currentSettings)
        {
            SettingType type = setting.type;
            SettingSpec spec = setting.spec;
            SettingStoredValue storedValue = store
                .getSettingValue(spec.getTabId(), spec
                    .getSubnavId(), spec.getGroupId(), spec
                    .getSettingId());
            if (storedValue == null)
            {
                storedValue = store.createSettingValue();
                storedValue.setTabId(setting.spec
                    .getTabId());
                storedValue.setSubnavId(setting.spec
                    .getSubnavId());
                storedValue.setGroupId(setting.spec
                    .getGroupId());
                storedValue.setSettingId(setting.spec
                    .getSettingId());
                store.getSettings().add(storedValue);
            }
            SettingValue value = new SettingValue();
            value.copyFrom(storedValue);
            value.setSpec(setting.spec);
            setting.loadedValue = value;
            type.loadValue(setting.component, value,
                setting.parameters);
        }
    }
}
