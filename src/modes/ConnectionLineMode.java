package modes;

import graphics.BasicObject;
import graphics.ConnectionLine;
import graphics.ToolElement;
import umlEditorComponents.Model;

import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.Arrays;
import java.util.Comparator;

public abstract class ConnectionLineMode extends MouseInputAdapter
{
    private Point startCoordinate, lastEndCoordinate;
    protected BasicObject startBasicObject = null, endBasicObject = null;
    protected BasicObject.ConnectionPort startPort, endPort;
    private static final Line2D.Double tempLine = new Line2D.Double();
    private static final Rectangle repaintArea = new Rectangle();

    protected abstract ConnectionLine createConcreteConnectionLine();

    private BasicObject.ConnectionPort getNearestConnectionPort(BasicObject basicObject, Point coordinate)
    {
        return Arrays.stream(basicObject.getPorts())
                .min(Comparator.comparingDouble(port -> coordinate.distanceSq(port.getCenterX(), port.getCenterY())))
                .orElseThrow();
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        startCoordinate = lastEndCoordinate = e.getPoint();

        ToolElement element = Model.INSTANCE.pressedEventHandler(e);
        if (element != null && element.isConnectable())  // BasicObject
        {
            startBasicObject = (BasicObject) element;
            startPort = getNearestConnectionPort(startBasicObject, startCoordinate);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e)
    {
        repaintArea.setFrameFromDiagonal(startCoordinate, lastEndCoordinate);
        tempLine.setLine(startCoordinate, e.getPoint());
        repaintArea.add(e.getPoint());

        int clipOffset = 4;
        Model.INSTANCE.getCanvas().repaint(repaintArea.x - clipOffset / 2, repaintArea.y - clipOffset / 2, repaintArea.width + clipOffset, repaintArea.height + clipOffset);

        lastEndCoordinate = e.getPoint();
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        repaintArea.setFrameFromDiagonal(startCoordinate, lastEndCoordinate);
        tempLine.setLine(0, 0, 0, 0);

        ToolElement element = Model.INSTANCE.getTopmostHitElement(e);
        if (element != null && element.isConnectable())
        {
            endBasicObject = (BasicObject) element;
            endPort = getNearestConnectionPort(endBasicObject, e.getPoint());
        }

        if (startBasicObject != null && endBasicObject != null)
        {
            ConnectionLine line = createConcreteConnectionLine();
            if (line != null)
            {
                Model.INSTANCE.addLine(line);

                repaintArea.add(startPort.getCenterX(), startPort.getCenterY());
                repaintArea.add(endPort.getCenterX(), endPort.getCenterY());
            }
        }

        // In order to draw the complete arrow, it is necessary to further expand the repaintArea.
        int clipOffset = 20;
        Model.INSTANCE.getCanvas().repaint(repaintArea.x - clipOffset / 2, repaintArea.y - clipOffset / 2, repaintArea.width + clipOffset, repaintArea.height + clipOffset);

        startBasicObject = endBasicObject = null;
    }

    public static Line2D.Double getTempLine()
    {
        return tempLine;
    }
}