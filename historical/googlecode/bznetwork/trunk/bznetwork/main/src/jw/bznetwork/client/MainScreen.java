package jw.bznetwork.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import jw.bznetwork.client.ui.Header3;
import jw.bznetwork.client.ui.HorizontalRule;
import jw.bznetwork.client.ui.Spacer;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

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
 * In addition, you can specify that the screen should show the menu in a left
 * sidebar of the screen instead of in a menu link.
 * 
 * @author Alexander Boyd
 * 
 */
@SuppressWarnings("deprecation")
public class MainScreen extends Composite implements ClickListener
{
    public class HelpLinkClickListener implements ClickListener
    {
        private String name;
        private String title;
        
        public HelpLinkClickListener(String name, String title)
        {
            super();
            this.name = name;
            this.title = title;
        }
        
        @Override
        public void onClick(Widget sender)
        {
            final PopupPanel box = new PopupPanel();
            int clientWidth = Window.getClientWidth();
            int clientHeight = Window.getClientHeight();
            VerticalPanel panel = new VerticalPanel();
            box.setWidget(panel);
            Frame frame = new Frame(screenHelpPrefix + name + ".html");
            frame.setWidth("" + Math.max(clientWidth - 100, 280) + "px");
            frame.setHeight("" + Math.max(clientHeight - 120, 200) + "px");
            panel.add(frame);
            Button closeButton = new Button("Close");
            panel.add(closeButton);
            closeButton.addClickHandler(new ClickHandler()
            {
                
                @Override
                public void onClick(ClickEvent event)
                {
                    box.hide();
                }
            });
            box.center();
        }
    }
    
    private Label headerLabel;
    private Label headerScreenLabel;
    private ArrayList<Screen> screens = new ArrayList<Screen>();
    private ArrayList<Anchor> screenMenuLinks = new ArrayList<Anchor>();
    private Screen selectedScreen;
    private PopupPanel menuBox = new PopupPanel(true, false);
    private SimplePanel mainContentWrapper = new SimplePanel();
    private HashMap<String, Screen> screensByName = new HashMap<String, Screen>();
    private String screenHelpPrefix;
    private boolean showScreenHelp;
    
    /**
     * Creates a new MainScreen.
     * 
     * @param header
     *            The text that should be shown in the header
     * @param headerScreenName
     *            True to show the name of the current screen right after the
     *            header
     * @param menuLeft
     *            True to show the menu to the left of the page, like Evaluation
     *            Portal does with the subnav list, and false to show it as a
     *            menu link like the rest of the docs for this class describe
     * @param links
     *            The list of links, which can contain html
     * @param listeners
     *            The listeners that correspond to the links given
     * @param screens
     *            The screens that should be installed. There must be at least
     *            one of these.
     */
    @SuppressWarnings("deprecation")
    public MainScreen(String header, boolean headerScreenName,
            boolean menuLeft, String[] links, ClickListener[] listeners,
            Screen[] screens, boolean showScreenHelp, String screenHelpPrefix)
    {
        this.showScreenHelp = showScreenHelp;
        this.screenHelpPrefix = screenHelpPrefix;
        this.headerLabel = new Label(header);
        this.headerScreenLabel = new Label("");
        String wrapperDivId = HTMLPanel.createUniqueId();
        HTMLPanel wrapper = new HTMLPanel(
                "<table width='100%' border='0' cellspacing='0' cellpadding='20'><tr><td><div id='"
                        + wrapperDivId
                        + "' style='width:100%'></div></td></tr></table>");
        initWidget(wrapper);
        VerticalPanel mainPagePanel = new VerticalPanel();
        wrapper.add(mainPagePanel, wrapperDivId);
        mainPagePanel.setWidth("100%");
        FlexTable topTable = new FlexTable();
        HorizontalPanel headerPositionWidget = new HorizontalPanel();
        headerLabel.addStyleName("bznetwork-MainScreenHeader");
        if (menuLeft)
            headerLabel.addStyleName("bznetwork-MainScreenHeaderLeft");
        headerPositionWidget.add(headerLabel);
        if (headerScreenName)
        {
            Label headerSeparator = new Label(":");
            headerSeparator.addStyleName("bznetwork-MainScreenHeaderSeparator");
            headerPositionWidget.add(headerSeparator);
            headerScreenLabel.addStyleName("bznetwork-MainScreenHeaderName");
            headerPositionWidget.add(headerScreenLabel);
        }
        topTable.setWidget(0, 0, headerPositionWidget);
        HorizontalPanel upperRightPanel = new HorizontalPanel();
        upperRightPanel.setVerticalAlignment(upperRightPanel.ALIGN_MIDDLE);
        final Anchor menuAnchor = new Anchor("Menu");
        VerticalPanel menuBoxPanel = new VerticalPanel();
        for (Screen screen : screens)
        {
            Anchor anchor = new Anchor(screen.getTitle());
            anchor.addClickListener(this);
            anchor.addStyleName("bznetwork-MenuScreenItem");
            FlexTable anchorTable = new FlexTable();
            anchorTable.setBorderWidth(0);
            anchorTable.setCellSpacing(0);
            anchorTable.setCellPadding(0);
            anchorTable.setWidget(0, 0, anchor);
            anchorTable.getFlexCellFormatter().setHorizontalAlignment(0, 0,
                    HorizontalPanel.ALIGN_LEFT);
            if (showScreenHelp)
            {
                Anchor helpLink = new Anchor("?");
                helpLink.addClickListener(new HelpLinkClickListener(screen
                        .getName(), screen.getTitle()));
                anchorTable.setWidget(0, 1, helpLink);
                anchorTable.getFlexCellFormatter().setHorizontalAlignment(0, 1,
                        HorizontalPanel.ALIGN_RIGHT);
            }
            anchorTable.setWidth("100%");
            menuBoxPanel.add(anchorTable);
            screenMenuLinks.add(anchor);
        }
        if (!menuLeft)
            menuBox.setWidget(menuBoxPanel);
        menuAnchor.addClickListener(new ClickListener()
        {
            
            @Override
            public void onClick(Widget sender)
            {
                menuBox.setPopupPosition(menuAnchor.getAbsoluteLeft(),
                        menuAnchor.getAbsoluteTop()
                                + menuAnchor.getOffsetHeight());
                menuBox.show();
            }
        });
        if (!menuLeft)
            upperRightPanel.add(menuAnchor);
        for (int i = 0; i < links.length; i++)
        {
            Anchor linkAnchor = new Anchor(links[i], true);
            linkAnchor.addClickListener(listeners[i]);
            linkAnchor.addStyleName("bznetwork-MainScreenRightLink");
            upperRightPanel.add(new HTML("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"));
            upperRightPanel.add(linkAnchor);
        }
        upperRightPanel.setVerticalAlignment(VerticalPanel.ALIGN_TOP);
        topTable.setWidget(0, 1, upperRightPanel);
        topTable.getFlexCellFormatter().setHorizontalAlignment(0, 0,
                HorizontalPanel.ALIGN_LEFT);
        topTable.getFlexCellFormatter().setHorizontalAlignment(0, 1,
                HorizontalPanel.ALIGN_RIGHT);
        topTable.getFlexCellFormatter().setVerticalAlignment(0, 1,
                VerticalPanel.ALIGN_MIDDLE);
        topTable.setWidth("100%");
        mainPagePanel.add(topTable);
        mainPagePanel.add(new HorizontalRule("100%"));
        mainPagePanel.add(new Spacer("3px", "3px"));
        if (menuLeft)
        {
            DockPanel dock = new DockPanel();
            menuBoxPanel.addStyleName("bznetwork-MenuBoxPanelLeft");
            dock.add(menuBoxPanel, dock.WEST);
            dock.setCellWidth(menuBoxPanel, "120px");
            dock.add(mainContentWrapper, dock.CENTER);
            dock.setWidth("100%");
            mainPagePanel.add(dock);
        }
        else
        {
            mainPagePanel.add(mainContentWrapper);
        }
        this.screens.addAll(Arrays.asList(screens));
        for (Screen s : screens)
        {
            s.setParent(this);
            s.init();
            screensByName.put(s.getName(), s);
        }
        selectScreen(0, null);
    }
    
    @Override
    public void onClick(Widget sender)
    {
        int anchorIndex = screenMenuLinks.indexOf(sender);
        selectScreen(anchorIndex, null);
        menuBox.hide();
    }
    
    public void selectScreen(int index, Map<String, String> params)
    {
        Screen previous = selectedScreen;
        if (previous != null)
        {
            if (previous == screens.get(index))
            {
                /*
                 * We're selecting the currently-selected screen, so we'll just
                 * reselect it.
                 */
                previous.reselect(params);
                return;
            }
            /*
             * We're selecting a different screen than the currently-selected
             * one, but there is, indeed, one that is currently selected. We'll
             * deselect it.
             */
            previous.deselect();
            screenMenuLinks.get(screens.indexOf(previous)).removeStyleName(
                    "bznetwork-MenuCurrentScreenItem");
        }
        /*
         * We're either selecting a different screen than the current one, or
         * there is no currently-selected screen.
         */
        selectedScreen = screens.get(index);
        mainContentWrapper.setWidget(selectedScreen.getWidget());
        selectedScreen.select(params);
        screenMenuLinks.get(index).addStyleName(
                "bznetwork-MenuCurrentScreenItem");
        headerScreenLabel.setText(selectedScreen.getTitle());
    }
    
    public void selectScreen(String name, Map<String, String> params)
    {
        selectScreen(screens.indexOf(screensByName.get(name)), params);
    }
    
    public Screen get(String screenName)
    {
        return screensByName.get(screenName);
    }
    
    /**
     * Returns a list of all of the screens in this MainScreen. This list must
     * not be modified by the caller.
     * 
     * @return
     */
    public ArrayList<Screen> getScreenList()
    {
        return screens;
    }
    
    public boolean isSelected(Screen screen)
    {
        return screen.equals(selectedScreen);
    }
    
    public Screen getSelectedScreen()
    {
        return selectedScreen;
    }
    
    /**
     * Adds a new history token.
     * 
     * @param screen
     *            The screen that this token should target
     * @param params
     *            The map of properties for this screen. This map will be
     *            modified by this method to add additional parameters specific
     *            to the MainScreen class.
     */
    public void addToHistory(Screen screen, Map<String, String> params)
    {
        params.put("_page", screen.getName());
        History.newItem(paramsToString(params), false);
    }
    
    public static String paramsToString(Map<String, String> params)
    {
        if (params.size() == 0)
            return "";
        StringBuffer buffer = new StringBuffer();
        for (Entry<String, String> e : params.entrySet())
        {
            buffer.append("&" + URL.encodeComponent(e.getKey()) + "="
                    + URL.encodeComponent(e.getValue()));
        }
        return buffer.substring(1, buffer.length());
    }
    
    public static Map<String, String> stringToParams(String string)
    {
        String[] tokens = string.split("\\&");
        HashMap<String, String> map = new HashMap<String, String>();
        for (String token : tokens)
        {
            String[] split = token.split("\\=");
            String key = split[0];
            String value = split.length > 1 ? split[1] : "";
            map.put(key, value);
        }
        return map;
    }
    
    public void processHistoryToken(String token)
    {
        Map<String, String> map = stringToParams(token);
        String pageName = map.get("_page");
        if (pageName == null)
            return;
        selectScreen(pageName, map);
    }
}
