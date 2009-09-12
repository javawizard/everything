package jw.bznetwork.server.rpc;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;

import net.sf.opengroove.common.utils.StringUtils;

import jw.bznetwork.client.Constants;
import jw.bznetwork.client.Perms;
import jw.bznetwork.client.SettingType;
import jw.bznetwork.client.Settings;
import jw.bznetwork.client.ShowMessageException;
import jw.bznetwork.client.Verify;
import jw.bznetwork.client.data.ActionLogModel;
import jw.bznetwork.client.data.AuthUser;
import jw.bznetwork.client.data.EditAuthenticationModel;
import jw.bznetwork.client.data.EditAuthgroupsModel;
import jw.bznetwork.client.data.EditCallsignsModel;
import jw.bznetwork.client.data.EditConfigurationModel;
import jw.bznetwork.client.data.EditPermissionsModel;
import jw.bznetwork.client.data.EditTriggersModel;
import jw.bznetwork.client.data.GroupModel;
import jw.bznetwork.client.data.GroupedServer;
import jw.bznetwork.client.data.LogSearchModel;
import jw.bznetwork.client.data.ServerListModel;
import jw.bznetwork.client.data.ServerModel;
import jw.bznetwork.client.data.UserSession;
import jw.bznetwork.client.data.ServerModel.LiveState;
import jw.bznetwork.client.data.model.Action;
import jw.bznetwork.client.data.model.ActionRequest;
import jw.bznetwork.client.data.model.Authgroup;
import jw.bznetwork.client.data.model.Banfile;
import jw.bznetwork.client.data.model.Callsign;
import jw.bznetwork.client.data.model.Configuration;
import jw.bznetwork.client.data.model.EditablePermission;
import jw.bznetwork.client.data.model.EmailGroup;
import jw.bznetwork.client.data.model.Group;
import jw.bznetwork.client.data.model.IrcBot;
import jw.bznetwork.client.data.model.Permission;
import jw.bznetwork.client.data.model.Role;
import jw.bznetwork.client.data.model.Server;
import jw.bznetwork.client.data.model.Trigger;
import jw.bznetwork.client.data.model.UserPair;
import jw.bznetwork.client.live.LivePlayer;
import jw.bznetwork.client.live.LivePlayer.GameType;
import jw.bznetwork.client.live.LivePlayer.TeamType;
import jw.bznetwork.client.rpc.GlobalLink;
import jw.bznetwork.server.BZNetworkServer;
import jw.bznetwork.server.RequestTrackerFilter;
import jw.bznetwork.server.data.DataStore;
import jw.bznetwork.server.live.LiveServer;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * TODO: consider making the methods synchronized, to avoid a few concurrency
 * issues that are present
 * 
 * @author Alexander Boyd
 * 
 */
public class GlobalLinkImpl extends RemoteServiceServlet implements GlobalLink
{
    
    @Override
    public void service(ServletRequest req, ServletResponse res)
            throws ServletException, IOException
    {
        Formatter f;
        HttpServletRequest hReq = (HttpServletRequest) req;
        if (hReq.getSession(false) == null
                || hReq.getSession().getAttribute("user") == null)
        {
            ((HttpServletResponse) res).sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "You are not logged in.");
            return;
        }
        super.service(req, res);
    }
    
    @Override
    public void addRole(String name)
    {
        Verify.global("manage-roles");
        Role role = new Role();
        role.setRoleid(DataStore.createId());
        role.setName(name);
        DataStore.addRole(role);
        action("add-role", "Role id: " + role.getRoleid() + "\nRole name: "
                + name);
    }
    
    @Override
    public void deleteRole(int id)
    {
        Verify.global("manage-roles");
        Role role = DataStore.getRoleById(id);
        DataStore.deleteRole(id);
        action("delete-role", "Role id: " + id + "\nRole name: "
                + (role != null ? role.getName() : ""));
    }
    
    @Override
    public EditPermissionsModel getPermissionsForRole(int roleid)
    {
        Verify.global("manage-roles");
        Permission[] perms = DataStore.getPermissionsByRole(roleid);
        EditablePermission[] result = new EditablePermission[perms.length];
        for (int i = 0; i < perms.length; i++)
        {
            result[i] = new EditablePermission();
            result[i].setGroup(perms[i].getGroup());
            result[i].setRoleid(perms[i].getRoleid());
            result[i].setPermission(perms[i].getPermission());
            result[i].setTarget(perms[i].getTarget());
            result[i].setBanfile(perms[i].getBanfile());
            if (perms[i].getTarget() == -1)
            {
                /*
                 * Global permission
                 */
                result[i].setGroupName(null);
                result[i].setServerName(null);
            }
            else if (perms[i].getBanfile() != null)
            {
                /*
                 * Banfile permission
                 */
                Banfile banfile = DataStore.getBanfileById(perms[i]
                        .getBanfile());
                if (banfile == null)
                    result[i].setBanfileName("_missing_"
                            + perms[i].getBanfile());
                else
                    result[i].setBanfileName(banfile.getName());
            }
            else if (perms[i].getGroup() == null)
            {
                /*
                 * Group permission, since the parent group is null
                 */
                Group group = DataStore.getGroupById(perms[i].getTarget());
                if (group == null)
                    result[i].setGroupName("_missing_" + perms[i].getTarget());
                else
                    result[i].setGroupName(group.getName());
                result[i].setServerName(null);
                
            }
            else
            {
                /*
                 * Server permission, since parent group is not null
                 */
                Server server = DataStore.getServerById(perms[i].getTarget());
                if (server == null)
                    result[i].setServerName("_missing_" + perms[i].getTarget());
                else
                    result[i].setServerName(server.getName());
                Group group = DataStore.getGroupById(perms[i].getGroup());
                if (group == null)
                    result[i].setGroupName("_missing_" + perms[i].getGroup());
                else
                    result[i].setGroupName(group.getName());
            }
        }
        EditPermissionsModel model = new EditPermissionsModel();
        model.setPermissions(result);
        Group[] groups = DataStore.listGroups();
        HashMap<Integer, Group> idsToGroups = new HashMap<Integer, Group>();
        for (Group group : groups)
        {
            idsToGroups.put(group.getGroupid(), group);
        }
        Server[] servers = DataStore.listServers();
        GroupedServer[] resultServers = new GroupedServer[servers.length];
        for (int i = 0; i < servers.length; i++)
        {
            GroupedServer r = new GroupedServer();
            resultServers[i] = r;
            r.setServerid(servers[i].getServerid());
            r.setName(servers[i].getName());
            r.setGroupid(servers[i].getGroupid());
            r.setParent(idsToGroups.get(r.getGroupid()));
        }
        model.setGroups(groups);
        model.setServers(resultServers);
        model.setBanfiles(DataStore.listBanfiles());
        return model;
    }
    
    @Override
    public Role[] getRoleList()
    {
        Verify.global("manage-roles");
        return DataStore.listRoles();
    }
    
    @Override
    public void renameRole(int id, String newName)
    {
        Verify.global("manage-roles");
        Role role = DataStore.getRoleById(id);
        String oldName = role.getName();
        role.setName(newName);
        DataStore.updateRole(role);
        action("rename-role", "Role id: " + id + "\nOld name: " + oldName
                + "\nNew name: " + newName);
    }
    
    @Override
    public void addPermission(int roleid, String permission, int target)
            throws ShowMessageException
    {
        Verify.global("manage-roles");
        if (!Perms.isPermissionValid(permission))
            throw new RuntimeException("Invalid permission");
        Role role = DataStore.getRoleById(roleid);
        if (role == null)
            throw new ShowMessageException(
                    "Trying to apply a permission to a nonexistent role");
        /*
         * In the future, we might want to validate that the permission is being
         * applied to the appropriate level. For now, though, it's not that big
         * of a deal, since the client performs this validation, and a client
         * modded not to perform that validation wouldn't cause any problems
         * because the server ends up acting like the permission doesn't even
         * exist anyway.
         */
        Permission p = new Permission();
        p.setPermission(permission);
        p.setRoleid(roleid);
        p.setTarget(target);
        if (DataStore.getPermission(p) == null)
            DataStore.addPermission(p);
        else
            throw new ShowMessageException(
                    "That permission/target pair already exists on this role.");
        action("add-permission", "Role id: " + roleid + "\nRole name: "
                + role.getName() + "\nPermission: " + permission
                + "\nTarget id: " + target);
    }
    
    @Override
    public void deletePermission(int roleid, String permission, int target)
    {
        Verify.global("manage-roles");
        Permission p = new Permission();
        p.setPermission(permission);
        p.setRoleid(roleid);
        p.setTarget(target);
        DataStore.deletePermission(p);
        action("delete-permission", "Role id: " + roleid + "\nPermission: "
                + permission + "\nTarget id: " + target);
    }
    
    @Override
    public void addAuthgroup(String name, int roleid)
            throws ShowMessageException
    {
        Verify.global("manage-callsign-auth");
        Authgroup authgroup = new Authgroup();
        authgroup.setName(name.trim());
        authgroup.setRole(roleid);
        Role role = DataStore.getRoleById(roleid);
        if (DataStore.getAuthgroupByName(name.trim()) != null)
        {
            throw new ShowMessageException(
                    "An authgroup for that group already exists.");
        }
        DataStore.addAuthgroup(authgroup);
        action("add-authgroup", "Authgroup name: " + name.trim()
                + "\nRole id: " + roleid + "\nRole name: "
                + (role != null ? role.getName() : ""));
    }
    
    @Override
    public void deleteAuthgroup(String name)
    {
        Verify.global("manage-callsign-auth");
        DataStore.deleteAuthgroup(name);
        action("delete-authgroup", "Authgroup name: " + name);
    }
    
    @Override
    public EditAuthgroupsModel getEditAuthgroupsModel()
    {
        Verify.global("manage-callsign-auth");
        EditAuthgroupsModel model = new EditAuthgroupsModel();
        model.setAuthgroups(DataStore.listAuthgroups());
        Role[] roles = DataStore.listRoles();
        for (Role role : roles)
        {
            model.getRoleIdsToNames().put(role.getRoleid(), role.getName());
        }
        return model;
    }
    
    // CALLSIGNS
    @Override
    public void addCallsign(String name, int roleid)
            throws ShowMessageException
    {
        Verify.global("manage-callsign-auth");
        Callsign callsign = new Callsign();
        callsign.setCallsign(name.trim());
        callsign.setRole(roleid);
        Role role = DataStore.getRoleById(roleid);
        if (DataStore.getCallsignByName(name.trim()) != null)
        {
            throw new ShowMessageException("That callsign already exists.");
        }
        DataStore.addCallsign(callsign);
        action("add-callsign", "Callsign: " + name + "\nRole id: " + roleid
                + "\nRole name: " + (role != null ? role.getName() : ""));
    }
    
    @Override
    public void deleteCallsign(String name)
    {
        Verify.global("manage-callsign-auth");
        DataStore.deleteCallsign(name);
        action("delete-callsign", "Callsign: " + name);
    }
    
    @Override
    public EditCallsignsModel getEditCallsignsModel()
    {
        Verify.global("manage-callsign-auth");
        EditCallsignsModel model = new EditCallsignsModel();
        model.setCallsigns(DataStore.listCallsigns());
        Role[] roles = DataStore.listRoles();
        for (Role role : roles)
        {
            model.getRoleIdsToNames().put(role.getRoleid(), role.getName());
        }
        return model;
    }
    
    // END CALLSIGNS
    @Override
    public EditAuthenticationModel getEditAuthenticationModel()
    {
        Verify.global("manage-auth");
        EditAuthenticationModel model = new EditAuthenticationModel();
        model.setProviders(BZNetworkServer.getAuthProviders());
        model.setEnabledProps(new HashMap<String, String>((Map) BZNetworkServer
                .loadEnabledAuthProps()));
        return model;
    }
    
    @Override
    public void updateAuthentication(HashMap<String, String> enabledProps)
    {
        Verify.global("manage-auth");
        Properties props = new Properties();
        props.putAll(enabledProps);
        BZNetworkServer.saveEnabledAuthProps(props);
        StringBuffer details = new StringBuffer();
        for (Entry<String, String> entry : enabledProps.entrySet())
        {
            details.append(entry.getKey() + ": " + entry.getValue() + "\n");
        }
        if (details.toString().endsWith("\n"))
        {
            details = new StringBuffer(details.substring(0,
                    details.length() - 1));
        }
        action("update-authentication", details.toString());
    }
    
    @Override
    public void disableEc()
    {
        Verify.global("edit-configuration");
        File file = getEcDisableFile();
        if (!file.exists())
        {
            try
            {
                file.createNewFile();
                action("disable-executable", "");
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
    }
    
    private File getEcDisableFile()
    {
        return new File(BZNetworkServer.getServletContext().getRealPath(
                "/WEB-INF/disable-ec"));
    }
    
    @Override
    public EditConfigurationModel getEditConfigurationModel()
    {
        Verify.global("edit-configuration");
        EditConfigurationModel model = new EditConfigurationModel();
        model.setConfiguration(Configuration.loadFromDatabase());
        model.setEcDisabled(getEcDisableFile().exists());
        model.setEcDisableFile(getEcDisableFile().getAbsolutePath());
        return model;
    }
    
    @Override
    public void updateConfiguration(Configuration config)
    {
        Verify.global("edit-configuration");
        boolean ecDisabled = getEcDisableFile().exists();
        Configuration oldConfig = Configuration.loadFromDatabase();
        for (Settings s : config.getSettings().keySet())
        {
            String value = config.getSettings().get(s);
            if (ecDisabled)
            {
                if (s.getType() == SettingType.sensitive)
                {
                    continue;
                }
            }
            s.setString(value);
        }
        String details = "";
        for (Settings s : Settings.values())
        {
            if (!config.getSettings().get(s).equals(
                    oldConfig.getSettings().get(s)))
                details += s.name() + ": " + config.getSettings().get(s) + "\n";
        }
        if (details.endsWith("\n"))
            details = details.substring(0, details.length() - 1);
        if (details.equals(""))
            details = "No changes";
        action("update-configuration", details);
    }
    
    @Override
    public UserSession[] getUserSessions()
    {
        Verify.global("view-sessions");
        HttpSession[] sessions = BZNetworkServer.getSessionList().values()
                .toArray(new HttpSession[0]);
        UserSession[] userSessions = new UserSession[sessions.length];
        for (int i = 0; i < sessions.length; i++)
        {
            UserSession us = new UserSession();
            HttpSession s = sessions[i];
            us.setId(s.getId());
            us.setUser((AuthUser) s.getAttribute("user"));
            us.setIp((String) s.getAttribute("stat-ip-address"));
            Long lastAccessTime = (Long) s
                    .getAttribute("stat-last-access-time");
            if (lastAccessTime == null)
                lastAccessTime = 0l;
            us.setLastAccessTime(lastAccessTime);
            Long loggedInTime = (Long) s.getAttribute("stat-logged-in");
            if (loggedInTime == null)
                loggedInTime = 0l;
            us.setLoggedIn(loggedInTime);
            us.setUserAgent((String) s.getAttribute("stat-user-agent"));
            userSessions[i] = us;
        }
        return userSessions;
    }
    
    @Override
    public void invalidateUserSession(String id)
    {
        Verify.global("view-sessions");
        HttpSession session = BZNetworkServer.getSessionList().get(id);
        if (session != null)
        {
            AuthUser user = (AuthUser) session.getAttribute("user");
            String details = "";
            if (user != null)
                details += "User: " + user.getProvider() + ":"
                        + user.getUsername();
            else
                details += "Not logged in";
            action("invalidate-user-session", details);
            session.invalidate();
        }
    }
    
    @Override
    public void addBanfile(String name)
    {
        Verify.global("manage-banfiles");
        Banfile banfile = new Banfile();
        banfile.setBanfileid(DataStore.createId());
        banfile.setName(name);
        DataStore.addBanfile(banfile);
        action("add-banfile", "Banfile id: " + banfile.getBanfileid()
                + "\nBanfile name: " + banfile.getName());
    }
    
    @Override
    public void deleteBanfile(int id) throws ShowMessageException
    {
        Verify.global("manage-banfiles");
        throw new ShowMessageException(
                "BZNetwork doesn't yet support deleting a banfile. "
                        + "Join irc.freenode.net/#bztraining and ask "
                        + "jcp for more info.");
    }
    
    @Override
    public Banfile[] listBanfiles()
    {
        Verify.global("manage-banfiles");
        return DataStore.listBanfiles();
    }
    
    @Override
    public ServerListModel getServerListModel()
    {
        boolean allTeams = Settings.allteams.getBoolean();
        boolean autoExpand = Settings.expandservers.getBoolean();
        ServerListModel model = new ServerListModel();
        ArrayList<GroupModel> groupList = new ArrayList<GroupModel>();
        Group[] groups = DataStore.listGroups();
        Banfile[] banfiles = DataStore.listBanfiles();
        HashMap<Integer, Banfile> banfileMap = new HashMap<Integer, Banfile>();
        for (Banfile banfile : banfiles)
            banfileMap.put(banfile.getBanfileid(), banfile);
        model.getBanfileMap().putAll(banfileMap);
        model.setBanfiles(banfiles);
        for (Group group : groups)
        {
            if (!Perms.group("view-in-group-list", group.getGroupid()))
                continue;
            GroupModel groupModel = new GroupModel();
            groupModel.setGroupid(group.getGroupid());
            groupModel.setName(group.getName());
            groupModel.setBanfile(group.getBanfile());
            ArrayList<ServerModel> serverList = new ArrayList<ServerModel>();
            Server[] servers = DataStore.listServersByGroup(group.getGroupid());
            for (Server server : servers)
            {
                if (!Perms.server("view-in-server-list", server))
                    continue;
                ServerModel serverModel = new ServerModel();
                serverModel.setBanfile(server.getBanfile());
                serverModel.setDirty(server.isDirty());
                serverModel.setGroupid(server.getGroupid());
                serverModel.setInheritgroupdb(server.isInheritgroupdb());
                serverModel.setListed(server.isListed());
                serverModel.setName(server.getName());
                serverModel.setNotes(server.getNotes());
                serverModel.setPort(server.getPort());
                serverModel.setRunning(server.isRunning());
                serverModel.setServerid(server.getServerid());
                LiveServer liveServer = BZNetworkServer.getLiveServers().get(
                        server.getServerid());
                if (liveServer == null)
                {
                    serverModel.setState(LiveState.STOPPED);
                }
                else
                {
                    if (liveServer.isChangingState())
                    {
                        if (liveServer.isStarting())
                        {
                            serverModel.setState(LiveState.STARTING);
                        }
                        else
                        {
                            serverModel.setState(LiveState.STOPPING);
                        }
                    }
                    else
                    {
                        serverModel.setState(LiveState.LIVE);
                    }
                    serverModel.setPlayers(liveServer.getPlayers().toArray(
                            new LivePlayer[0]));
                    
                }
                buildServerDetails(serverModel, liveServer, allTeams,
                        autoExpand);
                serverList.add(serverModel);
            }
            groupModel.setServers(serverList.toArray(new ServerModel[0]));
            groupList.add(groupModel);
        }
        model.setGroups(groupList.toArray(new GroupModel[0]));
        return model;
    }
    
    /**
     * Builds the details for the specified server.
     * 
     * @param serverModel
     * @param liveServer
     * @param allTeams
     */
    private void buildServerDetails(ServerModel serverModel,
            LiveServer liveServer, boolean allTeams, boolean autoExpand)
    {
        if (liveServer == null)
            return;
        LivePlayer[] players = serverModel.getPlayers();
        if (players == null)
        {
            return;
        }
        if (serverModel.getState() != LiveState.LIVE)
            return;
        /*
         * We have a list of players, and the server is running. Now we'll get
         * the map of team limits. Due to a race condition that I haven't been
         * able to track down, we need to create a new map off of the existing
         * team limits map to prevent a ConcurrentModificationException from
         * being thrown later on.
         */
        Map<TeamType, Integer> limits = new HashMap<TeamType, Integer>(
                liveServer.getTeamLimits());
        /*
         * We'll also use a StringBuffer instead of string concatentation to
         * improve performance.
         */
        StringBuffer details = new StringBuffer();
        /*
         * For rabbit mode servers, we only show the list of rogues (and we
         * count the rabbit as a rogue) and observers, and we show the rogues as
         * orange instead of yellow. For all other modes, we show red, green,
         * blue, purple, rogue, and observer teams. In the future, there should
         * probably be some sort of configuration setting for whether or not
         * teams with a limit of 0 should be shown at all in the details column.
         * 
         * AtomicInteger is mutable, so we'll use it here.
         */
        Map<TeamType, AtomicInteger> counts = new HashMap<TeamType, AtomicInteger>();
        int totalNonObservers = 0;
        for (TeamType type : TeamType.values())
        {
            counts.put(type, new AtomicInteger(0));
        }
        for (LivePlayer player : new ArrayList<LivePlayer>(liveServer
                .getPlayers()))
        {
            try
            {
                counts.get(player.getTeam()).incrementAndGet();
                if (player.getTeam() != TeamType.observer)
                    totalNonObservers += 1;
            }
            catch (Exception e)
            {
                /*
                 * A NullPointerException can occur here if this player joins
                 * right as we're doing this loop, but we'll just ignore it
                 * since it's extremely rare for it to happen, and ignoring it
                 * will just cause them not to be counted towards the total
                 * number of players on their team.
                 */
                e.printStackTrace();
            }
        }
        serverModel.setAutoExpand(totalNonObservers > 0 && autoExpand);
        if (liveServer.getGameType() == GameType.RabbitHunt)
        {
            int hunters = counts.get(TeamType.rogue).get()
                    + counts.get(TeamType.rabbit).get();
            int observers = counts.get(TeamType.observer).get();
            details.append(buildServerPlayerCountString(TeamType.hunters,
                    hunters, limits.get(TeamType.hunters), allTeams));
            details.append(buildServerPlayerCountString(TeamType.observer,
                    observers, limits.get(TeamType.observer), allTeams));
        }
        else if (liveServer.getGameType() == GameType.FreeForAll
                || liveServer.getGameType() == GameType.CaptureTheFlag)
        {
            int reds = counts.get(TeamType.red).get();
            int greens = counts.get(TeamType.green).get();
            int blues = counts.get(TeamType.blue).get();
            int purples = counts.get(TeamType.purple).get();
            int rogues = counts.get(TeamType.rogue).get();
            int observers = counts.get(TeamType.observer).get();
            details.append(buildServerPlayerCountString(TeamType.red, reds,
                    limits.get(TeamType.red), allTeams));
            details.append(buildServerPlayerCountString(TeamType.green, greens,
                    limits.get(TeamType.green), allTeams));
            details.append(buildServerPlayerCountString(TeamType.blue, blues,
                    limits.get(TeamType.blue), allTeams));
            details.append(buildServerPlayerCountString(TeamType.purple,
                    purples, limits.get(TeamType.purple), allTeams));
            details.append(buildServerPlayerCountString(TeamType.rogue, rogues,
                    limits.get(TeamType.rogue), allTeams));
            details.append(buildServerPlayerCountString(TeamType.observer,
                    observers, limits.get(TeamType.observer), allTeams));
        }
        serverModel.setDetailString(details.toString());
    }
    
    /**
     * Builds a stylized string indicating the team counts specified.
     * 
     * @param team
     *            The name of the team
     * @param current
     *            The number of people currently on the team
     * @param total
     *            The maximum number of people allowed on the team
     * @return
     */
    private String buildServerPlayerCountString(TeamType team, int current,
            int total, boolean allTeams)
    {
        /*
         * We use a StringBuffer instead of string concatenation to improve the
         * performance of this method.
         */
        StringBuffer buffer = new StringBuffer();
        if (total == 0 && !allTeams)
            return "";
        // if (current > 0)
        // buffer.append("<span style='font-weight: bold'>");
        buffer.append("<span style='color:#");
        buffer.append(team.light());
        buffer.append("'>");
        buffer.append(current);
        buffer.append("</span><span style='color:#");
        buffer.append(team.medium());
        buffer.append("'>/</span><span style='color:#");
        buffer.append(team.dark());
        buffer.append("'>");
        buffer.append(total);
        buffer.append("</span>");
        // if (current > 0)
        // buffer.append("</span>");
        buffer.append(" ");
        return buffer.toString();
    }
    
    @Override
    public void addGroup(String name)
    {
        Verify.global("create-group");
        Group group = new Group();
        group.setGroupid(DataStore.createId());
        group.setName(name);
        group.setBanfile(-1);
        DataStore.addGroup(group);
        action("add-group", "Group id: " + group.getGroupid()
                + "\nGroup name: " + group.getName());
    }
    
    @Override
    public void setGroupBanfile(int group, int banfile)
    {
        Verify.group("edit-group-banfile", group);
        Group groupObject = DataStore.getGroupById(group);
        groupObject.setBanfile(banfile);
        DataStore.updateGroup(groupObject);
        Banfile banfileObject = DataStore.getBanfileById(banfile);
        action("set-group-banfile", "Group id: " + group + "\nGroup name: "
                + groupObject.getName() + "\nBanfile id: " + banfile
                + "\nBanfile name: "
                + (banfileObject != null ? banfileObject.getName() : ""));
    }
    
    @Override
    public void addServer(String name, int group)
    {
        Verify.group("create-server", group);
        Group groupObject = DataStore.getGroupById(group);
        Server server = new Server();
        server.setBanfile(-1);
        server.setDirty(false);
        server.setGroupid(group);
        server.setInheritgroupdb(false);
        server.setListed(false);
        server.setLoglevel(0);
        server.setName(name);
        server.setNotes("");
        server.setPort(0);
        server.setRunning(false);
        server.setServerid(DataStore.createId());
        DataStore.addServer(server);
        action("add-server", "Server id: " + server.getServerid()
                + "\nServer name: " + name + "\nGroup id: " + group
                + "\nGroup name: " + groupObject.getName());
    }
    
    @Override
    public void setServerBanfile(int server, int banfile)
    {
        Server serverObject = DataStore.getServerById(server);
        int groupid = (serverObject == null ? -100 : serverObject.getGroupid());
        Verify.server("edit-server-banfile", server, groupid);
        serverObject.setBanfile(banfile);
        DataStore.updateServer(serverObject);
        action("set-server-banfile", "Server id: " + server + "\nServer name: "
                + serverObject.getName() + "\nBanfile id: " + banfile);
    }
    
    @Override
    public void renameGroup(int group, String newName)
    {
        Verify.group("rename-group", group);
        Group groupObject = DataStore.getGroupById(group);
        String oldName = groupObject.getName();
        groupObject.setName(newName);
        DataStore.updateGroup(groupObject);
        action("rename-group", "Group id: " + group + "\nOld name: " + oldName
                + "\nNew name: " + newName);
    }
    
    public static int getServerGroupId(int server)
    {
        Server serverObject = DataStore.getServerById(server);
        int groupid = (serverObject == null ? -100 : serverObject.getGroupid());
        return groupid;
    }
    
    @Override
    public void renameServer(int server, String newName)
    {
        Verify.server("edit-server-settings", server, getServerGroupId(server));
        Server serverObject = DataStore.getServerById(server);
        String oldName = serverObject.getName();
        serverObject.setName(newName);
        DataStore.updateServer(serverObject);
        action("rename-server", "Server id: " + server + "\nOld name: "
                + oldName + "\nNew name: " + newName);
    }
    
    @Override
    public void updateServer(Server server)
    {
        Verify.server("edit-server-settings", server.getServerid(),
                getServerGroupId(server.getServerid()));
        Server oldServer = DataStore.getServerById(server.getServerid());
        Server dbServer = DataStore.getServerById(server.getServerid());
        dbServer.setPort(server.getPort());
        dbServer.setListed(server.isListed());
        if (Perms.server("inherit-parent-groupdb", server.getServerid(),
                getServerGroupId(server.getServerid())))
            dbServer.setInheritgroupdb(server.isInheritgroupdb());
        dbServer.setNotes(server.getNotes());
        DataStore.updateServer(dbServer);
        String details = "";
        if (oldServer.getPort() != dbServer.getPort())
            details += "Port: " + dbServer.getPort() + "\n";
        if (oldServer.isListed() != dbServer.isListed())
            details += "Public: " + dbServer.isListed();
        if (oldServer.isInheritgroupdb() != dbServer.isInheritgroupdb())
            details += "Inherit groupdb: " + dbServer.isInheritgroupdb();
        if (!oldServer.getNotes().equals(dbServer.getNotes()))
            details += "Notes: " + dbServer.getNotes();
        if (details.endsWith("\n"))
            details = details.substring(0, details.length() - 1);
        if (details.equals(""))
            details = "No changes";
        action("update-server", details);
    }
    
    @Override
    public String getServerConfig(int serverid)
    {
        Verify.server("edit-server-settings", serverid,
                getServerGroupId(serverid));
        File configFile = BZNetworkServer.getConfigFile(serverid);
        if (!configFile.exists())
            return "# Add your server's configuration and command-line options here. "
                    + "Don't include any of these switches, as they are automatically "
                    + "added when the server is started:\n"
                    + "# -public\n"
                    + "# -p\n"
                    + "# -world\n"
                    + "# -conf\n"
                    + "# -groupdb\n"
                    + "# -banfile\n"
                    + "# -reportfile\n"
                    + "# Additionally, don't load the serverControl plugin, as it will"
                    + " be automatically loaded and configured for you. ";
        return StringUtils.readFile(configFile);
    }
    
    @Override
    public void saveServerConfig(int serverid, String config)
    {
        Verify.server("edit-server-settings", serverid,
                getServerGroupId(serverid));
        File configFile = BZNetworkServer.getConfigFile(serverid);
        StringUtils.writeFile(forceNewline(config), configFile);
        /*
         * TODO: consider adding in a diff of the old config and the new config,
         * if it wouldn't take up too much space (maybe have it as a
         * configuration setting whether or not diffs get saved like this).
         * Possibly find a java diff library, like the one Eclipse uses, to do
         * the actual diffing, or rely on the command-line diff.
         */
        action("edit-server-config", "");
    }
    
    // edit-groupdb and edit-group-groupdb
    
    @Override
    public String getGroupGroupdb(int groupid)
    {
        Verify.group("edit-group-groupdb", groupid);
        File groupdbFile = BZNetworkServer.getGroupdbFile(groupid);
        if (!groupdbFile.exists())
            return "# Add the group's groupdb here. Each server that is set to inherit "
                    + "its parent's groupdb will have this groupdb prepended to it when "
                    + "the server starts.";
        return StringUtils.readFile(groupdbFile);
    }
    
    @Override
    public String getServerGroupdb(int serverid)
    {
        Verify.server("edit-groupdb", serverid, getServerGroupId(serverid));
        File groupdbFile = BZNetworkServer.getGroupdbFile(serverid);
        if (!groupdbFile.exists())
            return "# Add the server's groupdb here. If the server is set to inherit "
                    + "its parent's groupdb, then when the server is run it will "
                    + "use both groupdbs. The parent group's groupdb will come first, "
                    + "so you can reference groups added in it.";
        return StringUtils.readFile(groupdbFile);
    }
    
    @Override
    public void saveGroupGroupdb(int groupid, String groupdb)
    {
        Verify.group("edit-group-groupdb", groupid);
        StringUtils.writeFile(forceNewline(groupdb), BZNetworkServer
                .getGroupdbFile(groupid));
        action("edit-group-groupdb", "");
    }
    
    @Override
    public void saveServerGroupdb(int serverid, String groupdb)
    {
        Verify.server("edit-groupdb", serverid, getServerGroupId(serverid));
        StringUtils.writeFile(forceNewline(groupdb), BZNetworkServer
                .getGroupdbFile(serverid));
        action("edit-server-groupdb", "");
    }
    
    private String forceNewline(String s)
    {
        if (s.endsWith(BZNetworkServer.newline))
            return s;
        return s + BZNetworkServer.newline;
    }
    
    @Override
    public void killServer(int serverid)
    {
        Verify
                .server("start-stop-server", serverid,
                        getServerGroupId(serverid));
        BZNetworkServer.killServer(serverid);
        action("kill-server", "Server id: " + serverid);
        try
        {
            Thread.sleep(500);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
    
    @Override
    public String startServer(int serverid)
    {
        Verify
                .server("start-stop-server", serverid,
                        getServerGroupId(serverid));
        try
        {
            String s = BZNetworkServer.startServer(serverid, true);
            action("start-server", "Server id: " + serverid + "\nResult: " + s);
            return s;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            action("start-server", "Server id: " + serverid
                    + "\nResult: exception " + e.getClass().getName() + ": "
                    + e.getMessage());
            return e.getClass().getName() + ": " + e.getMessage();
        }
    }
    
    @Override
    public void stopServer(int serverid)
    {
        Verify
                .server("start-stop-server", serverid,
                        getServerGroupId(serverid));
        BZNetworkServer.stopServer(serverid);
        action("stop-server", "Server id: " + serverid);
    }
    
    public static void action(String event, String details)
    {
        AuthUser user = (AuthUser) RequestTrackerFilter.getCurrentRequest()
                .getSession().getAttribute("user");
        if (user == null)
            throw new IllegalStateException(
                    "Can't add events to the action log when the "
                            + "user is not logged in.");
        Action action = new Action();
        action.setDetails(details);
        action.setEvent(event);
        action.setProvider(user.getProvider());
        action.setTarget(-1);
        action.setUsername(user.getUsername());
        action.setWhen(new Date());
        BZNetworkServer.logAction(action);
    }
    
    @Override
    public void clearActionLog(String provider, String user)
    {
        Verify.global("clear-action-log");
        AuthUser thisUser = (AuthUser) RequestTrackerFilter.getCurrentRequest()
                .getSession().getAttribute("user");
        // action-log-cleared, clear-action-log
        UserPair pair = new UserPair();
        pair.setProvider(provider);
        pair.setUser(user);
        DataStore.clearActionLog(pair);
        action("clear-action-log", "User: " + provider + ":" + user);
        Action action = new Action();
        action.setDetails("Cleared by: " + thisUser.getProvider() + ":"
                + thisUser.getUsername());
        action.setEvent("action-log-cleared");
        action.setProvider(provider);
        action.setTarget(-1);
        action.setUsername(user);
        action.setWhen(new Date());
        BZNetworkServer.logAction(action);
    }
    
    @Override
    public ActionLogModel getActionLogModel(String event, String provider,
            String user, int offset, int length)
    {
        Verify.global("view-action-log");
        ActionRequest request = new ActionRequest();
        request.setOffset(offset);
        request.setLength(length);
        if (event == null)
            request.setLiteralEvent("event");
        else
            request.setLiteralEvent("'" + StringEscapeUtils.escapeSql(event)
                    + "'");
        if (user == null)
            request.setLiteralUser("username");
        else
            request.setLiteralUser("'" + StringEscapeUtils.escapeSql(user)
                    + "'");
        if (provider == null)
            request.setLiteralProvider("provider");
        else
            request.setLiteralProvider("'"
                    + StringEscapeUtils.escapeSql(provider) + "'");
        ActionLogModel model = new ActionLogModel();
        model.setActions(DataStore.listActionsForSearch(request));
        model.setCount(DataStore.getActionCountForSearch(request));
        model.setEventNames(DataStore.getActionEventNames());
        model.setUsers(DataStore.getActionUserList());
        return model;
    }
    
    @Override
    public LogSearchModel getLogSearchModel()
    {
        LogSearchModel model = new LogSearchModel();
        Server[] servers = DataStore.listServers();
        ArrayList<Server> okServers = new ArrayList<Server>();
        for (Server s : servers)
        {
            if (Perms.server("view-logs", s.getServerid(), getServerGroupId(s
                    .getServerid())))
                okServers.add(s);
        }
        model.setServers(okServers.toArray(new Server[0]));
        model.setEvents(Constants.LOG_EVENTS);
        return model;
    }
    
    @Override
    public void say(ArrayList<Integer> servers, String message)
            throws ShowMessageException
    {
        if (servers.size() < 1)
        {
            Server[] serverArray = DataStore.listServers();
            for (Server s : serverArray)
            {
                servers.add(s.getServerid());
            }
        }
        AuthUser user = (AuthUser) RequestTrackerFilter.getCurrentRequest()
                .getSession().getAttribute("user");
        String username = user.getUsername();
        /*
         * First, we need to filter the servers to those that the user has the
         * say permission on.
         */
        ArrayList<Integer> okServers = new ArrayList<Integer>();
        for (int s : servers)
        {
            if (Perms.server("say", s, getServerGroupId(s)))
                okServers.add(s);
        }
        if (okServers.size() < 1)
            throw new ShowMessageException(
                    "None of the servers you're searching on have granted "
                            + "you permission to say messages on them.");
        /*
         * Now we do the actual saying.
         */
        boolean saidToServer = false;
        for (int s : servers)
        {
            LiveServer server = BZNetworkServer.getLiveServers().get(s);
            if (server == null)
                continue;
            try
            {
                server.sayToFromPlayer("+server", "+all", "[" + username + "] "
                        + message);
                saidToServer = true;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        if (!saidToServer)
            throw new ShowMessageException(
                    "None of the servers you're searching on are running.");
    }
    
    @Override
    public void deleteIrcBot(int botid)
    {
        Verify.global("manage-irc");
        Trigger[] triggers = DataStore.listTriggersByRecipient(botid);
        for (Trigger trigger : triggers)
        {
            DataStore.deleteTrigger(trigger.getTriggerid());
        }
        DataStore.deleteIrcBot(botid);
        BZNetworkServer.notifyIrcBotDeleted(botid);
    }
    
    @Override
    public IrcBot[] listIrcBots()
    {
        Verify.global("manage-irc");
        return DataStore.listIrcBots();
    }
    
    @Override
    public void reconnectIrcBots()
    {
        Verify.global("manage-irc");
        BZNetworkServer.notifyIrcReconnectRequested();
    }
    
    @Override
    public void updateIrcBot(int botid, String nick, String server, int port,
            String password, String channel) throws ShowMessageException
    {
        Verify.global("manage-irc");
        IrcBot bot;
        if (botid == -1)
        {
            bot = new IrcBot();
            bot.setBotid(DataStore.createId());
        }
        else
        {
            bot = DataStore.getIrcBotById(botid);
        }
        bot.setChannel(channel);
        bot.setNick(nick);
        bot.setPassword(password);
        bot.setPort(port);
        bot.setServer(server);
        if (botid == -1)
        {
            DataStore.addIrcBot(bot);
            BZNetworkServer.notifyIrcBotAdded(bot);
        }
        else
        {
            IrcBot oldBot = DataStore.getIrcBotById(botid);
            DataStore.updateIrcBot(bot);
            BZNetworkServer.notifyIrcBotUpdated(oldBot, bot);
        }
    }
    
    @Override
    public void deleteTrigger(int triggerid)
    {
        Verify.global("manage-triggers");
        DataStore.deleteTrigger(triggerid);
    }
    
    @Override
    public EditTriggersModel getEditTriggersModel()
    {
        Verify.global("manage-triggers");
        EditTriggersModel model = new EditTriggersModel();
        Group[] groups = DataStore.listGroups();
        Server[] servers = DataStore.listServers();
        model.getTargets().put(-1, "Global");
        for (Group group : groups)
        {
            model.getTargets().put(group.getGroupid(),
                    "group:" + group.getName());
        }
        for (Server server : servers)
        {
            model.getTargets().put(
                    server.getServerid(),
                    "" + model.getTargets().get(server.getGroupid())
                            + "/server:" + server.getName());
        }
        model.setTriggers(DataStore.listTriggers());
        for (IrcBot bot : DataStore.listIrcBots())
        {
            model.getRecipients().put(bot.getBotid(),
                    "irc:" + bot.getNick() + "@" + bot.getServer());
        }
        for (EmailGroup g : DataStore.listEmailGroups())
        {
            model.getRecipients().put(g.getEmailgroupid(),
                    "email:" + g.getName());
        }
        return model;
    }
    
    @Override
    public void updateTrigger(Trigger trigger)
    {
        Verify.global("manage-triggers");
        boolean isAdding = false;
        if (trigger.getTriggerid() == -1)
        {
            isAdding = true;
            trigger.setTriggerid(DataStore.createId());
        }
        if (isAdding)
            DataStore.addTrigger(trigger);
        else
            DataStore.updateTrigger(trigger);
    }
    
    @Override
    public void deleteEmailGroup(int groupid)
    {
        Verify.global("manage-email-groups");
        Trigger[] triggers = DataStore.listTriggersByRecipient(groupid);
        for (Trigger t : triggers)
        {
            DataStore.deleteTrigger(t.getTriggerid());
        }
        DataStore.deleteEmailGroup(groupid);
    }
    
    @Override
    public EmailGroup[] listEmailGroups()
    {
        Verify.global("manage-email-groups");
        return DataStore.listEmailGroups();
    }
    
    @Override
    public void updateEmailGroup(EmailGroup emailGroup)
    {
        Verify.global("manage-email-groups");
        boolean isAdding = false;
        if (emailGroup.getEmailgroupid() == -1)
        {
            isAdding = true;
            emailGroup.setEmailgroupid(DataStore.createId());
        }
        if (isAdding)
            DataStore.addEmailGroup(emailGroup);
        else
            DataStore.updateEmailGroup(emailGroup);
    }
}
