package modes;

import graphics.BasicObject;
import umlEditorComponents.Model;

import javax.swing.event.MouseInputAdapter;
import java.awt.event.MouseEvent;

public abstract class BasicObjectMode extends MouseInputAdapter
{
    protected abstract BasicObject createConcreteBasicObject(MouseEvent e);

    @Override
    public void mouseClicked(MouseEvent e)
    {
        BasicObject basicObject = createConcreteBasicObject(e);
        Model.INSTANCE.addObject(basicObject);

        int clipOffset = 4;
        Model.INSTANCE.getCanvas().repaint(
                basicObject.getUnselectedTopLeftCorner().x - clipOffset / 2,
                basicObject.getUnselectedTopLeftCorner().y - clipOffset / 2,
                basicObject.getUnselectedWidth() + clipOffset,
                basicObject.getUnselectedHeight() + clipOffset
        );
    }
}