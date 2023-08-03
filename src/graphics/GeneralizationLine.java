package graphics;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;

public class GeneralizationLine extends ConnectionLine
{
    private final Line2D.Double arrowShaft = new Line2D.Double();
    private final GeneralPath arrowhead = new GeneralPath(GeneralPath.WIND_NON_ZERO, 3);

    public GeneralizationLine(BasicObject startBasicObject, BasicObject.ConnectionPort startPort, BasicObject endBasicObject, BasicObject.ConnectionPort endPort)
    {
        super(startBasicObject, startPort, endBasicObject, endPort);
    }

    @Override
    public void draw(Graphics g)
    {
        drawInit();
        Graphics2D g2d = (Graphics2D) g;

        // draw a horizontal line from (0, 0) to (length - 8 * sqrt(3), 0) with an arrowhead that forms a 30-degree angle with the arrow shaft
        arrowShaft.setLine(0, 0, length - 8 * Math.sqrt(3), 0);
        arrowhead.reset();
        arrowhead.moveTo(length - 8 * Math.sqrt(3), -8);
        arrowhead.lineTo(length, 0);
        arrowhead.lineTo(length - 8 * Math.sqrt(3), 8);
        arrowhead.closePath();

        AffineTransform savedTransform = g2d.getTransform();
        AffineTransform affineTransform = new AffineTransform();

        affineTransform.translate(startPoint.x, startPoint.y);
        affineTransform.rotate(Math.atan2(endPoint.y - startPoint.y, endPoint.x - startPoint.x));

        g2d.transform(affineTransform);
        if (!selected)
        {
            g2d.setPaint(Color.BLACK);
        }
        else
        {
            g2d.setPaint(Color.ORANGE);
        }
        g2d.setStroke(new BasicStroke(2f));
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.draw(arrowShaft);
        g2d.draw(arrowhead);
        if (savedGraphics2D != null)
        {
            savedGraphics2D.dispose();
        }
        savedGraphics2D = (Graphics2D) g2d.create();

        g2d.setTransform(savedTransform);
    }

    @Override
    public boolean isHit(Point coordinate)
    {
        Rectangle coordinateRectangle = transformHitPosition(coordinate, savedGraphics2D);

        return savedGraphics2D.hit(coordinateRectangle, arrowShaft, false)
                || savedGraphics2D.hit(coordinateRectangle, arrowhead, true);
    }
}