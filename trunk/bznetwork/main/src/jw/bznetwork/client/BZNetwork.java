package jw.bznetwork.client;

import jw.bznetwork.client.rpc.GlobalLink;
import jw.bznetwork.client.rpc.GlobalLinkAsync;
import jw.bznetwork.client.rpc.GlobalUnauthLink;
import jw.bznetwork.client.rpc.GlobalUnauthLinkAsync;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DisclosureEvent;
import com.google.gwt.user.client.ui.DisclosureHandler;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class BZNetwork implements EntryPoint
{
    /**
     * The message displayed to the user when the server cannot be reached or
     * returns an error.
     */
    public static final String SERVER_ERROR =
        "An error occurred while "
            + "attempting to contact the server. Please check your network "
            + "connection and try again.";
    
    public static final GlobalLinkAsync authLink = GWT.create(GlobalLink.class);
    public static final GlobalUnauthLinkAsync unauthLink =
        GWT.create(GlobalUnauthLink.class);
    
    private static VerticalPanel mainPagePanel = new VerticalPanel();
    
    /**
     * This is the entry point method.
     */
    public void onModuleLoad()
    {
        start();
    }
    
    @SuppressWarnings("deprecation")
    public static void start()
    {
        RootPanel rootPanel = RootPanel.get("mainContentPanel");
        HTMLPanel wrapper =
            new HTMLPanel(
                "<table width='100%' border='0' cellspacing='0' cellpadding='20'><tr><td><div id='network"
                    + "InternalContentWrapper' style='width:100%'></div></td></tr></table>");
        wrapper.add(mainPagePanel, "networkInternalContentWrapper");
        rootPanel.add(wrapper);
        mainPagePanel.setWidth("100%");
        FlexTable topTable = new FlexTable();
        topTable.setHTML(0, 0, "<h1>BZTraining</h1>");
        HorizontalPanel upperRightPanel = new HorizontalPanel();
        final Anchor menuAnchor = new Anchor("Menu");
        Anchor logoutAnchor = new Anchor("Log out");
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
                dialogPanel.add(new Anchor(
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
        upperRightPanel.add(menuAnchor);
        upperRightPanel.add(new HTML("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"));
        upperRightPanel.add(logoutAnchor);
        upperRightPanel.setVerticalAlignment(VerticalPanel.ALIGN_TOP);
        topTable.setWidget(0, 1, upperRightPanel);
        topTable.getFlexCellFormatter().setHorizontalAlignment(0, 0,
            HorizontalPanel.ALIGN_LEFT);
        topTable.getFlexCellFormatter().setHorizontalAlignment(0, 1,
            HorizontalPanel.ALIGN_RIGHT);
        topTable.getFlexCellFormatter().setVerticalAlignment(0, 1,
            VerticalPanel.ALIGN_TOP);
        topTable.setWidth("100%");
        mainPagePanel.add(topTable);
        DisclosurePanel disclosure = new DisclosurePanel("Overlord by Dutchrai");
        final VerticalPanel serverPanel = new VerticalPanel();
        serverPanel.add(new HTML("<b>Users:</b>"));
        serverPanel
            .add(new HTML(
                "&nbsp;&nbsp;<font color='#00cccc'>@</font>&nbsp;<font color='#cc0000'>javawizard2539</font> (Excellence in Action)"));
        serverPanel.add(new HTML(
            "&nbsp;&nbsp;<font color='#00cc00'>phenoxydine</font> (is phake)"));
        serverPanel.setVisible(false);
        disclosure.addEventHandler(new DisclosureHandler()
        {
            
            @Override
            public void onClose(DisclosureEvent event)
            {
                serverPanel.setVisible(false);
            }
            
            @Override
            public void onOpen(DisclosureEvent event)
            {
                serverPanel.setVisible(true);
            }
        });
        FlexTable table = new FlexTable();
        table.addStyleName("bznetwork-ServerListTable");
        table.setBorderWidth(0);
        table.setCellSpacing(1);
        table.setCellPadding(0);
        table.setWidth("100%");
        mainPagePanel.add(table);
        table.setHTML(0, 0, "<b>Port</b>");
        table.getFlexCellFormatter().setWidth(0, 0, "55px");
        table.setHTML(0, 1, "<b>Name</b>");
        table.setHTML(0, 2, "<b>Chat</b>");
        table.setHTML(0, 3, "<b>Links</b>");
        table.setHTML(0, 4, "<b>Details</b>");
        table.setHTML(1, 0, "<hr width='100%'/>");
        table.getFlexCellFormatter().setColSpan(1, 0, 5);
        table.setHTML(2, 0, "<b>A test group</b>");
        table.getFlexCellFormatter().setColSpan(2, 0, 2);
        table.setHTML(3, 0, "5154");
        table.getFlexCellFormatter().setHorizontalAlignment(3, 0,
            HorizontalPanel.ALIGN_RIGHT);
        table.setWidget(3, 1, disclosure);
        table.setHTML(2, 2, "<u>create server</u> <u>rename</u>");
        table
            .setHTML(3, 3,
                "<u>start</u> <u>stop</u> <u>restart</u> <u>logs</u> <u>say</u> <u>live chat</u>");
        table
            .setHTML(
                3,
                4,
                "<font color='#cc0000'>1</font><font color='#aa0000'>/</font><font color='#880000'>15</font>"
                    + "&nbsp;&nbsp;<font color='#00cc00'>1</font><font color='#00aa00'>/</font><font color='#008800'>15</font>"
                    + "&nbsp;&nbsp;<font color='#cccccc'>0</font><font color='#aaaaaa'>/</font><font color='#888888'>15</font>");
        table.setWidget(4, 1, serverPanel);
        table.getFlexCellFormatter().setColSpan(4, 1, 4);
    }
}
