package br.dbbackup.util;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestLoggerAppender extends AppenderSkeleton {
    private final List<LoggingEvent> list = new ArrayList<>();
    private final StringBuilder text = new StringBuilder();
    private final Pattern pattern;

    public TestLoggerAppender(String regex){
        this.pattern = Pattern.compile(regex);
    }

    @Override
    protected void append(LoggingEvent loggingEvent) {
        Matcher m = pattern.matcher(loggingEvent.getRenderedMessage());

        if(!m.find()){
            return;
        }

        text.append(loggingEvent.getThreadName());
        text.append(" ");
        text.append(loggingEvent.getLevel().toString());
        text.append(" ");
        text.append(loggingEvent.getLogger().getName());
        text.append(" ");
        text.append(loggingEvent.getRenderedMessage());
        text.append("\n");

        list.add(loggingEvent);
    }

    @Override
    public void close() {}

    @Override
    public boolean requiresLayout() {
        return false;
    }

    public List<LoggingEvent> getLogList(){
        return list;
    }

    public String getLogText(){
        return text.toString();
    }
}
