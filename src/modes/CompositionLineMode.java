package modes;

import graphics.CompositionLine;
import graphics.ConnectionLine;

public class CompositionLineMode extends ConnectionLineMode
{
    public static final int NUMBER = 3;

    @Override
    protected ConnectionLine createConcreteConnectionLine()
    {
        if (startPort == endPort)  // allowing Self-Composition but not allowing connections to the same port
        {
            return null;
        }
        return new CompositionLine(startBasicObject, startPort, endBasicObject, endPort);
    }
}