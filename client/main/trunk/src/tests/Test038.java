package tests;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;

import com.jidesoft.dialog.AbstractDialogPage;
import com.jidesoft.dialog.BannerPanel;
import com.jidesoft.dialog.ButtonEvent;
import com.jidesoft.dialog.ButtonNames;
import com.jidesoft.dialog.PageList;
import com.jidesoft.wizard.CompletionWizardPage;
import com.jidesoft.wizard.DefaultWizardPage;
import com.jidesoft.wizard.WelcomeWizardPage;
import com.jidesoft.wizard.WizardDialogPane;
import com.jidesoft.wizard.WizardStyle;

import net.sf.opengroove.client.Statics;
import net.sf.opengroove.client.ui.FillContainer;
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
        final WizardDialogPane wizard = new WizardDialogPane()
        {
            private JLabel titleLabel;
            
            @Override
            protected void updateBannerPanel(
                JComponent bannerPanel,
                AbstractDialogPage page)
            {
                titleLabel.setText(page.getTitle());
            }
            
            @Override
            public JComponent createBannerPanel()
            {
                FillContainer fill = new FillContainer();
                fill.setFillImageName("test-wizard");
                fill.setLayout(new BorderLayout());
                titleLabel = new JLabel(" ");
                fill.add(titleLabel);
                JPanel panel = new JPanel();
                panel.setLayout(new BorderLayout());
                panel.add(fill);
                panel.add(new JSeparator(),
                    BorderLayout.SOUTH);
                fill.setBorder(new EmptyBorder(12, 20, 12, 10));
                return panel;
            }
        };
        PageList model = new PageList();
        DefaultWizardPage page1 = new WelcomeWizardPage(
            "This is the title",
            "This is the description for this page.");
        final DefaultWizardPage page2 = new WelcomeWizardPage(
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
            
            public boolean showBannerPane()
            {
                return true;
            }
        };
        DefaultWizardPage page3 = new WelcomeWizardPage(
            "This is the title3",
            "This is the description for this page3.")
        {
            
            @Override
            public void setupWizardButtons()
            {
                // TODO Auto-generated method stub
                super.setupWizardButtons();
            }
        };
        DefaultWizardPage page4 = new CompletionWizardPage(
            "This is the completion title",
            "This is the description for the completion page.");
        model.append(page1);
        model.append(page2);
        model.append(page3);
        model.append(page4);
        wizard.setPageList(model);
        wizard.initComponents();
        wizard.setFinishAction(new AbstractAction(
            ButtonNames.FINISH)
        {
            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                System.out.println(wizard
                    .closeCurrentPage());
                wizard.setCurrentPage(page2.getTitle());
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
        frame.setSize(650, 500);
        frame.setLocationRelativeTo(null);
        frame.show();
    }
}
