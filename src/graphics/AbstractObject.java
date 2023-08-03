package graphics;

import containers.Triple;
import umlEditorComponents.Model;

import java.awt.*;
import java.util.List;

public abstract class AbstractObject extends ToolElement
{
    protected final Point unselectedTopLeftCorner;
    protected Graphics2D savedGraphics2D;
    protected String name = null;

    protected AbstractObject(boolean connectable, boolean selected, Point unselectedTopLeftCorner)
    {
        super(connectable, true, selected);

        this.unselectedTopLeftCorner = unselectedTopLeftCorner.getLocation();
    }

    public abstract int getUnselectedWidth();
    public abstract int getUnselectedHeight();

    public abstract Point getSelectedTopLeftCorner();
    public abstract int getSelectedWidth();
    public abstract int getSelectedHeight();

    public abstract Triple<List<ToolElement>, List<AbstractObject>, List<ConnectionLine>> getMembers();  // percolating up

    @Override
    public void addToRepaintArea(Rectangle repaintArea)
    {
        repaintArea.add(
                new Rectangle(
                        getSelectedTopLeftCorner().x,
                        getSelectedTopLeftCorner().y,
                        getSelectedWidth(),
                        getSelectedHeight()
                )
        );
    }

    @Override
    boolean isContainedInSelectionBox(Rectangle selectionBox)
    {
        return selectionBox.contains(unselectedTopLeftCorner.x, unselectedTopLeftCorner.y, getUnselectedWidth(), getUnselectedHeight());
    }

    // transform the hit position to device space
    @Override
    protected Rectangle transformHitPosition(Point coordinate, Graphics2D g2d)
    {
        double[] matrix = new double[4];
        // top-left coordinate of the hit position
        matrix[0] = coordinate.getX();
        matrix[1] = coordinate.getY();
        // bottom-right coordinate of the hit position
        matrix[2] = matrix[0] + 1;
        matrix[3] = matrix[1] + 1;

        // transform user coordinates to a virtual device space that approximates the expected resolution of the target device
        g2d.getTransform().transform(matrix, 0, matrix, 0, 2);

        // set orientation of the rectangle
        if (matrix[2] - matrix[0] < 0)
        {
            double t = matrix[0];
            matrix[0] = matrix[2];
            matrix[2] = t;
        }
        if (matrix[3] - matrix[1] < 0)
        {
            double t = matrix[1];
            matrix[1] = matrix[3];
            matrix[3] = t;
        }

        return new Rectangle((int) matrix[0], (int) matrix[1], (int) (matrix[2] - matrix[0]), (int) (matrix[3] - matrix[1]));
    }

    public int[] translate(int offsetX, int offsetY)
    {
        // When an AbstractObject is being moved (in the selected state), it must not exceed the boundaries of the Canvas.
        if (getSelectedTopLeftCorner().x + offsetX < 0)  // move left
        {
            offsetX = -getSelectedTopLeftCorner().x;
        }
        else if (getSelectedTopLeftCorner().x + getSelectedWidth() + offsetX > Model.INSTANCE.getCanvas().getWidth())  // move right
        {
            offsetX = Model.INSTANCE.getCanvas().getWidth() - getSelectedTopLeftCorner().x - getSelectedWidth();
        }

        if (getSelectedTopLeftCorner().y + offsetY < 0)  // move up
        {
            offsetY = -getSelectedTopLeftCorner().y;
        }
        else if (getSelectedTopLeftCorner().y + getSelectedHeight() + offsetY > Model.INSTANCE.getCanvas().getHeight())  // move down
        {
            offsetY = Model.INSTANCE.getCanvas().getHeight() - getSelectedTopLeftCorner().y - getSelectedHeight();
        }

        unselectedTopLeftCorner.translate(offsetX, offsetY);

        return new int[] {offsetX, offsetY};
    }

    public Point getUnselectedTopLeftCorner()
    {
        return unselectedTopLeftCorner.getLocation();
    }

    public String getName()
    {
        return name;
    }

    /**
     * Sets the name of the AbstractObject.
     * <p>
     * This method sets the name of the AbstractObject to the specified name.
     * For most objects, this operation will result in repainting the object,
     * and thus, the method returns true. However, for CompositeObject, this method
     * should be overridden to return false, indicating that repainting is not needed.
     *
     * @param name The new name to be set for the AbstractObject.
     * @return Returns true if the name is successfully set and the AbstractObject needs to be repainted,
     *         returns false if the AbstractObject does not need to be repainted.
     */
    public boolean setName(String name)
    {
        this.name = name;

        return true;
    }
}