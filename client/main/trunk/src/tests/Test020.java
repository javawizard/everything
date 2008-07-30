package tests;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.basic.BasicBorders;

import org.awl.DefaultWizardPageDescriptor;
import org.awl.NavigationAuthorization;
import org.awl.Wizard;
import org.awl.WizardConstants;

public class Test020
{
    
    /**
     * @param args
     * @throws UnsupportedLookAndFeelException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     */
    public static void main(String[] args)
        throws ClassNotFoundException,
        InstantiationException, IllegalAccessException,
        UnsupportedLookAndFeelException
    {
        // A class for testing wizard navigation and how to restrict going
        // forward or backward
        Wizard wizard = new Wizard((JFrame) null);
        DefaultWizardPageDescriptor p1 = new DefaultWizardPageDescriptor(
            "title1");
        p1.setDescription("this is page 1");
        p1
            .setPreviousDescriptorId(WizardConstants.STARTING_DESCRIPTOR_ID);
        p1.setNextDescriptorId("2");
        p1.setComponent(new JLabel("page 1 contents"));
        final DefaultWizardPageDescriptor p2 = new DefaultWizardPageDescriptor(
            "title1")
        {
            
            @Override
            public void displayingPanel(Wizard wizard)
            {
                // TODO Auto-generated method stub
                System.out.println("displaying");
                super.displayingPanel(wizard);
            }
            
        };
        
        p2.setDescription("this is page 2");
        p2.setPreviousDescriptorId("1");
        p2
            .setCancelAuthorization(NavigationAuthorization.FORBIDDEN);
        p2
            .setNextDescriptorId(WizardConstants.TERMINAL_DESCRIPTOR_ID);
        JPanel p2panel = new JPanel();
        JButton b = new JButton(
            "click me to allow to continue");
        p2panel.add(b);
        p2
            .setFinishAuthorization(NavigationAuthorization.FORBIDDEN);
        b.addActionListener(new ActionListener()
        {
            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                p2
                    .setFinishAuthorization(NavigationAuthorization.ALLOWED);
            }
        });
        p2.setComponent(p2panel);
        wizard.registerWizardPanel("1", p1);
        wizard.registerWizardPanel("2", p2);
        wizard.setSize(400, 300);
        wizard.getNextButton().setText(" Next ");
        wizard.getBackButton().setText(" Back ");
        wizard.getFinishButton().setText(" Finish ");
        wizard.getCancelButton().setText(" Cancel ");
        wizard.getNextButton().setBorder(
            BasicBorders.getButtonBorder());
        wizard.getBackButton().setBorder(
            BasicBorders.getButtonBorder());
        wizard.getFinishButton().setBorder(
            BasicBorders.getButtonBorder());
        wizard.getCancelButton().setBorder(
            BasicBorders.getButtonBorder());
        wizard.show();
    }
    
}
