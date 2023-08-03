package graphics;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class UseCase extends BasicObject
{
    public static final int UNSELECTED_WIDTH = 120, UNSELECTED_HEIGHT = 80;
    private final Ellipse2D.Double ellipse = new Ellipse2D.Double();

    public UseCase(Point unselectedTopLeftCorner)
    {
        super(unselectedTopLeftCorner);
    }

    @Override
    public int getUnselectedWidth()
    {
        return UNSELECTED_WIDTH;
    }

    @Override
    public int getUnselectedHeight()
    {
        return UNSELECTED_HEIGHT;
    }

    @Override
    public void draw(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g;

        ellipse.setFrame(unselectedTopLeftCorner.getX(), unselectedTopLeftCorner.getY(), UNSELECTED_WIDTH, UNSELECTED_HEIGHT);
        g2d.setPaint(new Color(255, 173, 176, 128));
        g2d.fill(ellipse);
        g2d.setPaint(Color.BLACK);
        g2d.setStroke(new BasicStroke(2f));
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.draw(ellipse);
        if (savedGraphics2D != null)
        {
            savedGraphics2D.dispose();
        }
        savedGraphics2D = (Graphics2D) g2d.create();

        if (name != null && name.length() > 0)
        {
            drawName(g);
        }

        if (selected)
        {
            drawPorts(g);
        }
    }

    @Override
    public boolean isHit(Point coordinate)
    {
        return savedGraphics2D.hit(transformHitPosition(coordinate, savedGraphics2D), ellipse, false);
    }
}