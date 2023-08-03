import umlEditorComponents.MenuBar;
import umlEditorComponents.Model;
import umlEditorComponents.Toolbar;

import javax.swing.*;
import java.awt.*;

public class UMLEditor
{
    private Container createContentPane()
    {
        // By default, a panel's layout manager is an instance of FlowLayout,
        // which places the panel's contents in a row.
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.setOpaque(true);
        /*
        For the drag behavior to work correctly, the toolbar must be in a container
        that uses the BorderLayout layout manager. The component that the toolbar
        affects is generally in the center of the container. The toolbar must be the
        only other component in the container, and it must not be in the center.
         */
        contentPane.add(new Toolbar(), BorderLayout.LINE_START);
        // If the window is enlarged, the center area gets as much of the available space as possible.
        contentPane.add(Model.INSTANCE.getCanvas(), BorderLayout.CENTER);

        return contentPane;
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    private static void createAndShowGUI()
    {
        // Create and set up the window.
        JFrame frame = new JFrame("UML editor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Add content to the window.
        frame.setJMenuBar(new MenuBar());  // add the menu bar to the frame
        UMLEditor editor = new UMLEditor();
        frame.setContentPane(editor.createContentPane());  // add the toolbar and the canvas to the frame

        // Display the window.
        frame.pack();  // size the frame
        frame.setLocationRelativeTo(null);  // center the frame onscreen. To properly center the window, invoke this method after the window size has been set.
        frame.setVisible(true);  // show the frame
    }

    public static void main(String[] args)
    {
        // Schedule a job for the event dispatch thread:
        // creating and showing this application's GUI.
        SwingUtilities.invokeLater(UMLEditor::createAndShowGUI);
    }
}