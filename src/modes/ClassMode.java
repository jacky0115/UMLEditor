package modes;

import graphics.BasicObject;
import graphics.Class;
import umlEditorComponents.Model;

import java.awt.*;
import java.awt.event.MouseEvent;

public class ClassMode extends BasicObjectMode
{
    public static final int NUMBER = 4;

    @Override
    protected BasicObject createConcreteBasicObject(MouseEvent e)
    {
        // Class (in the selected state) shouldn't extend past the drawing area
        int x = Math.min(e.getX(), Model.INSTANCE.getCanvas().getWidth() - BasicObject.ConnectionPort.SIDE_LENGTH / 2 - Class.UNSELECTED_WIDTH);
        int y = Math.min(e.getY(), Model.INSTANCE.getCanvas().getHeight() - BasicObject.ConnectionPort.SIDE_LENGTH / 2 - Class.UNSELECTED_HEIGHT);

        return new Class(new Point(x, y));
    }
}