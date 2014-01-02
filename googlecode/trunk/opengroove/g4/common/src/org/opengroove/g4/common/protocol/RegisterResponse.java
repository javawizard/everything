package org.opengroove.g4.common.protocol;

import javax.swing.JPanel;

import org.opengroove.g4.common.Packet;
import org.opengroove.g4.common.protocol.doc.ServerToClient;
@ServerToClient
public class RegisterResponse extends Packet
{
    private JPanel component;
    
    public void setComponent(JPanel component)
    {
        this.component = component;
    }
    
    /**
     * If registration failed, a component indicating why registration failed,
     * and if the registration info status was INFO, then the component that
     * collects information about the user with an error message indicating why
     * registration did not complete.
     * 
     * @return
     */
    public JPanel getComponent()
    {
        return component;
    }
}
