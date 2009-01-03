package org.bzflag.jzapi.examples.swingposition;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;

import org.bzflag.jzapi.BasePlayerRecord;
import org.bzflag.jzapi.BzfsAPI;
import org.bzflag.jzapi.PlayerUpdateState;
import org.bzflag.jzapi.BzfsAPI.PlayerStatus;
import org.bzflag.jzapi.internal.SimpleBind;

public class SwingPositionComponent extends JComponent
{
    public SwingPositionComponent()
    {
        setOpaque(false);
        setPreferredSize(new Dimension(300, 300));
    }
    
    public void paintComponent(Graphics g)
    {
        float worldSize = SimpleBind.bz_getWorldSize();
        if (worldSize < 0)
            worldSize = 800;
        float targetSize =
            Math.min(getWidth(), getHeight());
        float scale = targetSize / worldSize;
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, (int) (targetSize - 1),
            (int) (targetSize - 1));
        g.setColor(Color.BLACK);
        g.drawRect(0, 0, (int) (targetSize - 1),
            (int) (targetSize - 1));
        int[] players = BzfsAPI.getPlayerIndexList();
        for (int player : players)
        {
            BasePlayerRecord record =
                BzfsAPI.getPlayerRecord(player);
            if (record != null)
            {
                PlayerUpdateState state =
                    record.getCurrentState();
                float[] pos = state.getPos();
                float x = pos[0];
                float y = pos[1];
                x = x + (worldSize / 2);
                y = y + (worldSize / 2);
                float scaledX = x * scale;
                scaledX = targetSize - scaledX;
                float scaledY = y * scale;
                if (state.getStatus() == PlayerStatus.dead)
                    g.setColor(Color.RED);
                else if (state.getStatus() == PlayerStatus.exploding)
                    g.setColor(Color.YELLOW);
                else if (state.getStatus() == PlayerStatus.paused)
                    g.setColor(Color.LIGHT_GRAY);
                else
                    g.setColor(Color.BLACK);
                g.fillOval((int) (scaledX - 3),
                    (int) (scaledY - 3), 6, 6);
                g.drawString(record.getCallsign(),
                    (int) (scaledX + 5), (int) scaledY);
            }
        }
    }
}
