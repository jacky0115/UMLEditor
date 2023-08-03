package modes;

import graphics.AbstractObject;
import graphics.SelectionBox;
import graphics.ToolElement;
import umlEditorComponents.Model;

import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;

public class SelectMode extends MouseInputAdapter
{
    public static final int NUMBER = 0;
    private Point startCoordinate, lastEndCoordinate;
    private ToolElement pressedElement;
    private static final SelectionBox selectionBox = new SelectionBox();
    private static final Rectangle repaintArea = new Rectangle();

    @Override
    public void mousePressed(MouseEvent e)
    {
        startCoordinate = lastEndCoordinate = e.getPoint();
        pressedElement = Model.INSTANCE.pressedEventHandler(e);
    }

    @Override
    public void mouseDragged(MouseEvent e)
    {
        if (pressedElement != null && pressedElement.isMovable())  // move AbstractObject
        {
            AbstractObject pressedObject = (AbstractObject) pressedElement;
            Model.INSTANCE.moveObject(pressedObject, e.getX() - lastEndCoordinate.x, e.getY() - lastEndCoordinate.y);
        }
        else if (pressedElement == null)  // select or unselect a group of objects
        {
            // repaint the background over the old selection box range
            repaintArea.setFrameFromDiagonal(startCoordinate, lastEndCoordinate);

            selectionBox.setBounds(startCoordinate, e.getPoint());

            // repaint the new selection box range
            repaintArea.add(e.getPoint());

            // In order to eliminate the residual edges left by the SelectionBox during scaling, it is necessary to further expand the repaintArea.
            int clipOffset = 4;
            Model.INSTANCE.getCanvas().repaint(repaintArea.x - clipOffset / 2, repaintArea.y - clipOffset / 2, repaintArea.width + clipOffset, repaintArea.height + clipOffset);
        }

        lastEndCoordinate = e.getPoint();
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        /*
        !startCoordinate.equals(e.getPoint()) is used to handle the case when the mouse is pressed and released at
        the same location without selecting any ToolElement. In such a situation, we do not want to perform any
        action, so this condition helps to skip unnecessary processing and prevent unexpected behavior in that
        specific scenario.
         */
        if (pressedElement == null && !startCoordinate.equals(e.getPoint()))  // select or unselect a group of objects
        {
            Model.INSTANCE.setElementsInSelectionBoxToSelected(selectionBox);

            selectionBox.resetBounds();

            // continue to use the repaintArea set in mouseDragged
            // In order to draw the complete arrow and ConnectionPort, it is necessary to further expand the repaintArea.
            int clipOffset = 20;
            Model.INSTANCE.getCanvas().repaint(repaintArea.x - clipOffset / 2, repaintArea.y - clipOffset / 2, repaintArea.width + clipOffset, repaintArea.height + clipOffset);
        }
        // If the mouse is pressed on a ConnectionLine (which sets it to the selected state), and then dragged before being released, it should be set back to an unselected state upon release.
        else if (pressedElement != null && !pressedElement.isMovable() && !startCoordinate.equals(e.getPoint()))
        {
            Model.INSTANCE.dragLine(pressedElement);
        }
    }

    public static SelectionBox getSelectionBox()
    {
        return selectionBox;
    }
}