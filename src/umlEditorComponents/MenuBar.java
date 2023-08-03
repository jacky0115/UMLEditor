package umlEditorComponents;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class MenuBar extends JMenuBar implements ActionListener
{
    public MenuBar()
    {
        JMenu menu;
        JMenuItem menuItem;

        menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);  // pressing the Alt and F keys makes the File menu appear
        add(menu);

        menu = new JMenu("Edit");
        menu.setMnemonic(KeyEvent.VK_E);  // pressing the Alt and E keys makes the Edit menu appear
        add(menu);

        menuItem = new JMenuItem("Group", KeyEvent.VK_G);  // while the Edit menu is visible, pressing the G key (with or without Alt) makes the Group item be chosen
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_DOWN_MASK));  // pressing the Ctrl and G keys in UML editor makes the Group item be chosen, without bringing up any menus
        menuItem.addActionListener(this);
        menu.add(menuItem);

        menuItem = new JMenuItem("UnGroup", KeyEvent.VK_U);  // while the Edit menu is visible, pressing the U key (with or without Alt) makes the UnGroup item be chosen
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.CTRL_DOWN_MASK));  // pressing the Ctrl and U keys in UML editor makes the UnGroup item be chosen, without bringing up any menus
        menuItem.addActionListener(this);
        menu.add(menuItem);

        menuItem = new JMenuItem("Change Object Name", KeyEvent.VK_C);  // while the Edit menu is visible, pressing the C key (with or without Alt) makes the Change Object Name item be chosen
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));  // pressing the Ctrl and C keys in UML editor makes the Change Object Name item be chosen, without bringing up any menus
        menuItem.addActionListener(this);
        menu.add(menuItem);

        menuItem = new JMenuItem("Delete", KeyEvent.VK_D);  // while the Edit menu is visible, pressing the D key (with or without Alt) makes the Delete item be chosen
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.CTRL_DOWN_MASK));  // pressing the Ctrl and D keys in UML editor makes the Delete item be chosen, without bringing up any menus
        menuItem.addActionListener(this);
        menu.add(menuItem);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        JMenuItem menuItem = (JMenuItem) e.getSource();

        if ("Group".equals(menuItem.getText()))
        {
            Model.INSTANCE.group();
        }
        else if ("UnGroup".equals(menuItem.getText()))
        {
            Model.INSTANCE.ungroup();
        }
        else if ("Change Object Name".equals(menuItem.getText()))
        {
            Model.INSTANCE.changeObjectName();
        }
        else if ("Delete".equals(menuItem.getText()))
        {
            Model.INSTANCE.delete();
        }
    }
}