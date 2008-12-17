package net.sf.opengroove.client.workspace;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import net.sf.opengroove.client.OpenGroove;
import net.sf.opengroove.client.com.OldCommunicator;
import net.sf.opengroove.client.com.MessageSink;
import net.sf.opengroove.client.com.OldStatusListener;
import net.sf.opengroove.client.notification.NotificationAdapter;
import net.sf.opengroove.client.oldplugins.Plugin;
import net.sf.opengroove.client.oldplugins.PluginManager;
import net.sf.opengroove.client.storage.Storage;
import net.sf.opengroove.client.ui.ReceiveWorkspaceInviteFrame;

import base64.Base64Coder;

public class WorkspaceManager
{
    /**
     * Enum for status of a user, online (HERE) or offline (GONE)
     * 
     * @author Alexander Boyd
     * 
     */
    public enum UserStatus
    {
        HERE
        {
            public String getStatusString()
            {
                return "online";
            }
        },
        GONE
        {
            public String getStatusString()
            {
                return "offline";
            }
        };
        
        public abstract String getStatusString();
    }
    
    private static OldCommunicator communicator;
    
    private static long lastInviteTime = 0;
    
    /**
     * reloads the workspaces from Storage, loading only those that have not
     * been loaded. it does not update any info from the file system. this
     * method indirectly calls initialize() on each of the Workspace objects
     * that are loaded.
     * 
     */
    public static synchronized void reloadWorkspaces()
    {
        WorkspaceWrapper[] fsWorkspaces = Storage
            .listWorkspaces();// fs stands
        // for File
        // System,
        // because
        // this is
        // the list
        // of
        // workspaces
        // that are on the file system
        for (WorkspaceWrapper fsw : fsWorkspaces)
        {
            if (!workspaces.contains(fsw))
            {
                try
                {
                    loadWorkspace(fsw);
                }
                catch (Exception e)
                {
                    System.err.println();
                    e.printStackTrace();
                }
            }
        }
    }
    
    private static void loadWorkspace(WorkspaceWrapper fsw)
    {
        Plugin<Workspace> workspacePlugin = PluginManager
            .getById(fsw.getTypeId());
        if (workspacePlugin == null)
            throw new NullPointerException(
                "the plugin for workspace "
                    + fsw.getId()
                    + " isn't present on the local computer");
        Workspace workspace = workspacePlugin.create();
        fsw.setWorkspace(workspace);
        workspace.setPluginMetadata(workspacePlugin
            .getMetadata());
        workspace.setCommunicator(communicator);
        workspace.setWrapper(fsw);
        try
        {
            workspace.initialize();
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
        workspaces.add(fsw);
    }
    
    private static ArrayList<WorkspaceWrapper> workspaces = new ArrayList<WorkspaceWrapper>();
    
    private static long lastForcedReload = 0;
    
    /**
     * initializes this WorkspaceManager. this should be called ONLY ONCE per VM
     * invocation. this method DOES NOT call reloadWorkspaces when it is done,
     * so you will most likely want to call reloadWorkspaces after you call this
     * method.
     * 
     * the communicator is used for communication between workspaces.
     * 
     * in the future, you will be able to call this method if the user logs out
     * and a new user logs in, and it will clear the manager and then
     * re-initialize.
     * 
     * WorkspaceManager will process incoming messages from this communicator
     * that start with wsi| and dispatch them accordingly. if they start with
     * wsi|w|WORKSPACEID|imessage then it will assume that it is a message from
     * one workspace to another. it will dispatch the message to the workspace
     * to which it is targeted, if the user specified is on the allowed user
     * list. the format of a message for a workspace is
     * wsi|w|WORKSPACEID|imessage|THEMESSAGEGOESHERE
     * 
     * if the message is wsi|w|WORKSPACEID|cstatusupdate and it is from the
     * workspace creator, then the specified workspace will be notified that
     * it's allowed user list has changed. if the message is
     * wsi|w|WORKSPACEID|pstatusupdate and it is from a workspace allowed user,
     * then the specified workspace will be notified that it's participant list
     * has changed.
     * 
     * OpenGroove will send pstatusupdate when a user imports a workspace, and
     * when they delete it. OpenGroove will send pstatusupdate when the creator
     * adds or removes from the allowed user list.
     * 
     * @param communicator
     *            the communicator to use
     */
    public synchronized static void init(
        final OldCommunicator communicator)
    {
        if (WorkspaceManager.communicator != null)
            throw new IllegalStateException(
                "WorkspaceManager has already been initialized");
        WorkspaceManager.communicator = communicator;
        communicator.addSink(new MessageSink()
        {
            
            public void process(String from, String message)
            {
                if (!message.startsWith("wsi|"))// message is not intended for
                    // WorkspaceManager
                    return;
                System.out
                    .println("message received for workspace manager: "
                        + message);
                if (message.startsWith("wsi|w|"))
                {
                    String remainder = message
                        .substring("wsi|w|".length());
                    int pIndex = remainder.indexOf("|");
                    String workspaceId = remainder
                        .substring(0, pIndex);
                    String wsMessage = remainder
                        .substring(pIndex + 1);
                    WorkspaceWrapper workspace = getById(workspaceId);
                    if (workspace == null)
                    {
                        System.err
                            .println("message received for nonexistant workspace");
                        return;
                    }
                    processWorkspaceSpecificMessage(from,
                        workspace, wsMessage);
                }
                else if (message.equals("wsi|reloadusers"))
                {
                    new Thread()
                    {
                        public void run()
                        {
                            if ((lastForcedReload + (1000 * 5)) < System
                                .currentTimeMillis())
                            {
                                lastForcedReload = System
                                    .currentTimeMillis();
                                reloadWorkspaceMembers();
                            }
                        }
                    }.start();
                }
                else if (message
                    .startsWith("wsi|workspaceinvite|"))// invitation
                // to a
                // workspace
                // by
                // another
                // user
                {
                    System.out
                        .println("workspace invitation received");
                    try
                    {
                        if ((System.currentTimeMillis() - 5000) < lastInviteTime)
                            return;
                        System.out
                            .println("last invite time ok");
                        lastInviteTime = System
                            .currentTimeMillis();
                        String wMessage = message
                            .substring("wsi|workspaceinvite|"
                                .length());
                        int pIndex = wMessage.indexOf("|");
                        final String wId = wMessage
                            .substring(0, pIndex);
                        String inviteText = wMessage
                            .substring(pIndex + 1);
                        inviteText = Base64Coder
                            .decodeString(inviteText);
                        final ReceiveWorkspaceInviteFrame frame = new ReceiveWorkspaceInviteFrame();
                        frame.getFromLabel().setText(from);
                        frame.getInviteTextArea().setText(
                            inviteText);
                        frame.getWorkspaceIdLabel()
                            .setText(wId);
                        frame.getRejectButton()
                            .addActionListener(
                                new ActionListener()
                                {
                                    
                                    public void actionPerformed(
                                        ActionEvent e)
                                    {
                                        if (JOptionPane
                                            .showConfirmDialog(
                                                frame,
                                                "Are you sure you want to reject this workspace?",
                                                null,
                                                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                                            frame.dispose();
                                    }
                                });
                        frame.getAcceptButton()
                            .addActionListener(
                                new ActionListener()
                                {
                                    
                                    public void actionPerformed(
                                        ActionEvent e)
                                    {
                                        if (JOptionPane
                                            .showConfirmDialog(
                                                frame,
                                                "Are you sure you want to accept this workspace?",
                                                null,
                                                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                                        {
                                            if (!OpenGroove.ocom
                                                .getCommunicator()
                                                .isActive())
                                            {
                                                OpenGroove.launchbar
                                                    .show();
                                                JOptionPane
                                                    .showMessageDialog(
                                                        OpenGroove.launchbar,
                                                        "You must be connected to the internet to add a workspace");
                                                return;
                                            }
                                            String id = wId;
                                            if (WorkspaceManager
                                                .getById(id) != null)
                                            {
                                                OpenGroove.launchbar
                                                    .show();
                                                JOptionPane
                                                    .showMessageDialog(
                                                        OpenGroove.launchbar,
                                                        "You are already participating in this workspace");
                                                return;
                                            }
                                            frame.dispose();
                                            boolean incorrectId = false;
                                            if (id
                                                .contains("-"))
                                            {
                                                String creator = WorkspaceManager
                                                    .getWorkspaceCreator(id);
                                                String mdString = OpenGroove.ocom
                                                    .getUserMetadata(creator);
                                                if (mdString != null)
                                                {
                                                    Properties md = WorkspaceManager
                                                        .parseMetadata(mdString);
                                                    String mdWorkspaceAllowedUsers = md
                                                        .getProperty("workspace_"
                                                            + id
                                                            + "_users");
                                                    if (mdWorkspaceAllowedUsers != null)
                                                    {
                                                        String[] allowedUsers = mdWorkspaceAllowedUsers
                                                            .split("\\,");
                                                        boolean allowed = false;
                                                        for (String u : allowedUsers)
                                                        {
                                                            if (u
                                                                .equals(OpenGroove.username))
                                                                allowed = true;
                                                        }
                                                        if (!allowed)
                                                        {
                                                            OpenGroove.launchbar
                                                                .show();
                                                            JOptionPane
                                                                .showMessageDialog(
                                                                    OpenGroove.launchbar,
                                                                    "<html>The creator of that workspace, "
                                                                        + creator
                                                                        + ", has <br/> not allowed you to participate in this workspace. Contact<br/>"
                                                                        + "that user, and ask them to add you to the workspace's list<br/>"
                                                                        + "of allowed users.");
                                                            return;
                                                        }
                                                        String mdWorkspaceType = md
                                                            .getProperty("workspace_"
                                                                + id
                                                                + "_type");
                                                        if (mdWorkspaceType != null)
                                                        {
                                                            if (PluginManager
                                                                .getById(mdWorkspaceType) != null)
                                                            {
                                                                WorkspaceWrapper ws = new WorkspaceWrapper();
                                                                ws
                                                                    .setDatastore(new File(
                                                                        Storage
                                                                            .getWorkspaceDataStore(),
                                                                        id
                                                                            + "_dstore_"
                                                                            + System
                                                                                .currentTimeMillis()));
                                                                ws
                                                                    .setId(id);
                                                                ws
                                                                    .setName(OpenGroove.WORKSPACE_DEFAULT_NAME);
                                                                ws
                                                                    .setTypeId(mdWorkspaceType);
                                                                Storage
                                                                    .addOrUpdateWorkspace(ws);
                                                                WorkspaceManager
                                                                    .reloadWorkspaces();
                                                                WorkspaceManager
                                                                    .reloadWorkspaceMembers();
                                                                OpenGroove
                                                                    .reloadLaunchbarWorkspaces();
                                                                for (String u : WorkspaceManager
                                                                    .getById(
                                                                        id)
                                                                    .getAllowedUsers())
                                                                {
                                                                    try
                                                                    {
                                                                        OpenGroove.ocom
                                                                            .sendMessage(
                                                                                u,
                                                                                "wsi|reloadusers");
                                                                    }
                                                                    catch (Exception e2)
                                                                    {
                                                                        e2
                                                                            .printStackTrace();
                                                                    }
                                                                }
                                                                OpenGroove.launchbar
                                                                    .show();
                                                                JOptionPane
                                                                    .showMessageDialog(
                                                                        OpenGroove.launchbar,
                                                                        "<html>The workspace has been successfully imported. Click<br/>on the configure icon next to it in the launchbar to edit it's settings.<br/><br/>"
                                                                            + "");
                                                                return;
                                                            }
                                                            else
                                                            {
                                                                OpenGroove.launchbar
                                                                    .show();
                                                                JOptionPane
                                                                    .showMessageDialog(
                                                                        OpenGroove.launchbar,
                                                                        "<html>You don't have this workspace's type installed.<br/>It's type is "
                                                                            + mdWorkspaceType
                                                                            + ".<br/>Please install this type, then try again.");
                                                                return;
                                                            }
                                                        }
                                                        else
                                                        {
                                                            incorrectId = true;
                                                        }
                                                    }
                                                    else
                                                    {
                                                        incorrectId = true;
                                                    }
                                                }
                                                else
                                                {
                                                    incorrectId = true;
                                                }
                                            }
                                            else
                                            {
                                                incorrectId = true;
                                            }
                                            if (incorrectId)
                                            {
                                                OpenGroove.launchbar
                                                    .show();
                                                JOptionPane
                                                    .showMessageDialog(
                                                        OpenGroove.launchbar,
                                                        "The ID specified is not a valid ID.");
                                            }
                                            
                                        }
                                    }
                                });
                        frame.setLocationRelativeTo(null);
                        frame.show();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });
        Thread t1 = new Thread(
            "WorkspaceManager user list updater")
        {
            public void run()

            {
                while (true)
                {
                    try
                    {
                        Thread.sleep(120 * 1000);
                        if (communicator.getCommunicator()
                            .isActive())
                        {
                            OpenGroove.updateMetadata();
                            reloadWorkspaceMembers();
                            communicator.reloadAllUsers();
                            communicator
                                .reloadOnlineUsers();
                            communicator
                                .reloadOfflineUsers();
                            communicator
                                .reloadTimeLatency();
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        };
        t1.setDaemon(true);
        t1.start();
        Thread t2 = new Thread(
            "WorkspaceManager user status hash updater")
        {
            public void run()
            {
                while (true)
                {
                    try
                    {
                        Thread.sleep(40 * 1000);
                        if (communicator.getCommunicator()
                            .isActive())
                        {
                            communicator
                                .reloadUserStatusHash();
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        };
        t2.setDaemon(true);
        t2.start();
        communicator.addStatusListener(new OldStatusListener()
        {
            
            public void allUsersUpdated(OldCommunicator c)
            {
                // TODO Auto-generated method stub
                
            }
            
            public void anyUsersUpdated(OldCommunicator c)
            {
                // TODO Auto-generated method stub
                
            }
            
            public void offlineUsersUpdated(OldCommunicator c)
            {
                // TODO Auto-generated method stub
                
            }
            
            public void onlineUsersUpdated(OldCommunicator c)
            {
                // TODO Auto-generated method stub
                
            }
            
            public void userGone(OldCommunicator c, String u)
            {
                notifyUserChanged(u, UserStatus.GONE);
                communicator.reloadUserStatusHash();
                communicator.reloadAllUsers();
                communicator.reloadOnlineUsers();
                communicator.reloadOfflineUsers();
            }
            
            public void userHere(OldCommunicator c, String u)
            {
                notifyUserChanged(u, UserStatus.HERE);
                communicator.reloadUserStatusHash();
                communicator.reloadAllUsers();
                communicator.reloadOnlineUsers();
                communicator.reloadOfflineUsers();
            }
            
            public void userStatusHashChanged(OldCommunicator c)
            {
                communicator.reloadAllUsers();
                communicator.reloadOnlineUsers();
                communicator.reloadOfflineUsers();
            }
            
            public void userStatusHashUpdated(OldCommunicator c)
            {
                // TODO Auto-generated method stub
                
            }
        });
        try
        {
            communicator.reloadAllUsers();
            communicator.reloadOnlineUsers();
            communicator.reloadOfflineUsers();
            communicator.reloadUserStatusHash();
            communicator.reloadTimeLatency();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    protected static void notifyUserChanged(String u,
        UserStatus status)
    {
        boolean isKnownUser = false;
        for (WorkspaceWrapper w : new ArrayList<WorkspaceWrapper>(
            workspaces))
        {
            try
            {
                if (w.getAllowedUsers().contains(u))
                {
                    isKnownUser = true;
                    w.getWorkspace().userStatusChanged();
                }
            }
            catch (Exception ex1)
            {
                ex1.printStackTrace();
            }
        }
        if (isKnownUser)
        {
            OpenGroove.notificationFrame.addNotification(
                new NotificationAdapter(
                    new JLabel("" + u + " is now "
                        + status.getStatusString()), false,
                    true), true);
        }
    }
    
    public static WorkspaceWrapper getById(
        String workspaceId)
    {
        for (WorkspaceWrapper w : workspaces)
        {
            if (w.getId().equals(workspaceId))
                return w;
        }
        return null;
    }
    
    protected static void processWorkspaceSpecificMessage(
        String from, WorkspaceWrapper workspace,
        String wsMessage)
    {
        if (wsMessage.startsWith("imessage|"))
        {
            if (validateFromParticipant(from, workspace))
            {
                String implMessage = wsMessage
                    .substring(wsMessage.indexOf("|") + 1);
                if (implMessage.length() > 0)
                    workspace.getWorkspace()
                        .receiveMessage(from, implMessage);
            }
            else
            {
                System.err
                    .println("imessage received from "
                        + from
                        + " but that user is not on the workspace TODO: add logging back to server, if this occurs to much then ban the user for some time");
            }
        }
    }
    
    private static boolean validateFromParticipant(
        String from, WorkspaceWrapper workspace)
    {
        return workspace.getAllowedUsers().contains(from);
    }
    
    public static String getWorkspaceCreator(
        String workspaceId)
    {
        // workspace ids always start with the username that created them and
        // then a hyphen.
        return workspaceId.substring(0, workspaceId
            .indexOf("-"));
    }
    
    public static void reloadWorkspaceMembers()
    {
        HashMap<String, Properties> mdCache = new HashMap<String, Properties>();
        for (WorkspaceWrapper ww : workspaces)
        {
            synchronized (ww)
            {
                try
                {
                    String creator = getWorkspaceCreator(ww
                        .getId());
                    Properties creatorMetadata = mdCache
                        .get(creator);
                    if (creatorMetadata == null)
                    {
                        String creatorMd = communicator
                            .getUserMetadata(creator);
                        creatorMetadata = parseMetadata(creatorMd);
                        mdCache.put(creator,
                            creatorMetadata);
                    }
                    String workspaceAllowedMembersString = creatorMetadata
                        .getProperty("workspace_"
                            + ww.getId() + "_users");
                    String[] workspaceAllowedMembers = null;
                    if (!ww.isMine())
                    {
                        workspaceAllowedMembers = workspaceAllowedMembersString
                            .split("\\,");
                        ww
                            .getAllowedUsers()
                            .addAll(
                                Arrays
                                    .asList(workspaceAllowedMembers));
                        ww
                            .getAllowedUsers()
                            .retainAll(
                                Arrays
                                    .asList(workspaceAllowedMembers));
                    }
                    else
                    {
                        workspaceAllowedMembers = ww
                            .getAllowedUsers().toArray(
                                new String[0]);
                    }
                    ArrayList<String> participants = new ArrayList<String>();
                    participants.add(creator);
                    for (String u : workspaceAllowedMembers)
                    {
                        try
                        {
                            Properties userMetadata = mdCache
                                .get(u);
                            if (userMetadata == null)
                            {
                                String userMd = communicator
                                    .getUserMetadata(u);
                                userMetadata = parseMetadata(userMd);
                                mdCache
                                    .put(u, userMetadata);
                            }
                            String participantWorkspacesString = userMetadata
                                .getProperty("workspaces_participant");
                            String[] participantWorkspaces = participantWorkspacesString
                                .split("\\,");
                            if (participantWorkspacesString
                                .trim().equals(""))
                                participantWorkspaces = new String[0];
                            for (String participantWorkspace : participantWorkspaces)
                            {
                                if (participantWorkspace
                                    .equals(ww.getId()))// this
                                // user
                                // is a
                                // participant
                                {
                                    if (participants
                                        .indexOf(u) == -1)
                                    {
                                        participants.add(u);
                                    }
                                }
                            }
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                    ww.getParticipants().addAll(
                        participants);
                    ww.getParticipants().retainAll(
                        participants);
                    ww.getWorkspace().save();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            Storage.addOrUpdateWorkspace(ww);
        }
    }
    
    public static Properties parseMetadata(
        String userMetadata)
    {
        String[] items = userMetadata.split("\\|");
        Properties p = new Properties();
        for (String item : items)
        {
            int pxIndex = item.indexOf("=");
            p.setProperty(item.substring(0, pxIndex), item
                .substring(pxIndex + 1));
        }
        return p;
    }
    
    public static String generateMetadata(
        Properties properties)
    {
        StringBuilder builder = new StringBuilder();
        boolean isFirst = true;
        for (Map.Entry entry : properties.entrySet())
        {
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            if (!isFirst)
                builder.append("|");
            isFirst = false;
            builder.append(key + "=" + value);
        }
        return builder.toString();
    }
    
    public static WorkspaceWrapper[] getAll()
    {
        return workspaces.toArray(new WorkspaceWrapper[0]);
    }
    
    public static synchronized void removeWorkspace(
        WorkspaceWrapper w)
    {
        workspaces.remove(w);
    }
    
}
