package jw.bznetwork.client;

import jw.bznetwork.client.data.AuthUser;
import jw.bznetwork.client.data.model.Configuration;
import jw.bznetwork.client.rpc.GlobalLink;
import jw.bznetwork.client.rpc.GlobalLinkAsync;
import jw.bznetwork.client.rpc.GlobalUnauthLink;
import jw.bznetwork.client.rpc.GlobalUnauthLinkAsync;
import jw.bznetwork.client.ui.HorizontalRule;
import jw.bznetwork.client.ui.Spacer;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DisclosureEvent;
import com.google.gwt.user.client.ui.DisclosureHandler;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.DockPanel;
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
    public static final String SERVER_ERROR = "An error occurred while "
            + "attempting to contact the server. Please check your network "
            + "connection and try again.";
    
    public static final GlobalLinkAsync authLink = GWT.create(GlobalLink.class);
    public static final GlobalUnauthLinkAsync unauthLink = GWT
            .create(GlobalUnauthLink.class);
    
    public static Configuration publicConfiguration;
    
    private static VerticalPanel mainPagePanel = new VerticalPanel();
    
    public static RootPanel rootPanel;
    
    public static String CONTEXT_URL;
    
    /**
     * This is the entry point method.
     */
    public void onModuleLoad()
    {
        rootPanel = RootPanel.get("mainContentPanel");
        rootPanel.add(new Label("Loading..."));
        CONTEXT_URL = GWT.getHostPageBaseURL();
        int fourthSlashIndex = 0;
        for (int i = 0; i < 4; i++)
        {
            fourthSlashIndex = CONTEXT_URL.indexOf('/', fourthSlashIndex + 1);
        }
        CONTEXT_URL = CONTEXT_URL.substring(0, fourthSlashIndex);
        unauthLink.getPublicConfiguration(new AsyncCallback<Configuration>()
        {
            
            @Override
            public void onFailure(Throwable caught)
            {
                epicFail(caught);
            }
            
            @Override
            public void onSuccess(Configuration result)
            {
                if (result == null)
                {
                    /*
                     * This indicates that BZNetwork hasn't been installed.
                     * We'll redirect to install.jsp instead of index.jsp to
                     * prevent an endless redirect loop in case the server is
                     * mistaken.
                     */
                    Window.Location.replace(CONTEXT_URL + "/install.jsp");
                    return;
                }
                publicConfiguration = result;
                init1();
            }
        });
    }
    
    /**
     * Asks the server if we're logged in. If we are, then proceed to init2. If
     * we're not, then proceed to initUnauth2.
     */
    private void init1()
    {
        unauthLink.getThisUser(new AsyncCallback<AuthUser>()
        {
            
            @Override
            public void onFailure(Throwable caught)
            {
                epicFail(caught);
            }
            
            @Override
            public void onSuccess(AuthUser authUser)
            {
                if (authUser == null)
                {
                    initUnauth2();
                }
                else
                {
                    initAuth2(authUser);
                }
            }
        });
    }
    
    protected void initAuth2(AuthUser authUser)
    {
        rootPanel.clear();
        rootPanel.add(new Label("You're logged in. More coming soon! Visit "
                + CONTEXT_URL + "/logout.jsp to log out."));
    }
    
    protected void initUnauth2()
    {
        String mode = Window.Location.getParameter("mode");
        if ("choose-auth-provider".equals(mode))
        {
            showChooseAuthScreen();
        }
        else if ("internal-auth".equals(mode))
        {
            showInternalAuthScreen();
        }
        else
        {
            /*
             * If no mode is specified and we're unauthenticated, redirect to
             * index.jsp.
             */
            Window.Location.replace(CONTEXT_URL + "/index.jsp");
        }
    }
    
    private void showChooseAuthScreen()
    {
        unauthLink.listEnabledAuthProviders(new AsyncCallback<AuthProvider[]>()
        {
            
            @Override
            public void onFailure(Throwable caught)
            {
                epicFail(caught);
            }
            
            @Override
            public void onSuccess(AuthProvider[] result)
            {
                rootPanel.clear();
                showChooseAuthScreen1(result);
            }
        });
    }
    
    protected void showChooseAuthScreen1(AuthProvider[] providers)
    {
        rootPanel.clear();
        VerticalPanel panel = new VerticalPanel();
        panel.setHorizontalAlignment(panel.ALIGN_CENTER);
        panel.add(new HTML("<span style='font-size:18px'><b>"
                + publicConfiguration.getSitename() + "</b></span>"));
        panel.add(new HorizontalRule("100%"));
        panel.add(new HTML("How would you like to log in?"));
        panel.add(new Spacer("3px", "3px"));
        for (AuthProvider p : providers)
        {
            Button b = new Button(p.getText());
            b.addStyleName("plainbutton");
            b.setWidth("250px");
            panel.add(b);
        }
        rootPanel.add(wrapCentered(panel));
    }
    
    /**
     * Creates a dock panel that is 100% width and height, center-aligned in
     * both directions, and adds the specified widget to the center of the dock
     * panel, effectively centering it in the screen.
     * 
     * @param widget
     * @return
     */
    public static DockPanel wrapCentered(Widget widget)
    {
        DockPanel panel = new DockPanel();
        panel.setHorizontalAlignment(panel.ALIGN_CENTER);
        panel.setVerticalAlignment(panel.ALIGN_MIDDLE);
        panel.setWidth("100%");
        panel.setHeight("100%");
        panel.add(widget, panel.CENTER);
        return panel;
    }
    
    private void showInternalAuthScreen()
    {
        rootPanel.clear();
        rootPanel.add(new Label("Coming soon!"));
    }
    
    @SuppressWarnings("deprecation")
    public static void start()
    {
        RootPanel rootPanel = RootPanel.get("mainContentPanel");
        HTMLPanel wrapper = new HTMLPanel(
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
        serverPanel
                .add(new HTML(
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
                .setHTML(
                        3,
                        3,
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
    
    public static void fail(Throwable t)
    {
        t.printStackTrace();
        if (t instanceof PermissionDeniedException)
        {
            Window.alert("A permission error was encountered: "
                    + t.getMessage());
        }
        else
        {
            Window.alert("An unknown error has occured: "
                    + t.getClass().getName() + ": " + t.getMessage());
        }
    }
    
    /**
     * Same as fail, but replaces the entire page with a message that says An
     * error occured. Refresh the page to try again, or visit <logout-url> to
     * log out. Generally reserved for unrecoverable errors, as the method name
     * would imply.
     * 
     * @param t
     */
    public void epicFail(Throwable t)
    {
        rootPanel.clear();
        rootPanel.add(new Label(
                "An error occured. Refresh the page to try again, "
                        + "or, if you're logged in, visit " + CONTEXT_URL
                        + "/logout.jsp to log out. "
                        + "And if you're not logged in, "
                        + "it wouldn't hurt to visit that "
                        + "url to try logging "
                        + "out, just in case you're logged"
                        + " in but you don't know it."));
        fail(t);
    }
}
