package tests;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;

import com.jidesoft.dialog.ButtonEvent;
import com.jidesoft.dialog.ButtonNames;
import com.jidesoft.dialog.PageList;
import com.jidesoft.wizard.CompletionWizardPage;
import com.jidesoft.wizard.WelcomeWizardPage;
import com.jidesoft.wizard.WizardDialogPane;
import com.jidesoft.wizard.WizardStyle;

import net.sf.opengroove.client.Statics;
import net.sf.opengroove.client.ui.TestFrame;

public class Test038
{
    
    /**
     * @param args
     */
    public static void main(String[] args)
    {
        Statics.run();
        WizardStyle.setStyle(WizardStyle.WIZARD97_STYLE);
        final JFrame frame = new JFrame(
            "Test038 - OpenGroove");
        final WizardDialogPane wizard = new WizardDialogPane();
        PageList model = new PageList();
        model.append(new WelcomeWizardPage(
            "This is the title",
            "This is the description for this page."));
        model.append(new WelcomeWizardPage(
            "This is the title2",
            "This is the description for this page2.")
        {
            
            @Override
            public void setupWizardButtons()
            {
                // TODO Auto-generated method stub
                super.setupWizardButtons();
                if (wizard.getVisitedPages().size() > 0)
                    fireButtonEvent(
                        ButtonEvent.ENABLE_BUTTON,
                        ButtonNames.BACK);
            }
        });
        model.append(new WelcomeWizardPage(
            "This is the title3",
            "This is the description for this page3.")
        {
            
            @Override
            public void setupWizardButtons()
            {
                // TODO Auto-generated method stub
                super.setupWizardButtons();
            }
        });
        model
            .append(new CompletionWizardPage(
                "This is the completion title",
                "This is the description for the completion page."));
        wizard.setPageList(model);
        wizard.initComponents();
        wizard.setFinishAction(new AbstractAction(
            ButtonNames.FINISH)
        {
            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                frame.dispose();
            }
        });
        wizard.setCancelAction(new AbstractAction(
            ButtonNames.CANCEL)
        {
            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                frame.dispose();
            }
        });
        frame.getContentPane().add(wizard);
        frame.pack();
        frame.setSize(500, 350);
        frame.setLocationRelativeTo(null);
        frame.show();
    }
}
