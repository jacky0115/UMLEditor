package graphics;

import containers.Triple;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class CompositeObject extends AbstractObject
{
    private static final int space = 6;
    private static final BasicStroke dashed = new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10f, new float[] {10f}, 0f);
    private final RoundRectangle2D.Double roundRectangle = new RoundRectangle2D.Double();
    private final int unselectedWidth, unselectedHeight;
    private final List<ToolElement> membersInDrawingOrder;
    private final List<AbstractObject> objects;
    private final List<ConnectionLine> lines;

    public CompositeObject(Point unselectedTopLeftCorner, int unselectedWidth, int unselectedHeight, List<ToolElement> membersInDrawingOrder, List<AbstractObject> objects, List<ConnectionLine> lines)
    {
        super(false, true, unselectedTopLeftCorner);

        this.unselectedWidth = unselectedWidth;
        this.unselectedHeight = unselectedHeight;
        this.membersInDrawingOrder = List.copyOf(membersInDrawingOrder);  // this.membersInDrawingOrder is an unmodifiable List
        this.objects = List.copyOf(objects);  // this.objects is an unmodifiable List
        this.lines = List.copyOf(lines);  // this.lines is an unmodifiable List
    }

    @Override
    public int getUnselectedWidth()
    {
        return unselectedWidth;
    }

    @Override
    public int getUnselectedHeight()
    {
        return unselectedHeight;
    }

    @Override
    public Point getSelectedTopLeftCorner()
    {
        return new Point(unselectedTopLeftCorner.x - space, unselectedTopLeftCorner.y - space);
    }

    @Override
    public int getSelectedWidth()
    {
        return unselectedWidth + space * 2;
    }

    @Override
    public int getSelectedHeight()
    {
        return unselectedHeight + space * 2;
    }

    @Override
    public Triple<List<ToolElement>, List<AbstractObject>, List<ConnectionLine>> getMembers()
    {
        return new Triple<>(membersInDrawingOrder, objects, lines);
    }

    @Override
    public int[] translate(int offsetX, int offsetY)
    {
        int[] offsets = super.translate(offsetX, offsetY);

        for (AbstractObject object : objects)
        {
            object.translate(offsets[0], offsets[1]);
        }

        return offsets;
    }

    @Override
    public boolean setName(String name)
    {
        this.name = name;

        return false;
    }

    @Override
    public void draw(Graphics g)
    {
        for (ToolElement element : membersInDrawingOrder)
        {
            element.draw(g);
        }

        if (selected)
        {
            Graphics2D g2d = (Graphics2D) g;

            g2d.setPaint(Color.BLACK);
            g2d.setStroke(dashed);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            roundRectangle.setRoundRect(getSelectedTopLeftCorner().getX(), getSelectedTopLeftCorner().getY(), getSelectedWidth(), getSelectedHeight(), 10., 10.);
            g2d.draw(roundRectangle);
            if (savedGraphics2D != null)
            {
                savedGraphics2D.dispose();
            }
            savedGraphics2D = (Graphics2D) g2d.create();
        }
    }

    @Override
    public boolean isHit(Point coordinate)
    {
        return savedGraphics2D.hit(transformHitPosition(coordinate, savedGraphics2D), roundRectangle, false);
    }

    @Override
    public void setSelected(boolean selected)
    {
        super.setSelected(selected);

        for (ToolElement element : membersInDrawingOrder)
        {
            element.setSelected(selected);
        }
    }
}