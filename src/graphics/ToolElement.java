package graphics;

import java.awt.*;

public abstract class ToolElement implements Drawable
{
    protected final boolean connectable, movable;
    protected boolean selected;

    protected ToolElement(boolean connectable, boolean movable, boolean selected)
    {
        this.connectable = connectable;
        this.movable = movable;
        this.selected = selected;
    }

    public abstract void addToRepaintArea(Rectangle repaintArea);
    abstract boolean isContainedInSelectionBox(Rectangle selectionBox);
    protected abstract Rectangle transformHitPosition(Point coordinate, Graphics2D g2d);
    public abstract boolean isHit(Point coordinate);

    public boolean isConnectable()
    {
        return connectable;
    }

    public boolean isMovable()
    {
        return movable;
    }

    public boolean isSelected()
    {
        return selected;
    }

    public void setSelected(boolean selected)
    {
        this.selected = selected;
    }
}