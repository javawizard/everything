package jw.bznetwork.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jw.bznetwork.client.Perms;
import jw.bznetwork.client.data.UploadStatus;
import jw.bznetwork.server.rpc.GlobalLinkImpl;
import jw.bznetwork.utils.StringUtils;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import gwtupload.server.UploadAction;

public class MapUploadServlet extends HttpServlet
{
    
    private static final int MAX_MAP_SIZE = 1024 * 1024 * 15;
    
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException
    {
        UploadStatus status = new UploadStatus();
        HttpSession session = req.getSession();
        ServletFileUpload fileUpload = new ServletFileUpload(
                new DiskFileItemFactory());
        session.setAttribute("map-upload-status", status);
        fileUpload.setSizeMax(MAX_MAP_SIZE);
        fileUpload.setProgressListener(new Prw)
    }
    
}
