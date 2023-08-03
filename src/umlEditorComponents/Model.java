package umlEditorComponents;

import containers.Triple;
import graphics.*;
import modes.AssociationLineMode;
import modes.CompositionLineMode;
import modes.GeneralizationLineMode;
import modes.SelectMode;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.*;

/**
 * Represents the Model in the MVC architecture.
 * This enum encapsulates the data and business logic of the UML editor application.
 * It follows the Singleton design pattern using the enum singleton approach.
 *
 * <p>Enum singletons have the following characteristics:</p>
 * <ol>
 *   <li>By default, enums do not support lazy loading.</li>
 *   <li>If we decide to convert the singleton to multi-ton in the future, an enum would not allow this.</li>
 *   <li>Enum singletons are thread-safe by design.</li>
 *   <li>They provide serialization and deserialization safety.</li>
 *   <li>They are immune to reflection attacks.</li>
 * </ol>
 */
public enum Model
{
    /*
    1. Enum class have a static `values` method that returns an array containing all the
    values of the enum in the order they are declared.
    2. All enums implicitly extend java.lang.Enum. Because a class can only extend one parent
    , the Java language does not support multiple inheritance of state, and therefore an enum
    cannot extend anything else.
    3. Java requires that the constants be defined first, prior to any fields or methods.
    Also, when there are fields and methods, the list of enum constants must end with a semicolon.
    4. The constructor for an enum type must be package-private or private access. It
    automatically creates the constants that are defined at the beginning of the enum body.
    You cannot invoke an enum constructor yourself.
     */
    INSTANCE;  // Enum constants are implicitly declared as public static final

    private final CanvasArea canvas = new CanvasArea();  // CanvasArea follows the singleton design pattern by being indirectly controlled and accessed through the Model enum
    // The elements towards the end of the paintedElementsInOrder will be painted later in the CanvasArea's paintComponent method, thus appearing on top of elements painted earlier.
    private final LinkedList<ToolElement> paintedElementsInOrder = new LinkedList<>();
    private final ArrayList<ToolElement> selectedPaintedElements = new ArrayList<>();
    private final LinkedList<AbstractObject> objectsOnCanvas = new LinkedList<>();
    private final ArrayList<AbstractObject> selectedObjects = new ArrayList<>();
    private final LinkedList<ConnectionLine> linesOnCanvas = new LinkedList<>();
    private final ArrayList<ConnectionLine> selectedLines = new ArrayList<>();
    /*
    Note that a Rectangle constructed with the default no-argument constructor will have
    dimensions of 0x0 and therefore be empty. That Rectangle will still have a location
    of (0,0) and will contribute that location to the union and add operations.
    Code attempting to accumulate the bounds of a set of points should therefore initially
    construct the Rectangle with a specifically negative width and height, or it should
    use the first point in the set to construct the Rectangle.
     */
    private final Rectangle repaintArea = new Rectangle(0, 0, -1, -1);
    private MouseInputAdapter mode;
    private int modeNumber;

    public CanvasArea getCanvas()
    {
        return canvas;
    }

    private <E extends ToolElement> void updateSelectedList(List<E> selectedList, List<E> collections)
    {
        selectedList.clear();
        for (E element : collections)
        {
            if (element.isSelected())
            {
                selectedList.add(element);
            }
        }
    }

    private Rectangle getUnselectedBoundsOfSelectedObjects()
    {
        AbstractObject minXSelectedObject = Collections.min(selectedObjects, Comparator.comparingInt((AbstractObject e) -> e.getUnselectedTopLeftCorner().x));
        AbstractObject maxXSelectedObject = Collections.max(selectedObjects, Comparator.comparingInt((AbstractObject e) -> e.getUnselectedTopLeftCorner().x + e.getUnselectedWidth()));
        AbstractObject minYSelectedObject = Collections.min(selectedObjects, Comparator.comparingInt((AbstractObject e) -> e.getUnselectedTopLeftCorner().y));
        AbstractObject maxYSelectedObject = Collections.max(selectedObjects, Comparator.comparingInt((AbstractObject e) -> e.getUnselectedTopLeftCorner().y + e.getUnselectedHeight()));

        int x = minXSelectedObject.getUnselectedTopLeftCorner().x;
        int y = minYSelectedObject.getUnselectedTopLeftCorner().y;
        int width = maxXSelectedObject.getUnselectedTopLeftCorner().x + maxXSelectedObject.getUnselectedWidth() - x;
        int height = maxYSelectedObject.getUnselectedTopLeftCorner().y + maxYSelectedObject.getUnselectedHeight() - y;

        return new Rectangle(x, y, width, height);
    }

    public void addObject(AbstractObject object)
    {
        paintedElementsInOrder.add(object);
        objectsOnCanvas.add(object);
    }

    public void addLine(ConnectionLine line)
    {
        paintedElementsInOrder.add(line);
        linesOnCanvas.add(line);
    }

    void group()
    {
        updateSelectedList(selectedObjects, objectsOnCanvas);
        boolean preconditionForGroup = modeNumber == SelectMode.NUMBER && selectedObjects.size() > 1;

        if (preconditionForGroup)  // meeting the preconditionForGroup means that the ungroup method or the setElementsInSelectionBoxToSelected method has been called before reaching this point
        {
            /*
            A line in selectedLines is a component of the newly created CompositeObject
            only if both of its endpoints are connected to "selected" BasicObjects or the
            CompositeObjects they belong to. In other words, a line must connect two
            "selected" elements to be included as a component of the newly formed CompositeObject.
             */
            updateSelectedList(selectedLines, linesOnCanvas);
            Iterator<ConnectionLine> iterator = selectedLines.iterator();
            ConnectionLine line;
            while (iterator.hasNext())
            {
                line = iterator.next();

                if (!line.getStartBasicObject().isSelected() || !line.getEndBasicObject().isSelected())
                {
                    line.setSelected(false);
                    selectedPaintedElements.remove(line);  // selectedPaintedElements has already been updated during the execution of the ungroup method and setElementsInSelectionBoxToSelected method
                    iterator.remove();
                    line.addToRepaintArea(repaintArea);
                }
            }

            Rectangle unselectedBoundsOfSelectedObjects = getUnselectedBoundsOfSelectedObjects();
            AbstractObject compositeObject = new CompositeObject(
                    unselectedBoundsOfSelectedObjects.getLocation(),
                    unselectedBoundsOfSelectedObjects.width,
                    unselectedBoundsOfSelectedObjects.height,
                    selectedPaintedElements,
                    selectedObjects,
                    selectedLines
            );

            paintedElementsInOrder.removeAll(selectedPaintedElements);
            objectsOnCanvas.removeAll(selectedObjects);
            addObject(compositeObject);
            selectedPaintedElements.clear();
            selectedPaintedElements.add(compositeObject);  // When CompositeObject is created, it is in the "selected" state.
            compositeObject.addToRepaintArea(repaintArea);
            linesOnCanvas.removeAll(selectedLines);

            int clipOffset = 4;
            canvas.repaint(repaintArea.x - clipOffset / 2, repaintArea.y - clipOffset / 2, repaintArea.width + clipOffset, repaintArea.height + clipOffset);

            repaintArea.setBounds(0, 0, -1, -1);
        }
    }

    void ungroup()
    {
        updateSelectedList(selectedObjects, objectsOnCanvas);
        updateSelectedList(selectedLines, linesOnCanvas);
        boolean preconditionForUnGroup = modeNumber == SelectMode.NUMBER && selectedObjects.size() == 1 && selectedLines.isEmpty();

        if (preconditionForUnGroup)
        {
            Triple<List<ToolElement>, List<AbstractObject>, List<ConnectionLine>> members = selectedObjects.get(0).getMembers();

            if (members != null)
            {
                AbstractObject compositeObject = selectedObjects.get(0);

                paintedElementsInOrder.remove(compositeObject);
                paintedElementsInOrder.addAll(members.first());
                selectedPaintedElements.clear();
                selectedPaintedElements.addAll(members.first());
                objectsOnCanvas.remove(compositeObject);
                objectsOnCanvas.addAll(members.second());
                linesOnCanvas.addAll(members.third());

                int clipOffset = 4;
                // paint the area of the component where the compositeObject previously was
                canvas.repaint(compositeObject.getSelectedTopLeftCorner().x - clipOffset / 2, compositeObject.getSelectedTopLeftCorner().y - clipOffset / 2, compositeObject.getSelectedWidth() + clipOffset, compositeObject.getSelectedHeight() + clipOffset);
            }
        }
    }

    void changeObjectName()
    {
        updateSelectedList(selectedObjects, objectsOnCanvas);
        updateSelectedList(selectedLines, linesOnCanvas);
        boolean preconditionForChangeObjectName = modeNumber == SelectMode.NUMBER && selectedObjects.size() == 1 && selectedLines.isEmpty();

        if (preconditionForChangeObjectName)
        {
            AbstractObject object = selectedObjects.get(0);

            String name = (String) JOptionPane.showInputDialog(
                    canvas,
                    "New Name:",
                    "Rename",
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    null,
                    object.getName()
            );

            if (name != null && name.length() > 0 && !name.equals(object.getName()))
            {
                if (object.setName(name))  // true if object is BasicObject and false if it is CompositeObject
                {
                    int clipOffset = 4;
                    canvas.repaint(object.getUnselectedTopLeftCorner().x - clipOffset / 2, object.getUnselectedTopLeftCorner().y - clipOffset / 2, object.getUnselectedWidth() + clipOffset, object.getUnselectedHeight() + clipOffset);
                }
            }

            selectedPaintedElements.clear();
            selectedPaintedElements.add(object);
        }
    }

    void delete()
    {
        // selectedPaintedElements has already been updated in all possible calls to methods before the delete method is invoked
        boolean preconditionForDelete = modeNumber == SelectMode.NUMBER && !selectedPaintedElements.isEmpty();

        if (preconditionForDelete)
        {
            paintedElementsInOrder.removeAll(selectedPaintedElements);
            selectedPaintedElements.clear();

            updateSelectedList(selectedObjects, objectsOnCanvas);
            objectsOnCanvas.removeAll(selectedObjects);  // delete selected objects
            for (AbstractObject object : selectedObjects)
            {
                object.addToRepaintArea(repaintArea);
            }

            updateSelectedList(selectedLines, linesOnCanvas);
            linesOnCanvas.removeAll(selectedLines);  // delete selected lines
            for (ConnectionLine line : selectedLines)
            {
                line.addToRepaintArea(repaintArea);
            }

            // delete unselected lines that are connected to deleted (selected) objects
            /*
            In my previous version of the code, I defined that "regardless of whether a CompositeObject is selected
            or not, its internal members are always unselected." That's why I needed to perform the recursive
            operation below to retrieve all the selected BasicObjects within a selected CompositeObject. However,
            in the latest version, I changed the rule to "the selected state of the members inside a CompositeObject
            should be the same as the selected state of the CompositeObject they belong to." Therefore, to remove the
            unselected lines connected to selected objects, it is sufficient to check whether one end of the line is selected.

            ==================== old version ====================
            // Because ConnectionLine only connects to BasicObject, we have to "recursively" retrieve all BasicObjects in selectedObjects first.
            ListIterator<AbstractObject> objectsIterator = selectedObjects.listIterator();
            AbstractObject object;
            while (objectsIterator.hasNext())
            {
                object = objectsIterator.next();
                Triple<List<ToolElement>, List<AbstractObject>, List<ConnectionLine>> members = object.getMembers();

                if (members != null)
                {
                    objectsIterator.remove();

                    for (AbstractObject subObject : members.second())
                    {
                        objectsIterator.add(subObject);
                        objectsIterator.previous();
                    }
                }
            }

            Iterator<ConnectionLine> linesIterator = linesOnCanvas.iterator();
            ConnectionLine line;
            while (linesIterator.hasNext())
            {
                line = linesIterator.next();

                if (selectedObjects.contains(line.getStartBasicObject()) || selectedObjects.contains(line.getEndBasicObject()))
                {
                    paintedElementsInOrder.remove(line);
                    linesIterator.remove();
                    line.addToRepaintArea(repaintArea);
                }
            }
            ==================== old version ====================
             */
            // ==================== new version ====================
            Iterator<ConnectionLine> iterator = linesOnCanvas.iterator();
            ConnectionLine line;
            while (iterator.hasNext())
            {
                line = iterator.next();

                if (line.getStartBasicObject().isSelected() || line.getEndBasicObject().isSelected())
                {
                    paintedElementsInOrder.remove(line);
                    iterator.remove();
                    line.addToRepaintArea(repaintArea);
                }
            }
            // ==================== new version ====================

            // In order to clear the complete arrow, it is necessary to further expand the repaintArea.
            int clipOffset = 20;
            canvas.repaint(repaintArea.x - clipOffset / 2, repaintArea.y - clipOffset / 2, repaintArea.width + clipOffset, repaintArea.height + clipOffset);

            repaintArea.setBounds(0, 0, -1, -1);
        }
    }

    void setMode(MouseInputAdapter mode, int modeNumber)
    {
        canvas.removeMouseListener(this.mode);
        canvas.removeMouseMotionListener(this.mode);
        this.mode = mode;
        this.modeNumber = modeNumber;
        canvas.addMouseListener(this.mode);
        canvas.addMouseMotionListener(this.mode);

        if (this.modeNumber != SelectMode.NUMBER)
        {
            // selectedPaintedElements has already been updated in all possible calls to methods before the setMode method is invoked
            for (ToolElement element : selectedPaintedElements)
            {
                element.setSelected(false);
                element.addToRepaintArea(repaintArea);
            }
            selectedPaintedElements.clear();

            // In order to draw the complete arrow, it is necessary to further expand the repaintArea.
            int clipOffset = 20;
            canvas.repaint(repaintArea.x - clipOffset / 2, repaintArea.y - clipOffset / 2, repaintArea.width + clipOffset, repaintArea.height + clipOffset);

            repaintArea.setBounds(0, 0, -1, -1);
        }
    }

    // getTopmostHitElement is only used in ConnectionLineMode, so there is no need to update selectedPaintedElements in this method.
    public ToolElement getTopmostHitElement(MouseEvent e)
    {
        Iterator<ToolElement> iterator = paintedElementsInOrder.descendingIterator();
        ToolElement element, topmostHitElement = null;

        while (iterator.hasNext())
        {
            element = iterator.next();

            if (element.isHit(e.getPoint()))
            {
                topmostHitElement = element;
                break;
            }
        }

        return topmostHitElement;
    }

    public ToolElement pressedEventHandler(MouseEvent e)
    {
        ToolElement pressedElement = null;

        if (modeNumber == SelectMode.NUMBER)
        {
            selectedPaintedElements.clear();

            Iterator<ToolElement> iterator = paintedElementsInOrder.descendingIterator();
            ToolElement element;
            while (iterator.hasNext())
            {
                element = iterator.next();

                if (element.isSelected() && (!element.isHit(e.getPoint()) || (element.isHit(e.getPoint()) && pressedElement != null)))
                {
                    element.setSelected(false);
                    element.addToRepaintArea(repaintArea);
                }
                else if (element.isHit(e.getPoint()) && pressedElement == null)
                {
                    pressedElement = element;

                    if (!pressedElement.isSelected())
                    {
                        pressedElement.setSelected(true);
                        pressedElement.addToRepaintArea(repaintArea);
                    }

                    selectedPaintedElements.add(pressedElement);
                    iterator.remove();
                }
            }
            if (pressedElement != null)
            {
                paintedElementsInOrder.add(pressedElement);
            }

            // In order to draw the complete arrow, it is necessary to further expand the repaintArea.
            int clipOffset = 20;
            canvas.repaint(repaintArea.x - clipOffset / 2, repaintArea.y - clipOffset / 2, repaintArea.width + clipOffset, repaintArea.height + clipOffset);

            repaintArea.setBounds(0, 0, -1, -1);
        }
        else if (modeNumber == AssociationLineMode.NUMBER || modeNumber == GeneralizationLineMode.NUMBER || modeNumber == CompositionLineMode.NUMBER)
        {
            pressedElement = getTopmostHitElement(e);
        }

        return pressedElement;
    }

    /*
    private List<AbstractObject> recursivelyGetBasicObjects(AbstractObject target)
    {
        Triple<List<ToolElement>, List<AbstractObject>, List<ConnectionLine>> members = target.getMembers();

        if (members == null)  // target is type of BasicObject
        {
            return Collections.singletonList(target);
        }
        else  // target is type of CompositeObject
        {
            return members.second().stream()
                    .flatMap(object -> recursivelyGetBasicObjects(object).stream())
                    .collect(Collectors.toList());
        }
    }
     */

    // Before the moveObject method, the pressedEventHandler has definitely been called, so there is no need to update selectedPaintedElements in this method.
    public void moveObject(AbstractObject movedObject, int offsetX, int offsetY)
    {
        movedObject.addToRepaintArea(repaintArea);  // repaint the background over the old movedObject location

        movedObject.translate(offsetX, offsetY);
        /*
        In theory, the code for repainting the movedObject at the new location should be added. However, due to the
        small amount of movement between the two mouseDragged events and the sufficiently large clipOffset, it is
        possible to achieve the same effect by expanding the repaintArea only once to include both "repaint the
        background over the old movedObject location" and "repaint the movedObject at the new location."
         */

        // all ConnectionLines connected to the movedObject need to be repainted
        /*
        In my previous version of the code, I defined that "regardless of whether a CompositeObject is
        selected or not, its internal members are always unselected." That's why I needed to perform
        the following recursive operation to retrieve all the selected BasicObjects within a selected
        CompositeObject. However, in the latest version, I changed the rule to "the selected state of
        the members inside a CompositeObject should be the same as the selected state of the
        CompositeObject they belong to." Therefore, to repaint all the lines connected to the
        movedObject (which can be either a BasicObject or a CompositeObject), it is sufficient to
        check whether one end of the line is selected.

        ==================== old version ====================
        List<AbstractObject> movedBasicObjects = recursivelyGetBasicObjects(movedObject);
         */

        for (ConnectionLine line : linesOnCanvas)
        {
            // ==================== old version ====================
            // if (movedBasicObjects.contains(line.getStartBasicObject()) || movedBasicObjects.contains(line.getEndBasicObject()))

            // ==================== new version ====================
            if (line.getStartBasicObject().isSelected() || line.getEndBasicObject().isSelected())
            {
                /*
                Due to the small amount of movement between the two mouseDragged events and the sufficiently large
                clipOffset, it is possible to achieve the desired result by expanding the repaintArea only once,
                including both "repaint the background over the old ConnectionLine location" and "repaint the
                ConnectionLine at the new location."
                 */
                line.addToRepaintArea(repaintArea);
            }
        }

        // In order to draw the complete arrow, it is necessary to further expand the repaintArea.
        int clipOffset = 20;
        canvas.repaint(repaintArea.x - clipOffset / 2, repaintArea.y - clipOffset / 2, repaintArea.width + clipOffset, repaintArea.height + clipOffset);

        repaintArea.setBounds(0, 0, -1, -1);
    }

    // This method does not update the screen; instead, it is left for the mouseReleased method in SelectMode to handle the repaint.
    public List<ToolElement> setElementsInSelectionBoxToSelected(SelectionBox selectionBox)
    {
        // All elements on the CanvasArea are already in the unselected state because the pressedEventHandler method is always called before this method, we don't have to clear selectedPaintedElements at the beginning of this method.
        for (ToolElement element : paintedElementsInOrder)
        {
            if (selectionBox.contains(element))
            {
                element.setSelected(true);
                selectedPaintedElements.add(element);
            }
        }

        paintedElementsInOrder.removeAll(selectedPaintedElements);
        paintedElementsInOrder.addAll(selectedPaintedElements);

        if (selectedPaintedElements.isEmpty())
        {
            return null;
        }
        return List.copyOf(selectedPaintedElements);
    }

    public void dragLine(ToolElement line)
    {
        line.setSelected(false);
        line.addToRepaintArea(repaintArea);

        selectedPaintedElements.clear();

        // In order to draw the complete arrow, it is necessary to further expand the repaintArea.
        int clipOffset = 20;
        canvas.repaint(repaintArea.x - clipOffset / 2, repaintArea.y - clipOffset / 2, repaintArea.width + clipOffset, repaintArea.height + clipOffset);

        repaintArea.setBounds(0, 0, -1, -1);
    }

    LinkedList<ToolElement> getPaintedElementsInOrder()
    {
        return paintedElementsInOrder;
    }
}