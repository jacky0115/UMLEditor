package graphics;

import containers.Triple;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.Hashtable;
import java.util.List;

public abstract class BasicObject extends AbstractObject
{
    /*
    A nested class is a member of its enclosing class. Non-static nested classes (inner
    classes) have access to other members of the enclosing class, even if they are
    declared private. Static nested classes do not have access to other members of the
    enclosing class. As a member of the OuterClass, a nested class can be declared
    private, public, protected, or package private. (Recall that outer classes can only
    be declared public or package private.)

    As with instance methods and variables, an inner class is associated with an instance
    of its enclosing class and has direct access to that object's methods and fields. Also,
    because an inner class is associated with an instance, it cannot define any static
    members itself.

    As with class methods and variables, a static nested class is associated with its outer
    class. And like static class methods, a static nested class cannot refer directly to
    instance variables or methods defined in its enclosing class: it can use them only
    through an object reference. A static nested class interacts with the instance members
    of its outer class (and other classes) just like any other top-level class. In effect,
    a static nested class is behaviorally a top-level class that has been nested in another
    top-level class for packaging convenience.
     */
    public static class ConnectionPort implements Drawable
    {
        public static final int SIDE_LENGTH = 12;
        private final Rectangle rectangle;

        // ConnectionPort can only be instantiated within the class BasicObject
        private ConnectionPort(Point topLeftCorner)
        {
            rectangle = new Rectangle(topLeftCorner, new Dimension(SIDE_LENGTH, SIDE_LENGTH));
        }

        @Override
        public void draw(Graphics g)
        {
            Graphics2D g2d = (Graphics2D) g;

            g2d.setPaint(Color.DARK_GRAY);
            g2d.fill(rectangle);
        }

        private void translate(int offsetX, int offsetY)
        {
            rectangle.translate(offsetX, offsetY);
        }

        public double getCenterX()
        {
            return rectangle.getCenterX();
        }

        public double getCenterY()
        {
            return rectangle.getCenterY();
        }
    }

    protected final ConnectionPort[] ports = new ConnectionPort[4];
    protected enum BasicObjectPort {TOP, BOTTOM, LEFT, RIGHT}
    protected static final Hashtable<TextAttribute, Object> attributes = new Hashtable<>();

    static
    {
        attributes.put(TextAttribute.FAMILY, "Serif");
        attributes.put(TextAttribute.SIZE, 18);
    }

    protected BasicObject(Point unselectedTopLeftCorner)
    {
        super(true, false, unselectedTopLeftCorner);

        ports[BasicObjectPort.TOP.ordinal()] = new ConnectionPort(new Point(this.unselectedTopLeftCorner.x + getUnselectedWidth() / 2 - ConnectionPort.SIDE_LENGTH / 2, this.unselectedTopLeftCorner.y - ConnectionPort.SIDE_LENGTH / 2));
        ports[BasicObjectPort.BOTTOM.ordinal()] = new ConnectionPort(new Point(this.unselectedTopLeftCorner.x + getUnselectedWidth() / 2 - ConnectionPort.SIDE_LENGTH / 2, this.unselectedTopLeftCorner.y + getUnselectedHeight() - ConnectionPort.SIDE_LENGTH / 2));
        ports[BasicObjectPort.LEFT.ordinal()] = new ConnectionPort(new Point(this.unselectedTopLeftCorner.x - ConnectionPort.SIDE_LENGTH / 2, this.unselectedTopLeftCorner.y + getUnselectedHeight() / 2 - ConnectionPort.SIDE_LENGTH / 2));
        ports[BasicObjectPort.RIGHT.ordinal()] = new ConnectionPort(new Point(this.unselectedTopLeftCorner.x + getUnselectedWidth() - ConnectionPort.SIDE_LENGTH / 2, this.unselectedTopLeftCorner.y + getUnselectedHeight() / 2 - ConnectionPort.SIDE_LENGTH / 2));
    }

    @Override
    public Point getSelectedTopLeftCorner()
    {
        return new Point(unselectedTopLeftCorner.x - ConnectionPort.SIDE_LENGTH / 2, unselectedTopLeftCorner.y - ConnectionPort.SIDE_LENGTH / 2);
    }

    @Override
    public int getSelectedWidth()
    {
        return getUnselectedWidth() + ConnectionPort.SIDE_LENGTH;
    }

    @Override
    public int getSelectedHeight()
    {
        return getUnselectedHeight() + ConnectionPort.SIDE_LENGTH;
    }

    @Override
    public Triple<List<ToolElement>, List<AbstractObject>, List<ConnectionLine>> getMembers()
    {
        return null;
    }

    @Override
    public int[] translate(int offsetX, int offsetY)
    {
        int[] offsets = super.translate(offsetX, offsetY);

        for (ConnectionPort port : ports)
        {
            port.translate(offsets[0], offsets[1]);
        }

        return offsets;
    }

    protected void drawName(Graphics g)
    {
        AttributedString attributedName = new AttributedString(name, attributes);

        Graphics2D g2d = (Graphics2D) g;

        // create a new LineBreakMeasurer from the paragraph
        AttributedCharacterIterator paragraph = attributedName.getIterator();
        int paragraphStart = paragraph.getBeginIndex();  // index of the first character in the paragraph
        int paragraphEnd = paragraph.getEndIndex();  // index of the first character after the end of the paragraph
        FontRenderContext frc = g2d.getFontRenderContext();
        /*
        LineBreakMeasurer class enables styled text to be broken into lines so that they fit
        within a particular visual advance. Each line is returned as a TextLayout object,
        which represents unchangeable, styled character data.
         */
        LineBreakMeasurer lineMeasurer = new LineBreakMeasurer(paragraph, frc);  // the LineBreakMeasurer used to line-break the paragraph

        // set break width to width of BasicObject
        float breakWidth = (float) getUnselectedWidth();
        float drawPosY = (float) (unselectedTopLeftCorner.y + ConnectionPort.SIDE_LENGTH / 2);
        // set position to the index of the first character in the paragraph
        lineMeasurer.setPosition(paragraphStart);

        // get lines until the entire paragraph has been displayed
        while (lineMeasurer.getPosition() < paragraphEnd)
        {
            // retrieve next layout
            TextLayout layout = lineMeasurer.nextLayout(breakWidth);

            // compute pen x position (center the name)
            float drawPosX = unselectedTopLeftCorner.x + breakWidth / 2 - layout.getAdvance() / 2;

            // move y-coordinate by the ascent of the layout
            drawPosY += layout.getAscent();

            // draw the TextLayout at (drawPosX, drawPosY)
            layout.draw(g2d, drawPosX, drawPosY);

            // move y-coordinate in preparation for next layout
            drawPosY += layout.getDescent() + layout.getLeading();
        }
    }

    protected void drawPorts(Graphics g)
    {
        for (ConnectionPort port : ports)
        {
            port.draw(g);
        }
    }

    public ConnectionPort[] getPorts()
    {
        return ports;
    }
}