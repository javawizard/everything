package tests;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smackx.packet.OfflineMessageRequest;

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
        // set this to true to respond acknowledging messages when they are
        // received, by the entire body of the message, false to not
        // acknowledge them
        final boolean ackMessages = false;
        final XMPPConnection con = new XMPPConnection("localhost");
        System.out.println("connecting");
        con.connect();
        con.addPacketListener(new PacketListener()
        {
            
            public void processPacket(Packet packet)
            {
                System.out.println("packet " + packet);
                if (packet instanceof Message)
                {
                    Message message = (Message) packet;
                    System.out.println("New message");
                    
                    System.out.println("Subject:  " + message.getSubject());
                    System.out.println("Body:     " + message.getBody());
                    System.out.println();
                    if (ackMessages)
                    {
                        OfflineMessageRequest omr = new OfflineMessageRequest();
                        OfflineMessageRequest.Item omrItem =
                            new OfflineMessageRequest.Item(message.getSubject());
                        omrItem.setAction("rcontent");
                        omr.addItem(omrItem);
                        con.sendPacket(omr);
                    }
                }
            }
        }, null);
        System.out.println("logging in");
        con.login("testusername1", "testpassword", "g4");
        System.out.println("ready");
        Thread.sleep(100000000);
    }
}
