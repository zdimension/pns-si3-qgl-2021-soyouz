package fr.unice.polytech.si3.qgl.soyouz;

import java.util.Queue;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

class ListLogHandler extends Handler
{
    private final SimpleFormatter fmt = new SimpleFormatter();
    private final Queue<String> logList;

    public ListLogHandler(Queue<String> logList)
    {
        this.logList = logList;
    }

    @Override
    public void publish(LogRecord record)
    {
        logList.add(fmt.format(record));
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
