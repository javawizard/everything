package org.opengroove.g4.common.protocol;

import org.opengroove.g4.common.Packet;
import org.opengroove.g4.common.protocol.doc.ClientToServer;
import org.opengroove.g4.common.user.Userid;

@ClientToServer
public class LoginPacket extends Packet
{
    private Userid userid;
    private String password;
    public Userid getUserid()
    {
        return userid;
    }
    public String getPassword()
    {
        return password;
    }
    public void setUserid(Userid userid)
    {
        this.userid = userid;
    }
    public void setPassword(String password)
    {
        this.password = password;
    }
}
