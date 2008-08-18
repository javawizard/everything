package net.sf.opengroove.projects.filleditor.plugins;

import java.awt.Color;
import java.awt.Graphics2D;

import net.sf.opengroove.projects.filleditor.FillParameter;
import net.sf.opengroove.projects.filleditor.FillPlugin;
import net.sf.opengroove.projects.filleditor.Region;

public class GradientPlugin implements FillPlugin
{
    private FillParameter[] parameters;
    private FillParameter firstColorParam;
    private FillParameter secondColorParam;
    private FillParameter firstPointParam;
    private FillParameter secondPointParam;
    
    public GradientPlugin()
    {
        firstColorParam = new FillParameter();
        secondColorParam = new FillParameter();
        firstPointParam = new FillParameter();
        secondPointParam = new FillParameter();
        firstColorParam.name = "Color 1";
        firstColorParam.type = FillParameter.Type.COLOR;
        firstColorParam.value = Color.WHITE;
        secondColorParam.name = "Color 2";
        secondColorParam.type = FillParameter.Type.COLOR;
        secondColorParam.value = Color.WHITE;
    }
    
    @Override
    public void draw(Region region, Graphics2D g)
    {
    }
    
    @Override
    public FillParameter[] getParameters()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
}
