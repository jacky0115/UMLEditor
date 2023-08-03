package modes;

import graphics.BasicObject;
import graphics.UseCase;
import umlEditorComponents.Model;

import java.awt.*;
import java.awt.event.MouseEvent;

public class UseCaseMode extends BasicObjectMode
{
    public static final int NUMBER = 5;

    @Override
    protected BasicObject createConcreteBasicObject(MouseEvent e)
    {
        // UseCase (in the selected state) shouldn't extend past the drawing area
        int x = Math.min(e.getX(), Model.INSTANCE.getCanvas().getWidth() - BasicObject.ConnectionPort.SIDE_LENGTH / 2 - UseCase.UNSELECTED_WIDTH);
        int y = Math.min(e.getY(), Model.INSTANCE.getCanvas().getHeight() - BasicObject.ConnectionPort.SIDE_LENGTH / 2 - UseCase.UNSELECTED_HEIGHT);

        return new UseCase(new Point(x, y));
    }
}