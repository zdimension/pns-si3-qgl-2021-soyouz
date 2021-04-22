package fr.unice.polytech.si3.qgl.soyouz;

import fr.unice.polytech.si3.qgl.soyouz.classes.utilities.Util;

import java.util.Queue;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

class ListLogHandler extends Handler
{
    private final SimpleFormatter fmt = new SimpleFormatter();
    private final Queue<String> logList;

    public ListLogHandler(Queue<String> logList)
    {
        this.logList = logList;
        Util.currentLogLevel = Level.CONFIG;
    }

    @Override
    public void publish(LogRecord record)
    {
        var str = fmt.format(record);
        if (record.getLevel().intValue() < Util.currentLogLevel.intValue())
            return;
        System.out.print(str);
        logList.add(str);
    }

    @Override
    public void flush()
    {
        //Not necessary to implement but you know.. override..
    }

    @Override
    public void close()
    {
        //Not necessary to implement but you know.. override..
    }
}
