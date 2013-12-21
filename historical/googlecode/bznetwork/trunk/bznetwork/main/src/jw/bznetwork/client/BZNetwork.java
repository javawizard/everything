package jw.bznetwork.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import jw.bznetwork.client.MainScreen.HelpLinkClickListener;
import jw.bznetwork.client.data.AuthUser;
import jw.bznetwork.client.data.model.Configuration;
import jw.bznetwork.client.rpc.GlobalLink;
import jw.bznetwork.client.rpc.GlobalLinkAsync;
import jw.bznetwork.client.rpc.GlobalUnauthLink;
import jw.bznetwork.client.rpc.GlobalUnauthLinkAsync;
import jw.bznetwork.client.screens.ActionsScreen;
import jw.bznetwork.client.screens.AuthenticationScreen;
import jw.bznetwork.client.screens.AuthgroupsScreen;
import jw.bznetwork.client.screens.BanfilesScreen;
import jw.bznetwork.client.screens.CallsignsScreen;
import jw.bznetwork.client.screens.ConfigurationScreen;
import jw.bznetwork.client.screens.HelpScreen;
import jw.bznetwork.client.screens.IRCScreen;
import jw.bznetwork.client.screens.LogsScreen;
import jw.bznetwork.client.screens.RolesScreen;
import jw.bznetwork.client.screens.ServersScreen;
import jw.bznetwork.client.screens.SessionsScreen;
import jw.bznetwork.client.screens.TriggersScreen;
import jw.bznetwork.client.screens.WelcomeScreen;
import jw.bznetwork.client.ui.HorizontalRule;
import jw.bznetwork.client.ui.Spacer;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.HistoryListener;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DisclosureEvent;
import com.google.gwt.user.client.ui.DisclosureHandler;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This is the main client-side class. It is the class that is run when the user
 * loads BZNetwork in their browser. Specifically, the {@link #onModuleLoad()}
 * is the method that is called.
 */
public class BZNetwork implements EntryPoint
{
    /**
     * The message displayed to the user when the server cannot be reached or
     * returns an error.
     */
    public static final String SERVER_ERROR = "An error occurred while "
            + "attempting to contact the server. Please check your network "
            + "connection and try again. \nFailing that, join "
            + "#bztraining on irc.freenode.net and ask jcp for help.";
    
    public static final GlobalLinkAsync authLink = GWT.create(GlobalLink.class);
    public static final GlobalUnauthLinkAsync unauthLink = GWT
            .create(GlobalUnauthLink.class);
    
    public static Configuration publicConfiguration;
    
    private static VerticalPanel mainPagePanel = new VerticalPanel();
    
    public static RootPanel rootPanel;
    
    public static String CONTEXT_URL;
    
    public static AuthUser currentUser;
    
    public static MainScreen mainScreen;
    
    private Screen[] defaultScreens;
    
    /**
     * This is where everything starts. When BZNetwork loads in the client's
     * browser, this is the method that is called. It's basically the same in
     * purpose as C++'s "int main()".
     */
    public void onModuleLoad()
    {
        
        GWT.setUncaughtExceptionHandler(new UncaughtExceptionHandler()
        {
            
            @Override
            public void onUncaughtException(Throwable e)
            {
                Window
                        .alert("An internal error has occured as a result of an uncaught exception.");
                fail(e);
            }
        });
        /*
         * First things first. We need to remove the "loading" text that's
         * placed in the page by default.
         */
        com.google.gwt.dom.client.Element element = Document.get()
                .getElementById("loadingInformationBox");
        if (element != null)
        {
            com.google.gwt.dom.client.Element parentElement = element
                    .getParentElement();
            if (parentElement != null)
                parentElement.removeChild(element);
        }
        /*
         * Now that the loading text is removed, we can get on to adding our own
         * custom loading text.
         */
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
        Window.setTitle(publicConfiguration.getString(Settings.sitename));
        try
        {
            dateFormat = DateTimeFormat.getFormat(publicConfiguration
                    .getString(Settings.datetimeformat));
            dateOnlyFormat = DateTimeFormat.getFormat(publicConfiguration
                    .getString(Settings.dateformat));
        }
        catch (Exception e)
        {
            dateFormat = DateTimeFormat.getFormat("'Invalid date/time format'");
            dateOnlyFormat = DateTimeFormat
                    .getFormat("'Invalid date-only format'");
        }
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
        currentUser = authUser;
        Perms.installProvider(new ClientPermissionsProvider(authUser));
        rootPanel.clear();
        ArrayList<Screen> defaultScreenList = new ArrayList<Screen>();
        defaultScreenList.add(new WelcomeScreen());
        if (Perms.global("edit-configuration"))
            defaultScreenList.add(new ConfigurationScreen());
        if (Perms.global("manage-roles"))
            defaultScreenList.add(new RolesScreen());
        if (Perms.global("manage-banfiles"))
            defaultScreenList.add(new BanfilesScreen());
        if (Perms.global("manage-callsign-auth"))
        {
            defaultScreenList.add(new AuthgroupsScreen());
            defaultScreenList.add(new CallsignsScreen());
        }
        /*
         * This one is dependent on some perms, but since the rules are
         * complicated, we'll just add the screen anyway and let the server
         * decide the perms when it sends the server list back to the client.
         */
        defaultScreenList.add(new ServersScreen());
        defaultScreenList.add(new LogsScreen());
        if (Perms.global("manage-irc"))
            defaultScreenList.add(new IRCScreen());
        if (Perms.global("manage-triggers"))
            defaultScreenList.add(new TriggersScreen());
        if (Perms.global("manage-auth"))
            defaultScreenList.add(new AuthenticationScreen());
        if (Perms.global("view-action-log"))
            defaultScreenList.add(new ActionsScreen());
        if (Perms.global("view-sessions"))
            defaultScreenList.add(new SessionsScreen());
        defaultScreenList.add(new HelpScreen());
        defaultScreens = defaultScreenList.toArray(new Screen[0]);
        ArrayList<String> linkNames = new ArrayList<String>();
        ArrayList<ClickListener> linkListeners = new ArrayList<ClickListener>();
        // Help link if applicable
        if (publicConfiguration.getBoolean(Settings.singlehelplink))
        {
            linkNames.add("Help");
            linkListeners.add(new ClickListener()
            {
                
                @Override
                public void onClick(Widget sender)
                {
                    HelpLinkClickListener targetListener = mainScreen.new HelpLinkClickListener(
                            mainScreen.getSelectedScreen().getName(),
                            mainScreen.getSelectedScreen().getTitle());
                    targetListener.onClick(sender);
                }
            });
        }
        // Log out link
        linkNames.add("Log out");
        linkListeners.add(new ClickListener()
        {
            
            @Override
            public void onClick(Widget sender)
            {
                showLoadingBox();
                Window.Location.assign(CONTEXT_URL + "/logout.jsp");
            }
        });
        mainScreen = new MainScreen(publicConfiguration
                .getString(Settings.sitename), publicConfiguration
                .getBoolean(Settings.currentname), publicConfiguration
                .getBoolean(Settings.menuleft), linkNames
                .toArray(new String[0]), linkListeners
                .toArray(new ClickListener[0]), defaultScreens,
                publicConfiguration.getBoolean(Settings.pagehelp), CONTEXT_URL
                        + "/screen-help/");
        mainScreen.selectScreen("welcome", null);
        mainScreen.setWidth("100%");
        rootPanel.add(mainScreen);
        TickTimer tickTimer = new TickTimer(mainScreen);
        tickTimer.start();
        if (History.getToken() != null && !History.getToken().equals(""))
        {
            mainScreen.processHistoryToken(History.getToken());
        }
        History.addHistoryListener(new HistoryListener()
        {
            
            @Override
            public void onHistoryChanged(String historyToken)
            {
                mainScreen.processHistoryToken(historyToken);
            }
        });
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
    
    @SuppressWarnings("deprecation")
    protected void showChooseAuthScreen1(AuthProvider[] providers)
    {
        rootPanel.clear();
        VerticalPanel panel = new VerticalPanel();
        panel.setHorizontalAlignment(panel.ALIGN_CENTER);
        panel.add(new HTML("<span style='font-size:20px'><b>"
                + publicConfiguration.getString(Settings.sitename)
                + "</b></span>"));
        panel.add(new HorizontalRule("100%"));
        panel
                .add(new HTML(
                        "<span style='font-size: 15px'>How would you like to log in?</span>"));
        panel.add(new Spacer("6px", "6px"));
        for (final AuthProvider p : providers)
        {
            Button b = new Button(p.getText().replace("{site-name}",
                    publicConfiguration.getString(Settings.sitename)));
            b.setStylePrimaryName("bznetwork-ChooseAuthProviderButton");
            b.setWidth("250px");
            b.addClickListener(new ClickListener()
            {
                
                @Override
                public void onClick(Widget sender)
                {
                    Window.Location.assign(p.getUrl().replace("{path}",
                            CONTEXT_URL).replace("{path-encoded}",
                            URL.encode(CONTEXT_URL)));
                }
            });
            panel.add(b);
            panel.add(new Spacer("5px", "5px"));
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
    
    /**
     * Those of you that use GNOME (or more specifically, gdm) will probably
     * know that if you use "Require Quarter" as your username to log in (at
     * which point it lets you enter your username again), then once you log in
     * gdm pops up a message that says "Please insert 25 cents to log in". I
     * thought it would be fun to add a similar easter egg to BZNetwork; this is
     * what this field is for.<br/><br/>
     * 
     * I'm also considering adding "Gimme Random Cursor", but I can't remember
     * at the moment how to change the cursor in a browser.
     */
    private static boolean requireQuarter = false;
    
    @SuppressWarnings("deprecation")
    private void showInternalAuthScreen()
    {
        rootPanel.clear();
        VerticalPanel panel = new VerticalPanel();
        panel.setHorizontalAlignment(panel.ALIGN_CENTER);
        panel.add(new HTML("<span style='font-size:20px'><b>"
                + publicConfiguration.getString(Settings.sitename)
                + "</b></span>"));
        panel.add(new HorizontalRule("100%"));
        panel
                .add(new HTML(
                        "<span style='font-size: 14px'>Enter your username and password.</span>"));
        panel.add(new Spacer("6px", "6px"));
        FlexTable table = new FlexTable();
        final TextBox usernameField = new TextBox();
        final PasswordTextBox passwordField = new PasswordTextBox();
        usernameField.setVisibleLength(17);
        passwordField.setVisibleLength(17);
        usernameField.setTitle("Type your "
                + publicConfiguration.getString(Settings.sitename)
                + " username here.");
        passwordField.setTitle("Type your password here.");
        table.setHTML(0, 0, "Username:");
        table.setHTML(1, 0, "Password:");
        table.setWidget(0, 1, usernameField);
        table.setWidget(1, 1, passwordField);
        table.setWidth("100%");
        table.getFlexCellFormatter().setHorizontalAlignment(0, 1,
                HorizontalPanel.ALIGN_RIGHT);
        table.getFlexCellFormatter().setHorizontalAlignment(1, 1,
                HorizontalPanel.ALIGN_RIGHT);
        Button loginButton = new Button("<b>Log in</b>");
        loginButton.setTitle("When you've entered your information, "
                + "click this button to log in.");
        Anchor differentProviderLink = new Anchor(
                "<small>Log in with different credentials</small>", true);
        VerticalPanel vp = new VerticalPanel();
        vp.setVerticalAlignment(vp.ALIGN_MIDDLE);
        vp.setHorizontalAlignment(vp.ALIGN_LEFT);
        vp.setHeight("100%");
        vp.add(differentProviderLink);
        differentProviderLink
                .setTitle("Click this to use other credentials to log "
                        + "in, such as your bzflag callsign.");
        panel.add(table);
        DockPanel dp = new DockPanel();
        dp.setWidth("100%");
        panel.add(dp);
        dp.add(vp, DockPanel.WEST);
        dp.add(loginButton, DockPanel.EAST);
        dp.setCellHorizontalAlignment(loginButton, dp.ALIGN_RIGHT);
        dp.setVerticalAlignment(dp.ALIGN_MIDDLE);
        rootPanel.add(wrapCentered(panel));
        differentProviderLink.addClickListener(new ClickListener()
        {
            
            @Override
            public void onClick(Widget sender)
            {
                rootPanel.clear();
                rootPanel.add(new Label("Loading..."));
                showChooseAuthScreen();
            }
        });
        loginButton.addClickListener(new ClickListener()
        {
            
            @Override
            public void onClick(Widget sender)
            {
                if (usernameField.getText().equalsIgnoreCase("require quarter")
                        && !requireQuarter)
                {
                    requireQuarter = true;
                    usernameField.setText("");
                    return;
                }
                final PopupPanel box = showLoadingBox();
                unauthLink.login(usernameField.getText(), passwordField
                        .getText(), new AsyncCallback<String>()
                {
                    
                    @Override
                    public void onFailure(Throwable caught)
                    {
                        box.hide();
                        fail(caught);
                    }
                    
                    @Override
                    public void onSuccess(String result)
                    {
                        if (result == null)
                        {
                            /*
                             * We've successfully logged in.
                             */
                            if (requireQuarter)
                                Window
                                        .alert("Please insert 25 cents to log in.");
                            Window.Location.assign(CONTEXT_URL
                                    + "/BZNetwork.html");
                        }
                        else
                        {
                            box.hide();
                            Window.alert(result);
                        }
                    }
                });
            }
        });
        usernameField.setFocus(true);
    }
    
    /**
     * This method is no longer used. It was used to mock up the servers page
     * before I actually started working on BZNetwork at all, and I'm only
     * keeping it around so that I can consult various portions of how it
     * rendered the servers page mockup.
     */
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
    
    /**
     * Pops up a message indicating that an error has occurred. The user will
     * still be able to use BZNetwork after dismissing the error message. If the
     * error is a fatal one that should prevent further use of BZNetwork until
     * it is reloaded in the user's browser, consider using
     * {@link #epicFail(Throwable)} instead.
     * 
     * @param t
     *            The throwable whose information will be shown in the error
     *            box. This must not be null.
     */
    public static void fail(Throwable t)
    {
        t.printStackTrace();
        if (t instanceof PermissionDeniedException)
        {
            Window.alert("A permission error was encountered: "
                    + t.getMessage());
        }
        else if (t instanceof ShowMessageException)
        {
            Window.alert(t.getMessage());
        }
        else if (t instanceof StatusCodeException
                && ((StatusCodeException) t).getStatusCode() == 401)
        {
            Window.alert("You have been logged out, probably because "
                    + "your session timed out. Refresh the page "
                    + "to log back in.");
        }
        else if (t instanceof StatusCodeException
                && ((StatusCodeException) t).getStatusCode() == 0)
        {
            Window
                    .alert("You aren't connected to the internet. Connect to the "
                            + "internet, then try again. If you're sure you're connected,"
                            + " our servers might be down for maintenance, so wait a few "
                            + "minutes and try again.");
        }
        else if (t instanceof StatusCodeException)
        {
            Window
                    .alert("A status code other than 200 was received from the server: "
                            + ((StatusCodeException) t).getStatusCode()
                            + " "
                            + t.getClass().getName() + ": " + t.getMessage());
        }
        else
        {
            Window.alert("An unknown error has occured: "
                    + t.getClass().getName() + ": " + t.getMessage());
        }
    }
    
    /**
     * Same as fail, but replaces the entire page with a message that says
     * something like An error occured, refresh the page to try again, or visit
     * <logout-url> to log out. Generally reserved for unrecoverable errors, as
     * the method name would imply.
     * 
     * @param t
     */
    public static void epicFail(Throwable t)
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
    
    /**
     * Creates a modal dialog box that says "Loading..." (and will have a
     * spinner widget in the future), shows it, centers it, and returns it.
     * 
     * @return
     */
    public static PopupPanel showLoadingBox()
    {
        PopupPanel box = new PopupPanel(false, true);
        box.setWidget(new Label("Loading..."));
        box.center();
        return box;
    }
    
    public static ListBox createRoleBox(HashMap<Integer, String> roleIdsToNames)
    {
        ListBox box = new ListBox();
        box.addItem("", "");
        for (int roleid : roleIdsToNames.keySet())
        {
            box.addItem(roleIdsToNames.get(roleid), "" + roleid);
        }
        return box;
    }
    
    private static DateTimeFormat dateFormat;
    
    public static String format(Date date)
    {
        return dateFormat.format(date);
    }
    
    private static DateTimeFormat dateOnlyFormat;
    
    public static String formatDate(Date date)
    {
        return dateOnlyFormat.format(date);
    }
    
    public static Date parseDate(String string)
    {
        return dateOnlyFormat.parse(string);
    }
    
    public static void setCellTitle(FlexTable table, int row, int column,
            String title)
    {
        DOM.setElementAttribute(table.getFlexCellFormatter().getElement(row,
                column), "title", title);
    }
    
    public static native Document getResponseXml(JavaScriptObject request) /*-{
        return request.responseXML;
    }-*/;
    
    public static native JavaScriptObject getXMLHttpRequest(Request request) /*-{
        return request.@com.google.gwt.http.client.Request::xmlHttpRequest;
    }-*/;
    
    public static String getSelectionValue(ListBox box, int index)
    {
        return ((SelectElement) box.getElement().cast()).getOptions().getItem(
                index).getValue();
    }
}
