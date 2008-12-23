package org.bzflag.jzapi.examples.sui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.bzflag.jzapi.BzfsAPI;
import org.bzflag.jzapi.internal.SimpleBind;

public class SwingPlugin
{
    public static JFrame frame;
    
    public static boolean repetitiveGMFire = false;
    
    public static void load(String args)
    {
        frame = new JFrame("SwingUI Plugin");
        frame.setSize(400, 330);
        frame.setLocationRelativeTo(null);
        frame
            .setDefaultCloseOperation(frame.DISPOSE_ON_CLOSE);
        JPanel content = new JPanel();
        content.setLayout(new BorderLayout());
        frame.getContentPane().add(content);
        JSplitPane split = new JSplitPane();
        content.add(split);
        split.setResizeWeight(0.5d);
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());
        split.setLeftComponent(leftPanel);
        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new BoxLayout(actionPanel,
            BoxLayout.Y_AXIS));
        leftPanel.add(actionPanel, BorderLayout.NORTH);
        loadActions(actionPanel);
        frame.show();
    }
    
    private static void loadActions(JPanel actionPanel)
    {
        actionPanel.add(new JActionButton(
            "getPlayerIndexList")
        {
            
            public void actionPerformed(ActionEvent e)
            {
                int[] playerIndexes =
                    BzfsAPI.getPlayerIndexList();
                String list = "<html>Index list:<br/>";
                for (int index : playerIndexes)
                {
                    list += "<br/>" + index;
                }
                JOptionPane.showMessageDialog(frame, list);
            }
        });
        actionPanel.add(new JActionButton(
            "delayed fireWorldWeapon")
        {
            
            public void actionPerformed(ActionEvent e)
            {
                // flagtype,lifetime,pos,tilt,direction,shot,dt
                String input =
                    JOptionPane
                        .showInputDialog(
                            frame,
                            "Type ww params (when,flagtype,lifetime,posx,posy,posz,tilt,direction)");
                if (input == null)
                    return;
                final String[] inputParsed =
                    input.split("\\,");
                new Thread()
                {
                    public void run()
                    {
                        try
                        {
                            Thread.sleep(Integer
                                .parseInt(inputParsed[0]));
                        }
                        catch (NumberFormatException e)
                        {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        catch (InterruptedException e)
                        {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        System.out.println("firing ww");
                        SimpleBind
                            .bz_fireWorldWep(
                                inputParsed[1],
                                Float
                                    .parseFloat(inputParsed[2]),
                                new float[] {
                                    Float
                                        .parseFloat(inputParsed[3]),
                                    Float
                                        .parseFloat(inputParsed[4]),
                                    Float
                                        .parseFloat(inputParsed[5]) },
                                Float
                                    .parseFloat(inputParsed[6]),
                                Float
                                    .parseFloat(inputParsed[7]),
                                0, 0);
                        System.out.println("fired.");
                    }
                }.start();
            }
        });
        actionPanel.add(new JActionButton(
            "startRepetitiveGMFiring")
        {
            
            public void actionPerformed(ActionEvent e)
            {
                repetitiveGMFire = true;
                new Thread()
                {
                    public void run()
                    {
                        while (repetitiveGMFire)
                        {
                            try
                            {
                                Thread.sleep(5000);
                            }
                            catch (InterruptedException e)
                            {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            for(int index : BzfsAPI.getPlayerIndexList())
                            {
                            }
                        }
                    }
                }.start();
            }
        });
    }
    
    private static abstract class JActionButton extends
        JButton implements ActionListener
    {
        public JActionButton(String name)
        {
            super(name);
            addActionListener(this);
        }
    }
    
    public static void unload()
    {
        
    }
}
