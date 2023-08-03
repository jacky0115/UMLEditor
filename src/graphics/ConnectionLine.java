package graphics;

import java.awt.*;
import java.awt.geom.Point2D;

public abstract class ConnectionLine extends ToolElement
{
    protected final BasicObject startBasicObject, endBasicObject;
    protected final BasicObject.ConnectionPort[] ports = new BasicObject.ConnectionPort[2];
    protected enum ConnectionLinePort {START, END}
    protected final Point2D.Double startPoint = new Point2D.Double(), endPoint = new Point2D.Double();
    protected Graphics2D savedGraphics2D;
    protected double length;

    protected ConnectionLine(BasicObject startBasicObject, BasicObject.ConnectionPort startPort, BasicObject endBasicObject, BasicObject.ConnectionPort endPort)
    {
        super(false, false, false);

        this.startBasicObject = startBasicObject;
        ports[ConnectionLinePort.START.ordinal()] = startPort;
        this.endBasicObject = endBasicObject;
        ports[ConnectionLinePort.END.ordinal()] = endPort;
    }

    @Override
    public void addToRepaintArea(Rectangle repaintArea)
    {
        repaintArea.add(startPoint);
        repaintArea.add(endPoint);
    }

    @Override
    boolean isContainedInSelectionBox(Rectangle selectionBox)
    {
        return selectionBox.contains(startPoint) && selectionBox.contains(endPoint);
    }

    // transform the hit position to device space
    @Override
    protected Rectangle transformHitPosition(Point coordinate, Graphics2D g2d)
    {
        double[] matrix = new double[4];
        // the top-left coordinate of the hit position relative to the horizontal line starting from (0, 0)
        // translate back to the origin
        double x = coordinate.getX() - startPoint.x;
        double y = coordinate.getY() - startPoint.y;
        // rotate to a horizontal line
        double angle = -Math.atan2(endPoint.y - startPoint.y, endPoint.x - startPoint.x);
        matrix[0] = Math.cos(angle) * x - Math.sin(angle) * y;
        matrix[1] = Math.sin(angle) * x + Math.cos(angle) * y;

        // increase the size of the mouse hit area to facilitate easier clicking on the line
        int hitOffset = 10;
        matrix[0] -= hitOffset / 2.;
        matrix[1] -= hitOffset / 2.;

        // bottom-right coordinate of the hit position relative to the horizontal line starting from (0, 0)
        matrix[2] = matrix[0] + hitOffset;
        matrix[3] = matrix[1] + hitOffset;

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

    protected void drawInit()
    {
        startPoint.x = ports[ConnectionLinePort.START.ordinal()].getCenterX();
        startPoint.y = ports[ConnectionLinePort.START.ordinal()].getCenterY();
        endPoint.x = ports[ConnectionLinePort.END.ordinal()].getCenterX();
        endPoint.y = ports[ConnectionLinePort.END.ordinal()].getCenterY();
        length = startPoint.distance(endPoint);
    }

    public BasicObject getStartBasicObject()
    {
        return startBasicObject;
    }

    public BasicObject getEndBasicObject()
    {
        return endBasicObject;
    }

    public Point2D.Double getStartPoint()
    {
        return startPoint;
    }

    public Point2D.Double getEndPoint()
    {
        return endPoint;
    }
}