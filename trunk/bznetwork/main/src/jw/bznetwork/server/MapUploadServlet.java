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
    
    @Override
    public String doAction(Vector<FileItem> sessionFiles) throws IOException
    {
        System.out.println("Performing map upload action");
        String serveridString = getFormField(sessionFiles, "serverid");
        int serverid = Integer.parseInt(serveridString);
        System.out.println("serverid: " + serverid);
        int groupid = GlobalLinkImpl.getServerGroupId(serverid);
        System.out.println("groupid: " + groupid);
        if (!Perms.server("edit-map", serverid, groupid))
            return "You don't have permission to upload a new map "
                    + "for this server.";
        System.out.println("permissions are in order");
        File mapFile = BZNetworkServer.getMapFile(serverid);
        System.out.println("Map file: " + mapFile);
        InputStream in = null;
        System.out.println("reading file list");
        for (FileItem file : sessionFiles)
        {
            if (!file.isFormField())
            {
                System.out.println("found a correct field");
                in = file.getInputStream();
                break;
            }
        }
        if (in == null)
        {
            System.out.println("no correct upload field found");
            return "You didn't specify a file to upload.";
        }
        System.out.println("copying to on-disk map file");
        FileOutputStream out = new FileOutputStream(mapFile);
        StringUtils.copy(in, out);
        out.flush();
        out.close();
        in.close();
        System.out.println("copied successfully. The new map file has been uploaded.");
        //FIXME: log this as an action
        return "Successfully uploaded.";
    }

    @Override
    public void service(ServletRequest req, ServletResponse res)
            throws ServletException, IOException
    {
        System.out.println("Servicing upload request");
        super.service(req, res);
    }
    
}
