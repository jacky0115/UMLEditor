package graphics;

import umlEditorComponents.Model;

import java.awt.*;

public class SelectionBox implements Drawable
{
    private final Rectangle rectangle = new Rectangle();

    @Override
    public void draw(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g;

        g2d.setPaint(new Color(0, 102, 204, 128));
        g2d.fill(rectangle);
        g2d.setPaint(new Color(0, 120, 215));
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.draw(rectangle);
    }

    public void setBounds(Point diagonalPoint1, Point diagonalPoint2)
    {
        Point coordinate = diagonalPoint2.getLocation();

        if (coordinate.x < 0)
        {
            coordinate.x = 0;
        }
        else if (coordinate.x > Model.INSTANCE.getCanvas().getWidth())
        {
            coordinate.x = Model.INSTANCE.getCanvas().getWidth();
        }

        if (coordinate.y < 0)
        {
            coordinate.y = 0;
        }
        else if (coordinate.y > Model.INSTANCE.getCanvas().getHeight())
        {
            coordinate.y = Model.INSTANCE.getCanvas().getHeight();
        }

        rectangle.setFrameFromDiagonal(diagonalPoint1, coordinate);
    }

    public void resetBounds()
    {
        rectangle.setBounds(0, 0, 0, 0);
    }

    public boolean contains(ToolElement element)
    {
        return element.isContainedInSelectionBox(rectangle);
    }
}