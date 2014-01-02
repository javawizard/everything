package net.sf.opengroove.realmserver.web.rpc;

import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;

import net.sf.opengroove.common.security.CertificateUtils;
import net.sf.opengroove.common.security.Hash;
import net.sf.opengroove.realmserver.DataStore;
import net.sf.opengroove.realmserver.OpenGrooveRealmServer;
import net.sf.opengroove.realmserver.data.model.User;
import net.sf.opengroove.realmserver.gwt.core.rcp.AuthLink;
import net.sf.opengroove.realmserver.gwt.core.rcp.NotificationException;
import net.sf.opengroove.realmserver.gwt.core.rcp.UserException;
import net.sf.opengroove.realmserver.gwt.core.rcp.model.GUser;
import net.sf.opengroove.realmserver.gwt.core.rcp.model.PKIGeneralInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class AuthLinkImpl extends RemoteServiceServlet
    implements AuthLink
{
    
    @Override
    public void sendUserNotification(String to,
        String subject, String message, String priority,
        int dismissMinutes) throws NotificationException

    {
        OpenGrooveRealmServer.sendUserNotifications(to,
            subject, message, priority, dismissMinutes);
    }
    
    public void createUser(String username,
        String password, String passwordagain)
        throws UserException
    {
        try
        {
            username = username.trim();
            password = password.trim();
            passwordagain = passwordagain.trim();
            if (!password.equals(passwordagain))
            {
                throw new UserException(
                    "Passwords don't match");
            }
            if (password.length() < 5)
            {
                throw new UserException(
                    "Passwords can't be shorter than 5 characters");
            }
            if (DataStore.getUser(username) != null)
            {
                throw new UserException(
                    "A user with that username already exists");
            }
            DataStore.addUser(username,
                Hash.hash(password), false);
        }
        catch (Exception e)
        {
            if (e instanceof UserException)
                throw (UserException) e;
            e.printStackTrace();
            throw new UserException(
                "An error occured while accessing the database",
                e);
        }
    }
    
    public GUser[] getUsers()
    {
        try
        {
            List<User> users = DataStore.listUsers();
            GUser[] gusers = new GUser[users.size()];
            for (int i = 0; i < users.size(); i++)
            {
                gusers[i] = new GUser();
                gusers[i].setUsername(users.get(i)
                    .getUsername());
            }
            return gusers;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public PKIGeneralInfo getPKIGeneralInfo()
    {
        PKIGeneralInfo info = new PKIGeneralInfo();
        X509Certificate endCert = OpenGrooveRealmServer.serverCertificateChain[0];
        if (new Date().after(endCert.getNotAfter()))
            info.setCertValidDate(false);
        else
            info.setCertValidDate(true);
        info.setCertExpiresOn(endCert.getNotAfter()
            .toString());
        info.setCertFingerprint(CertificateUtils
            .fingerprint(endCert));
        boolean isOGSigned = CertificateUtils
            .checkSignatureChainValid(new X509Certificate[] {
                endCert, OpenGrooveRealmServer.ogcaCert });
        info.setHasSignedCert(isOGSigned);
        return info;
    }
}
