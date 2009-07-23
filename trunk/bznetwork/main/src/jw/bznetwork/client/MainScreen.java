package jw.bznetwork.client;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
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
public class MainScreen extends Composite
{
    private Label headerLabel;
    private Label headerScreenLabel;
    private ArrayList<Screen> screens = new ArrayList<Screen>();
    private Screen selectedScreen;
    
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
        final Anchor menuAnchor = new Anchor("Menu");
        menuAnchor.addClickListener(new ClickListener()
        {
            
            @Override
            public void onClick(Widget sender)
            {
                PopupPanel box = new PopupPanel(true, false);
                box.setPopupPosition(menuAnchor.getAbsoluteLeft(), menuAnchor
                        .getAbsoluteTop()
                        + menuAnchor.getOffsetHeight());
                VerticalPanel dialogPanel = new VerticalPanel();
                dialogPanel.add(new Anchor("Reports"));
                dialogPanel.add(new Anchor("Bans"));
                dialogPanel
                        .add(new Anchor(
                                "<span class='bznetwork-MenuCurrentScreenItem'>Servers</span>",
                                true));
                dialogPanel.add(new Anchor("Logs"));
                dialogPanel.add(new Anchor("Live"));
                dialogPanel.add(new Anchor("Users"));
                dialogPanel.add(new Anchor("Callsigns"));
                dialogPanel.add(new Anchor("Roles"));
                dialogPanel.add(new Anchor("Authentication"));
                dialogPanel.add(new Anchor("Help"));
                box.setWidget(dialogPanel);
                box.show();
            }
        });
        
    }
}
