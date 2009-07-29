package jw.bznetwork.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import jw.bznetwork.client.ui.HorizontalRule;
import jw.bznetwork.client.ui.Spacer;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
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
 * @author Alexander Boyd
 * 
 */
@SuppressWarnings("deprecation")
public class MainScreen extends Composite implements ClickListener
{
    private Label headerLabel;
    private Label headerScreenLabel;
    private ArrayList<Screen> screens = new ArrayList<Screen>();
    private ArrayList<Anchor> screenMenuLinks = new ArrayList<Anchor>();
    private Screen selectedScreen;
    private PopupPanel menuBox = new PopupPanel(true, false);
    private SimplePanel mainContentWrapper = new SimplePanel();
    private HashMap<String, Screen> screensByName = new HashMap<String, Screen>();
    
    /**
     * Creates a new MainScreen.
     * 
     * @param header
     *            The text that should be shown in the header
     * @param headerScreenName
     *            True to show the name of the current screen right after the
     *            header
     * @param links
     *            The list of links, which can contain html
     * @param listeners
     *            The listeners that correspond to the links given
     * @param screens
     *            The screens that should be installed. There must be at least
     *            one of these.
     */
    @SuppressWarnings("deprecation")
    public MainScreen(String header, boolean headerScreenName, String[] links,
            ClickListener[] listeners, Screen[] screens)
    {
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
            menuBoxPanel.add(anchor);
            screenMenuLinks.add(anchor);
        }
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
        mainPagePanel.add(mainContentWrapper);
        this.screens.addAll(Arrays.asList(screens));
        for (Screen s : screens)
        {
            s.init();
            screensByName.put(s.getName(), s);
        }
        selectScreen(0);
    }
    
    @Override
    public void onClick(Widget sender)
    {
        int anchorIndex = screenMenuLinks.indexOf(sender);
        selectScreen(anchorIndex);
        menuBox.hide();
    }
    
    public void selectScreen(int index)
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
                previous.reselect();
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
        selectedScreen.select();
        screenMenuLinks.get(index).addStyleName(
                "bznetwork-MenuCurrentScreenItem");
        headerScreenLabel.setText(selectedScreen.getTitle());
    }
    
    public void selectScreen(String name)
    {
        selectScreen(screens.indexOf(screensByName.get(name)));
    }
    
    public Screen get(String screenName)
    {
        return screensByName.get(screenName);
    }
}
