package tests;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;

import com.jidesoft.dialog.AbstractDialogPage;
import com.jidesoft.dialog.BannerPanel;
import com.jidesoft.dialog.ButtonEvent;
import com.jidesoft.dialog.ButtonNames;
import com.jidesoft.dialog.PageList;
import com.jidesoft.wizard.CompletionWizardPage;
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
            
            @Override
            protected void updateBannerPanel(
                JComponent bannerPanel,
                AbstractDialogPage page)
            {
                BannerPanel banner = (BannerPanel) bannerPanel
                    .getComponent(0);
                banner.setBackgroundPaint(new Color(255,
                    255, 255, 0));
            }
            
            @Override
            public JComponent createBannerPanel()
            {
                BannerPanel banner = new BannerPanel();
                banner.setOpaque(false);
                FillContainer fill = new FillContainer();
                fill.setFillImageName("test-wizard");
                fill.setLayout(new BorderLayout());
                fill.add(banner);
                return fill;
            }
        };
        wizard.setBackground(new Color(235, 235, 235));
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
