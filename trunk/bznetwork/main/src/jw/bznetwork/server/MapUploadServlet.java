package jw.bznetwork.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import jw.bznetwork.client.Perms;
import jw.bznetwork.server.rpc.GlobalLinkImpl;
import jw.bznetwork.utils.StringUtils;

import org.apache.commons.fileupload.FileItem;

import gwtupload.server.UploadAction;

public class MapUploadServlet extends UploadAction
{
    
    private static final int MAX_MAP_SIZE = 1024 * 1024 * 15;
    
    @Override
    public String doAction(Vector<FileItem> sessionFiles) throws IOException
    {
        String serveridString = getFormField(sessionFiles, "serverid");
        int serverid = Integer.parseInt(serveridString);
        int groupid = GlobalLinkImpl.getServerGroupId(serverid);
        if (!Perms.server("edit-map", serverid, groupid))
            return "You don't have permission to upload a new map "
                    + "for this server.";
        File mapFile = BZNetworkServer.getMapFile(serverid);
        System.out.println("Map file: " + mapFile);
        InputStream in = null;
        for (FileItem file : sessionFiles)
        {
            if (!file.isFormField())
            {
                in = file.getInputStream();
                if (file.getSize() > MAX_MAP_SIZE)
                {
                    return "That map file is too large. Map files cannot be larger than "
                            + MAX_MAP_SIZE
                            + " bytes. change MAX_MAP_SIZE in "
                            + "jw.bznetwork.server.MapUploadServlet and then recompile "
                            + "BZNetwork to raise this limit. This will be a config setting in"
                            + " the future.";
                }
                break;
            }
        }
        if (in == null)
            return "You didn't specify a file to upload.";
        FileOutputStream out = new FileOutputStream(mapFile);
        StringUtils.copy(in, out);
        out.flush();
        out.close();
        in.close();
        return null;
    }

    @Override
    public void service(ServletRequest req, ServletResponse res)
            throws ServletException, IOException
    {
        System.out.println("Servicing upload request");
        super.service(req, res);
    }
    
}
