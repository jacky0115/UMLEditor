package modes;

import graphics.AssociationLine;
import graphics.ConnectionLine;

public class AssociationLineMode extends ConnectionLineMode
{
    public static final int NUMBER = 1;

    @Override
    protected ConnectionLine createConcreteConnectionLine()
    {
        if (startPort == endPort)  // allowing Self-Associations but not allowing connections to the same port
        {
            return null;
        }
        return new AssociationLine(startBasicObject, startPort, endBasicObject, endPort);
    }
}