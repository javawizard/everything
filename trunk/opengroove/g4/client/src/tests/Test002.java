package tests;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

/**
 * A class that tests out offline messaging. It connects as testusername1@localhost
 * and sends a message to testusername2@localhost. Then it disconnects and signs
 * on as testusername2@localhost, waits 5 seconds (printing out any message
 * packets it receives during this time), and then signs off.
 * 
 * @author Alexander Boyd
 * 
 */
public class Test002
{
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
                Message message = (Message) packet;
                System.out.println("New message");
                System.out.println("From:     " + message.getFrom());
                System.out.println("To:       " + message.getTo());
                System.out.println("Type:     " + message.getType());
                System.out.println("Subject:  " + message.getSubject());
                System.out.println("Body:     " + message.getBody());
                System.out.println();
            }
        }, new PacketTypeFilter(Message.class));
        System.out.println("logging in");
        con.login("testusername1", "testpassword", "g4");
        System.out.println("ready");
        Thread.sleep(25000);
        con.disconnect();
    }
}
