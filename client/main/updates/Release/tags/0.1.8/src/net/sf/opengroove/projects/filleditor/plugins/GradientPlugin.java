package net.sf.opengroove.projects.filleditor.plugins;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;

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
    private FillParameter cyclicParameter;
    
    public GradientPlugin()
    {
        firstColorParam = new FillParameter();
        secondColorParam = new FillParameter();
        firstPointParam = new FillParameter();
        secondPointParam = new FillParameter();
        cyclicParameter = new FillParameter();
        firstColorParam.name = "Color 1";
        firstColorParam.type = FillParameter.Type.COLOR;
        firstColorParam.value = Color.WHITE;
        secondColorParam.name = "Color 2";
        secondColorParam.type = FillParameter.Type.COLOR;
        secondColorParam.value = Color.WHITE;
        firstPointParam.name = "Point 1";
        firstPointParam.type = FillParameter.Type.POINT;
        firstPointParam.value = new Point(0, 0);
        secondPointParam.name = "Point 2";
        secondPointParam.type = FillParameter.Type.POINT;
        secondPointParam.value = new Point(100, 100);
        cyclicParameter.name = "Cyclic";
        cyclicParameter.type = FillParameter.Type.BOOLEAN;
        cyclicParameter.value = false;
        parameters = new FillParameter[] { firstColorParam,
            secondColorParam, firstPointParam,
            secondPointParam, cyclicParameter };
    }
    
    @Override
    public void draw(Region region, Graphics2D g,
        int width, int height)
    {
        GradientPaint paint = new GradientPaint(
            (Point) firstPointParam.value,
            (Color) firstColorParam.value,
            (Point) secondPointParam.value,
            (Color) secondColorParam.value,
            (Boolean) cyclicParameter.value);
        g.setPaint(paint);
        g.fillRect(0, 0, width + 1, height + 1);
    }
    
    @Override
    public FillParameter[] getParameters()
    {
        return parameters;
    }
    
}
