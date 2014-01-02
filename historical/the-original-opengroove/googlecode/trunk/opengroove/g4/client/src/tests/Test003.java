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
        con.addPacketListener(new PacketListener()
        {
            
            public void processPacket(Packet packet)
            {
                System.out.println("packet " + packet);
            }
        }, null);
        System.out.println("logging in");
        con.login("testusername2", "testpassword", "g4");
        for (int i = 0; i < 10000; i++)
        {
            if ((i % 500) == 0)
                System.out.println("Message " + (i + 1));
            Message message =
                new Message("testusername1@localhost", Message.Type.normal);
            message.setBody("This is test message " + (i + 1) + ".");
            con.sendPacket(message);
        }
        Thread.sleep(3000);
        con.disconnect();
    }
    
}
