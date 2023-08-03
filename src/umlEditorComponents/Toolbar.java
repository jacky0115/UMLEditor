package umlEditorComponents;

import modes.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.URL;

public class Toolbar extends JToolBar implements ActionListener, ItemListener
{
    public Toolbar()
    {
        super("Tools", VERTICAL);

        JRadioButton radioButton;
        ButtonGroup group = new ButtonGroup();

        radioButton = createRadioButton("select.png", SelectMode.NUMBER);
        group.add(radioButton);
        add(radioButton);

        radioButton = createRadioButton("association_line.png", AssociationLineMode.NUMBER);
        group.add(radioButton);
        add(radioButton);

        radioButton = createRadioButton("generalization_line.png", GeneralizationLineMode.NUMBER);
        group.add(radioButton);
        add(radioButton);

        radioButton = createRadioButton("composition_line.png", CompositionLineMode.NUMBER);
        group.add(radioButton);
        add(radioButton);

        radioButton = createRadioButton("class.png", ClassMode.NUMBER);
        group.add(radioButton);
        add(radioButton);

        radioButton = createRadioButton("use_case.png", UseCaseMode.NUMBER);
        group.add(radioButton);
        add(radioButton);
    }

    private JRadioButton createRadioButton(String imgFilename, int modeNumber)
    {
        // get filename without extension and replace underscores with spaces
        String modeString = imgFilename.replaceFirst("\\.[^.]+$", "").replace("_", " ");

        URL imgURL = Toolbar.class.getResource("images/" + imgFilename);

        // create and initialize the radio button
        JRadioButton radioButton = new JRadioButton();
        radioButton.setBackground(Color.WHITE);
        radioButton.setBorderPainted(true);
        radioButton.setToolTipText(modeString);
        radioButton.setActionCommand(String.valueOf(modeNumber));
        radioButton.addActionListener(this);
        radioButton.addItemListener(this);
        if (imgURL != null)  // image found
        {
            radioButton.setIcon(new ImageIcon(imgURL, modeString));
        }
        else  // no image found
        {
            radioButton.setText(modeString);
            System.err.println("Resource not found: images/" + imgFilename);
        }

        return radioButton;
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        int modeNumber = Integer.parseInt(e.getActionCommand());

        switch (modeNumber)
        {
            case SelectMode.NUMBER -> Model.INSTANCE.setMode(new SelectMode(), SelectMode.NUMBER);
            case AssociationLineMode.NUMBER -> Model.INSTANCE.setMode(new AssociationLineMode(), AssociationLineMode.NUMBER);
            case GeneralizationLineMode.NUMBER -> Model.INSTANCE.setMode(new GeneralizationLineMode(), GeneralizationLineMode.NUMBER);
            case CompositionLineMode.NUMBER -> Model.INSTANCE.setMode(new CompositionLineMode(), CompositionLineMode.NUMBER);
            case ClassMode.NUMBER -> Model.INSTANCE.setMode(new ClassMode(), ClassMode.NUMBER);
            case UseCaseMode.NUMBER -> Model.INSTANCE.setMode(new UseCaseMode(), UseCaseMode.NUMBER);
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e)
    {
        JRadioButton radioButton = (JRadioButton) e.getItem();

        if (e.getStateChange() == ItemEvent.SELECTED)
        {
            radioButton.setBackground(Color.GRAY);
        }
        else if (e.getStateChange() == ItemEvent.DESELECTED)
        {
            radioButton.setBackground(Color.WHITE);
        }
    }
}