package umlEditorComponents;

import graphics.ToolElement;
import modes.ConnectionLineMode;
import modes.SelectMode;

import javax.swing.*;
import java.awt.*;

public class CanvasArea extends JPanel
{
    CanvasArea()
    {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(1000, 600));
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        /*
        Most of the standard Swing components have their look and feel implemented by separate UI Delegates.
        This means that most (or all) of the painting for the standard Swing components proceeds as follows.
        1. paint() invokes paintComponent().
        2. If the ui property is non-null, paintComponent() invokes ui.update().
        3. If the component's opaque property is true, ui.update() fills the component's background with the background color and invokes ui.paint().
        4. ui.paint() renders the content of the component.
         */
        super.paintComponent(g);  // passes the graphics context off to the component's UI delegate, which paints the panel's background since this component is opaque

        for (ToolElement element : Model.INSTANCE.getPaintedElementsInOrder())
        {
            element.draw(g);
        }

        // the SelectionBox and TempLine are always drawn above the AbstractObject and ConnectionLine
        SelectMode.getSelectionBox().draw(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setPaint(Color.BLACK);
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.draw(ConnectionLineMode.getTempLine());
    }
}