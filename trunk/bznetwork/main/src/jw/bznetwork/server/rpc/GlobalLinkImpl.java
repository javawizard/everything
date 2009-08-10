package jw.bznetwork.server.rpc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.opengroove.common.utils.StringUtils;

import jw.bznetwork.client.Perms;
import jw.bznetwork.client.ShowMessageException;
import jw.bznetwork.client.Verify;
import jw.bznetwork.client.data.AuthUser;
import jw.bznetwork.client.data.EditAuthenticationModel;
import jw.bznetwork.client.data.EditAuthgroupsModel;
import jw.bznetwork.client.data.EditConfigurationModel;
import jw.bznetwork.client.data.EditPermissionsModel;
import jw.bznetwork.client.data.GroupModel;
import jw.bznetwork.client.data.GroupedServer;
import jw.bznetwork.client.data.ServerListModel;
import jw.bznetwork.client.data.ServerModel;
import jw.bznetwork.client.data.UserSession;
import jw.bznetwork.client.data.ServerModel.LiveState;
import jw.bznetwork.client.data.model.Authgroup;
import jw.bznetwork.client.data.model.Banfile;
import jw.bznetwork.client.data.model.Configuration;
import jw.bznetwork.client.data.model.EditablePermission;
import jw.bznetwork.client.data.model.Group;
import jw.bznetwork.client.data.model.Permission;
import jw.bznetwork.client.data.model.Role;
import jw.bznetwork.client.data.model.Server;
import jw.bznetwork.client.live.LivePlayer;
import jw.bznetwork.client.rpc.GlobalLink;
import jw.bznetwork.server.BZNetworkServer;
import jw.bznetwork.server.data.DataStore;
import jw.bznetwork.server.live.LiveServer;

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
    }
    
    @Override
    public void deleteRole(int id)
    {
        Verify.global("manage-roles");
        DataStore.deleteRole(id);
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
        role.setName(newName);
        DataStore.updateRole(role);
    }
    
    @Override
    public void addPermission(int roleid, String permission, int target)
            throws ShowMessageException
    {
        Verify.global("manage-roles");
        if (!Perms.isPermissionValid(permission))
            throw new RuntimeException("Invalid permission");
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
    }
    
    @Override
    public void addAuthgroup(String name, int roleid)
            throws ShowMessageException
    {
        Verify.global("manage-callsign-auth");
        Authgroup authgroup = new Authgroup();
        authgroup.setName(name.trim());
        authgroup.setRole(roleid);
        if (DataStore.getAuthgroupByName(name.trim()) != null)
        {
            throw new ShowMessageException(
                    "An authgroup for that group already exists.");
        }
        DataStore.addAuthgroup(authgroup);
    }
    
    @Override
    public void deleteAuthgroup(String name)
    {
        Verify.global("manage-callsign-auth");
        DataStore.deleteAuthgroup(name);
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
        model.setConfiguration(DataStore.getConfiguration());
        model.setEcDisabled(getEcDisableFile().exists());
        model.setEcDisableFile(getEcDisableFile().getAbsolutePath());
        return model;
    }
    
    @Override
    public void updateConfiguration(Configuration config)
    {
        Verify.global("edit-configuration");
        boolean ecDisabled = getEcDisableFile().exists();
        if (ecDisabled)
        {
            /*
             * If we're not allowed to change the executable, then set it to
             * what it currently is in the db
             */
            config.setExecutable(DataStore.getConfiguration().getExecutable());
        }
        DataStore.updateConfiguration(config);
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
            us.setIp((String) s.getAttribute("ip-address"));
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
            session.invalidate();
    }
    
    @Override
    public void addBanfile(String name)
    {
        Verify.global("manage-banfiles");
        Banfile banfile = new Banfile();
        banfile.setBanfileid(DataStore.createId());
        banfile.setName(name);
        DataStore.addBanfile(banfile);
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
                serverList.add(serverModel);
            }
            groupModel.setServers(serverList.toArray(new ServerModel[0]));
            groupList.add(groupModel);
        }
        model.setGroups(groupList.toArray(new GroupModel[0]));
        return model;
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
    }
    
    @Override
    public void setGroupBanfile(int group, int banfile)
    {
        Verify.group("edit-group-banfile", group);
        Group groupObject = DataStore.getGroupById(group);
        groupObject.setBanfile(banfile);
        DataStore.updateGroup(groupObject);
    }
    
    @Override
    public void addServer(String name, int group)
    {
        Verify.group("create-server", group);
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
    }
    
    @Override
    public void setServerBanfile(int server, int banfile)
    {
        Server serverObject = DataStore.getServerById(server);
        int groupid = (serverObject == null ? -1 : serverObject.getGroupid());
        Verify.server("edit-server-banfile", server, groupid);
        serverObject.setBanfile(banfile);
        DataStore.updateServer(serverObject);
    }
    
    @Override
    public void renameGroup(int group, String newName)
    {
        Verify.group("rename-group", group);
        Group groupObject = DataStore.getGroupById(group);
        groupObject.setName(newName);
        DataStore.updateGroup(groupObject);
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
        serverObject.setName(newName);
        DataStore.updateServer(serverObject);
    }
    
    @Override
    public void updateServer(Server server)
    {
        Verify.server("edit-server-settings", server.getServerid(),
                getServerGroupId(server.getServerid()));
        Server dbServer = DataStore.getServerById(server.getServerid());
        dbServer.setPort(server.getPort());
        dbServer.setListed(server.isListed());
        if (Perms.server("inherit-parent-groupdb", server.getServerid(),
                getServerGroupId(server.getServerid())))
            dbServer.setInheritgroupdb(server.isInheritgroupdb());
        dbServer.setNotes(server.getNotes());
        DataStore.updateServer(dbServer);
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
                    + "# -port\n"
                    + "# -world\n"
                    + "# -conf\n"
                    + "# -groupdb\n"
                    + "# -banfile\n"
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
        StringUtils.writeFile(config, configFile);
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
        StringUtils.writeFile(groupdb, BZNetworkServer.getGroupdbFile(groupid));
    }
    
    @Override
    public void saveServerGroupdb(int serverid, String groupdb)
    {
        Verify.server("edit-groupdb", serverid, getServerGroupId(serverid));
        StringUtils
                .writeFile(groupdb, BZNetworkServer.getGroupdbFile(serverid));
    }
    
    @Override
    public void killServer(int serverid)
    {
        Verify
                .server("start-stop-server", serverid,
                        getServerGroupId(serverid));
        BZNetworkServer.killServer(serverid);
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
        return BZNetworkServer.startServer(serverid, true);
    }
    
    @Override
    public void stopServer(int serverid)
    {
        Verify
                .server("start-stop-server", serverid,
                        getServerGroupId(serverid));
        BZNetworkServer.stopServer(serverid);
    }
}
