package jw.bznetwork.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.opengroove.common.utils.StringUtils;

import jw.bznetwork.client.Perms;
import jw.bznetwork.client.data.model.Group;
import jw.bznetwork.client.data.model.Server;
import jw.bznetwork.server.data.DataStore;

public class MapDownloadServlet extends HttpServlet
{
    
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException
    {
        /*
         * Get the path info. The substring(1) removes the leading slash
         * character from the path info.
         */
        String pathInfo = req.getPathInfo().substring(1);
        String[] tokens = pathInfo.split("\\/");
        int serverid = Integer.parseInt(tokens[0]);
        Server server = DataStore.getServerById(serverid);
        if (server == null || !Perms.server("view-in-server-list", server))
        {
            resp
                    .sendError(
                            resp.SC_FORBIDDEN,
                            "Either that server doesn't exist, or you don't "
                                    + "have permission to download the server's map file.");
            return;
        }
        File mapFile = BZNetworkServer.getMapFile(serverid);
        if (!mapFile.exists())
        {
            resp
                    .sendError(
                            resp.SC_NOT_FOUND,
                            "That server doesn't have a map file yet. If you "
                                    + "have appropriate permissions, use the upload link on the "
                                    + "servers page to upload a map for the server.");
            return;
        }
        FileInputStream in = new FileInputStream(mapFile);
        OutputStream out = resp.getOutputStream();
        StringUtils.copy(in,out);
        in.close();
        out.flush();
        out.close();
    }
    
}
