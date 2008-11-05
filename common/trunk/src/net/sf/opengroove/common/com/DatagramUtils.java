package net.sf.opengroove.common.com;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatagramUtils
{
    public static byte[] read(InputStream stream,
        int maxLength) throws IOException
    {
        DataInputStream din = new DataInputStream(stream);
        int packetLength = din.readInt();
        if (packetLength > maxLength)
            throw new IOException(
                "A packet of size "
                    + packetLength
                    + " was received, but that is larger than the max packet size "
                    + maxLength);
        byte[] bytes = new byte[packetLength];
        din.readFully(bytes);
        return bytes;
    }
    
    public static void write(byte[] bytes, OutputStream out)
        throws IOException
    {
        DataOutputStream dout = new DataOutputStream(out);
        dout.writeInt(bytes.length);
        dout.write(bytes);
        dout.flush();
    }
}
