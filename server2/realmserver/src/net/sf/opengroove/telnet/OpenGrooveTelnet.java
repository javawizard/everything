package net.sf.opengroove.telnet;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

import DE.knp.MicroCrypt.Aes256;

import net.sf.opengroove.realmserver.ProtocolMismatchException;
import net.sf.opengroove.common.security.Crypto;
import net.sf.opengroove.common.security.Hash;
import net.sf.opengroove.common.security.RSA;

public class OpenGrooveTelnet
{
    public final static SecureRandom random = new SecureRandom();
    
    public static BigInteger rsaPublic;
    
    public static BigInteger rsaMod;
    
    /**
     * @param args
     */
    public static void main(String[] args) throws Throwable
    {
        JFrame frame = new JFrame("OpenGroove Telnet");
        frame.setSize(500, 300);
        frame.setLocationRelativeTo(null);
        frame.show();
        // String serverId = JOptionPane
        // .showInputDialog(
        // frame,
        // "Type the realm server id of the server to connect to. This should
        // be\n"
        // + "in the form server:port", "localhost:63745");
        String serverId = "localhost:63745";
        if (serverId == null)
        {
            System.exit(0);
        }
        String[] serverIdSplit = serverId.split("\\:");
        String host = serverIdSplit[0];
        int port = Integer.parseInt(serverIdSplit[1]);
        final JTextArea top = new JTextArea();
        final JTextArea bottom = new JTextArea();
        JSplitPane split = new JSplitPane(
            JSplitPane.VERTICAL_SPLIT, true);
        frame.getContentPane()
            .setLayout(new BorderLayout());
        frame.getContentPane().add(split,
            BorderLayout.CENTER);
        final JButton send = new JButton("Send");
        send.setEnabled(false);
        final JScrollPane topScroll = new JScrollPane(top);
        JScrollPane bottomScroll = new JScrollPane(bottom);
        split.setTopComponent(topScroll);
        split.setBottomComponent(bottomScroll);
        split.setDividerLocation(150);
        split.setResizeWeight(0.5);
        top.setEditable(false);
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
        final OutputStream out = socket.getOutputStream();
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
                    "too much first initialization data sent by the server");
        }
        s = s.trim();
        if (!s.equalsIgnoreCase("OpenGrooveServer"))
        {
            throw new ProtocolMismatchException(
                "Invalid initial response sent");
        }
        BigInteger aesRandomNumber = new BigInteger(3060,
            random);
        byte[] securityKeyBytes = new byte[32];
        System.arraycopy(aesRandomNumber.toByteArray(), 0,
            securityKeyBytes, 0, 32);
        final Aes256 securityKey = new Aes256(
            securityKeyBytes);
        // System.out.println("using aes key "
        // + Hash.hexcode(securityKeyBytes));
        BigInteger securityKeyEncrypted = RSA.encrypt(
            rsaPublic, rsaMod, aesRandomNumber);
        out
            .write((securityKeyEncrypted.toString(16) + "\n")
                .getBytes());
        BigInteger randomServerCheckInteger = new BigInteger(
            3060, random);
        // System.out.println("randomservercheckinteger "
        // + randomServerCheckInteger.toString(16));
        byte[] randomServerCheckBytes = new byte[16];
        System.arraycopy(randomServerCheckInteger
            .toByteArray(), 0, randomServerCheckBytes, 0,
            16);
        BigInteger serverCheckEncrypted = RSA.encrypt(
            rsaPublic, rsaMod, randomServerCheckInteger);
        // System.out.println("servercheckencrypted "
        // + serverCheckEncrypted.toString(16));
        out
            .write((serverCheckEncrypted.toString(16) + "\n")
                .getBytes());
        out.flush();
        s = "";
        for (int i = 0; i < 1024; i++)
        {
            int read = in.read();
            s += (char) read;
            if ((read == '\r' || read == '\n') && i != 0)
                break;
            if (i == 1023)
                throw new ProtocolMismatchException(
                    "too much second initialization data sent by the server");
        }
        s = s.trim();
        byte[] confirmServerCheckBytes = new byte[16];
        // FIXME: arrayindexoutofboundsexception for small arrays
        securityKey.decrypt(new BigInteger(s, 16)
            .toByteArray(), 0, confirmServerCheckBytes, 0);
        if (!Arrays.equals(randomServerCheckBytes,
            confirmServerCheckBytes))
            throw new RuntimeException(
                "Server failed check bytes with sent "
                    + Hash.hexcode(randomServerCheckBytes)
                    + " and received "
                    + Hash.hexcode(confirmServerCheckBytes)
                    + " and unenc received "
                    + Hash.hexcode(new BigInteger(s, 16)
                        .toByteArray()));
        out.write('c');
        out.flush();
        for (int i = 0; i < 5; i++)
        {
            if (in.read() == 'c')
                break;
            if (i == 4)
                throw new ProtocolMismatchException(
                    "no terminating 'c' at end of handshake");
        }
        byte[] antiReplayMessage = Crypto.dec(securityKey,
            in, 200);
        // System.out.println("received antireply "
        // + Hash.hexcode(antiReplayMessage));
        String antiReplayHash = Hash
            .hash(antiReplayMessage);
        // System.out.println("sending antireply hash "
        // + Hash.hexcode(antiReplayHash.getBytes()));
        Crypto.enc(securityKey, antiReplayHash.getBytes(),
            out);
        out.flush();
        top
            .append("Successfully set up connection to server. Type commands to "
                + "send in the lower text area\n");
        frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
        // System.out.println("packet mode");
        send.addActionListener(new ActionListener()
        {
            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                send.setEnabled(false);
                new Thread()
                {
                    public void run()
                    {
                        String message = bottom.getText();
                        synchronized (top)
                        {
                            top
                                .append("---------------------------------\n");
                            top.append(">>>>>\n");
                            top.append(message + "\n");
                            top.setCaretPosition(top
                                .getDocument().getLength());
                        }
                        bottom.setEnabled(false);
                        bottom.setText("Sending...");
                        try
                        {
                            Crypto.enc(securityKey, message
                                .getBytes(), out);
                            out.flush();
                        }
                        catch (IOException e)
                        {
                            System.err
                                .println("Exception when sending to server, exiting.");
                            e.printStackTrace();
                            System.exit(0);
                        }
                        bottom.setText("");
                        bottom.setEnabled(true);
                        send.setEnabled(true);
                    }
                }.start();
            }
            
        });
        send.setEnabled(true);
        while (!socket.isClosed())
        {
            try
            {
                // System.out.println("about to decrypt");
                byte[] message = Crypto.dec(securityKey,
                    in, 65535);
                // System.out.println("decrypted");
                String messageString = new String(message);
                synchronized (top)
                {
                    top
                        .append("---------------------------------\n");
                    top.append("<<<<<\n");
                    top.append(messageString + "\n");
                    top.setCaretPosition(top.getDocument()
                        .getLength());
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                socket.close();
            }
        }
        // System.out.println("done.");
        top.append("---------------------------------\n");
        top.append("Connection to server has been closed."
            + "\n");
        top.setCaretPosition(top.getDocument().getLength());
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
