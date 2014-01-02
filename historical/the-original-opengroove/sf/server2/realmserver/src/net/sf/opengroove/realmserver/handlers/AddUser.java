package net.sf.opengroove.realmserver.handlers;

import net.sf.opengroove.realmserver.DataStore;
import net.sf.opengroove.realmserver.web.Handler;
import net.sf.opengroove.realmserver.web.HandlerContext;
import net.sf.opengroove.common.security.Hash;

public class AddUser implements Handler
{
    
    @Override
    public void handle(HandlerContext context)
        throws Exception
    {
        String username = context.getRequest()
            .getParameter("username");
        String password = context.getRequest()
            .getParameter("password");
        String passwordagain = context.getRequest()
            .getParameter("passwordagain");
        if (username == null)
            return;
        username = username.trim();
        password = password.trim();
        passwordagain = passwordagain.trim();
        if (!password.equals(passwordagain))
        {
            context.setMessage("Passwords don't match.");
            return;
        }
        if (password.length() < 5)
        {
            context
                .setMessage("Passwords can't be shorter than 5 characters");
            return;
        }
        if (DataStore.getUser(username) != null)
        {
            context
                .setMessage("A user with that username already exists");
            return;
        }
        DataStore.addUser(username, Hash.hash(password),
            false);
        context.setRedirect("listusers");
    }
}
