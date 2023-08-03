package graphics;

import java.awt.*;
import java.awt.geom.Line2D;

public class Class extends BasicObject
{
    public static final int UNSELECTED_WIDTH = 100, UNSELECTED_HEIGHT = 120;
    private final Rectangle rectangle = new Rectangle();
    private final Line2D.Double horizontalLine1 = new Line2D.Double(), horizontalLine2 = new Line2D.Double();

    public Class(Point unselectedTopLeftCorner)
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

        rectangle.setBounds(unselectedTopLeftCorner.x, unselectedTopLeftCorner.y, UNSELECTED_WIDTH, UNSELECTED_HEIGHT);
        g2d.setPaint(new Color(124, 220, 177, 128));
        g2d.fill(rectangle);
        g2d.setPaint(Color.BLACK);
        g2d.setStroke(new BasicStroke(2f));
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.draw(rectangle);
        if (savedGraphics2D != null)
        {
            savedGraphics2D.dispose();
        }
        savedGraphics2D = (Graphics2D) g2d.create();

        horizontalLine1.setLine(unselectedTopLeftCorner.getX(), unselectedTopLeftCorner.getY() + UNSELECTED_HEIGHT / 3., unselectedTopLeftCorner.getX() + UNSELECTED_WIDTH, unselectedTopLeftCorner.getY() + UNSELECTED_HEIGHT / 3.);
        g2d.draw(horizontalLine1);
        horizontalLine2.setLine(unselectedTopLeftCorner.getX(), unselectedTopLeftCorner.getY() + UNSELECTED_HEIGHT * 2. / 3., unselectedTopLeftCorner.getX() + UNSELECTED_WIDTH, unselectedTopLeftCorner.getY() + UNSELECTED_HEIGHT * 2. / 3.);
        g2d.draw(horizontalLine2);

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
        return savedGraphics2D.hit(transformHitPosition(coordinate, savedGraphics2D), rectangle, false);
    }
}