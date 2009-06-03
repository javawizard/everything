package tests;

import java.util.Date;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

public class Test003
{
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Throwable
    {
        XMPPConnection con =
            new XMPPConnection(new ConnectionConfiguration("localhost", 5222));
        System.out.println("connecting");
        con.connect();
        System.out.println("logging in");
        con.login("testusername2", "testpassword", "g4");
        Message message = new Message("testusername1@localhost", Message.Type.normal);
        message.setBody("Hello, this is a message sent from Test003 at " + new Date());
        // <%!$org.opengroove.g4$!%>(--:base64:sdjkfldsjklfjkldjfk:--)
    }
    
}
