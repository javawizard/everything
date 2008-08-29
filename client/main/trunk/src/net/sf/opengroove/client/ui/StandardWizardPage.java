package net.sf.opengroove.client.ui;

import com.jidesoft.dialog.ButtonEvent;
import com.jidesoft.dialog.ButtonNames;
import com.jidesoft.wizard.DefaultWizardPage;

public abstract class StandardWizardPage extends
    DefaultWizardPage
{
    private boolean isBackAllowed;
    private boolean isCancelAllowed;
    private boolean isNextAllowed;
    private boolean isLastStep;
    
    public StandardWizardPage(String title,
        boolean isBackAllowed, boolean isCancelAllowed,
        boolean isNextAllowed, boolean isLastStep)
    {
        super(title);
        this.isBackAllowed = isBackAllowed;
        this.isCancelAllowed = isCancelAllowed;
        this.isNextAllowed = isNextAllowed;
        this.isLastStep = isLastStep;
    }
    
    protected abstract void init();
    
    protected void initContentPane()
    {
        super.initContentPane();
        init();
    }
    
    public void setupWizardButtons()
    {
        super.setupWizardButtons();
        reloadButtons();
    }
    
    private void reloadButtons()
    {
        fireButtonEvent(
            isBackAllowed ? ButtonEvent.ENABLE_BUTTON
                : ButtonEvent.DISABLE_BUTTON,
            ButtonNames.BACK);
        fireButtonEvent(
            isCancelAllowed ? ButtonEvent.ENABLE_BUTTON
                : ButtonEvent.DISABLE_BUTTON,
            ButtonNames.CANCEL);
        String button = isLastStep ? ButtonNames.FINISH
            : ButtonNames.NEXT;
        String notButton = isLastStep ? ButtonNames.NEXT
            : ButtonNames.FINISH;
        fireButtonEvent(ButtonEvent.SHOW_BUTTON, button);
        fireButtonEvent(ButtonEvent.HIDE_BUTTON, notButton);
        if (isNextAllowed)
            fireButtonEvent(ButtonEvent.ENABLE_BUTTON,
                button);
        else
            fireButtonEvent(ButtonEvent.DISABLE_BUTTON,
                button);
    }
    
    //    
    // private void showButton(String button)
    // {
    // fireButtonEvent(ButtonEvent.SHOW_BUTTON, button);
    // }
    //    
    // private void hideButton(String button)
    // {
    // fireButtonEvent(ButtonEvent.HIDE_BUTTON, button);
    // }
    
    public boolean isBackAllowed()
    {
        return isBackAllowed;
    }
    
    public boolean isCancelAllowed()
    {
        return isCancelAllowed;
    }
    
    public boolean isNextAllowed()
    {
        return isNextAllowed;
    }
    
    public boolean isLastStep()
    {
        return isLastStep;
    }
    
    public void setBackAllowed(boolean isBackAllowed)
    {
        this.isBackAllowed = isBackAllowed;
        reloadButtons();
    }
    
    public void setCancelAllowed(boolean isCancelAllowed)
    {
        this.isCancelAllowed = isCancelAllowed;
        reloadButtons();
    }
    
    public void setNextAllowed(boolean isNextAllowed)
    {
        this.isNextAllowed = isNextAllowed;
        reloadButtons();
    }
    
    public void setLastStep(boolean isLastStep)
    {
        this.isLastStep = isLastStep;
        reloadButtons();
    }
}
