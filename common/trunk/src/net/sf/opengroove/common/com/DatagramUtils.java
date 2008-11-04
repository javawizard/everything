package net.sf.opengroove.common.com;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatagramUtils
{
    public byte[] read(InputStream stream)
        throws IOException
    {
        DataInputStream din = new DataInputStream(stream);
        int packetLength = din.readInt();
        byte[] bytes = new byte[packetLength];
        din.readFully(bytes);
        return bytes;
    }
    
    public void write(byte[] bytes, OutputStream out)
        throws IOException
    {
        DataOutputStream dout = new DataOutputStream(out);
        dout.writeInt(bytes.length);
        dout.write(bytes);
        dout.flush();
    }
}
