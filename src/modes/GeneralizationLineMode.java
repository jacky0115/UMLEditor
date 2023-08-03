package modes;

import graphics.ConnectionLine;
import graphics.GeneralizationLine;

public class GeneralizationLineMode extends ConnectionLineMode
{
    public static final int NUMBER = 2;

    @Override
    protected ConnectionLine createConcreteConnectionLine()
    {
        if (startBasicObject == endBasicObject)  // not allowed for a BasicObject to inherit from itself
        {
            return null;
        }
        return new GeneralizationLine(startBasicObject, startPort, endBasicObject, endPort);
    }
}