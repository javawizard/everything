package net.sf.opengroove.telnet;

import java.awt.BorderLayout;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.sf.opengroove.realmserver.ProtocolMismatchException;

public class OpenGrooveTelnet
{
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Throwable
    {
        JFrame frame = new JFrame("OpenGroove Telnet");
        frame.setSize(500, 300);
        frame.setLocationRelativeTo(null);
        frame.show();
        JOptionPane
            .showMessageDialog(
                frame,
                "In the file chooser that is about to open, select\n"
                    + "the file that contains your server's security key.");
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter(
            "OpenGroove Server Key", "ogvs"));
        fc.showDialog(null, "Open");
        File file = fc.getSelectedFile();
        if (file == null)
            System.exit(0);
        String keyMerged = readFile(file);
        keyMerged = keyMerged.trim();
        String[] keySplit = keyMerged.split("x");
        if (keySplit.length != 4)
        {
            System.out.println("Invalid key");
            System.exit(0);
        }
        BigInteger publicKey = new BigInteger(keySplit[0],
            16);
        BigInteger modulus = new BigInteger(keySplit[1], 16);
        String serverId = JOptionPane
            .showInputDialog(
                frame,
                "Type the realm server id of the server to connect to. This should be\n"
                    + "in the form server:port");
        if (serverId == null)
        {
            System.exit(0);
        }
        String[] serverIdSplit = serverId.split("\\:");
        String host = serverIdSplit[0];
        int port = Integer.parseInt(serverIdSplit[1]);
        if (!(JOptionPane.showConfirmDialog(frame,
            "Server: " + host + "\nPort: " + port
                + "\n\nAre you sure?") == JOptionPane.YES_OPTION))
            System.exit(0);
        JTextArea top = new JTextArea();
        JTextArea bottom = new JTextArea();
        JSplitPane split = new JSplitPane(
            JSplitPane.VERTICAL_SPLIT, true);
        frame.getContentPane()
            .setLayout(new BorderLayout());
        frame.getContentPane().add(split,
            BorderLayout.CENTER);
        JButton send = new JButton("Send");
        split.setTopComponent(new JScrollPane(top));
        split.setBottomComponent(new JScrollPane(bottom));
        split.setDividerLocation(150);
        split.setResizeWeight(0.5);
        frame.getContentPane()
            .add(send, BorderLayout.SOUTH);
        frame.invalidate();
        frame.validate();
        frame.repaint();
        frame.setSize(500, 301);
        Thread.sleep(100);
        frame.setSize(500, 300);
        top.append("Connecting to server " + serverId
            + "...\n");
        // default: 63745
        Socket socket = new Socket(host, port);
        top
            .append("Negotiating handshake with server...\n");
        OutputStream out = socket.getOutputStream();
        InputStream in = socket.getInputStream();
        out.write("OpenGroove\n".getBytes());
        String s = "";
        for (int i = 0; i < 30; i++)
        {
            int read = in.read();
            s += (char) read;
            if ((read == '\r' || read == '\n') && i != 0)
                break;
            if (i == 29)
                throw new ProtocolMismatchException(
                    "too much initialization data sent by the server");
        }
        s = s.trim();
        if (!s.equalsIgnoreCase("OpenGrooveServer"))
        {
            throw new ProtocolMismatchException(
                "Invalid initial response sent");
        }
        BigInteger randomNumber1
    }
    
    /**
     * reads the file specified in to a string. the file must not be larger than
     * 5 MB.
     * 
     * @param file.
     * @return
     */
    public static String readFile(File file)
    {
        try
        {
            if (file.length() > (5 * 1000 * 1000))
                throw new RuntimeException(
                    "the file is "
                        + file.length()
                        + " bytes. that is too large. it can't be larger than 5000000 bytes.");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            FileInputStream fis = new FileInputStream(file);
            copy(fis, baos);
            fis.close();
            baos.flush();
            baos.close();
            return new String(baos.toByteArray(), "UTF-8");
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public static void writeFile(String string, File file)
    {
        try
        {
            ByteArrayInputStream bais = new ByteArrayInputStream(
                string.getBytes("UTF-8"));
            FileOutputStream fos = new FileOutputStream(
                file);
            copy(bais, fos);
            bais.close();
            fos.flush();
            fos.close();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public static void copy(InputStream in, OutputStream out)
        throws IOException
    {
        byte[] buffer = new byte[8192];
        int amount;
        while ((amount = in.read(buffer)) != -1)
        {
            out.write(buffer, 0, amount);
        }
    }
    
}
