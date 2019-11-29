package com.autohome.autolog4j.logback;

import java.util.List;
import java.util.stream.Collectors;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import com.google.common.base.Splitter;

public class EnumFilter extends Filter<ILoggingEvent> {

    private String allowLevels;

    private List<Level> allowLevelList;

    public String getAllowLevels() {
        return allowLevels;
    }

    public void setAllowLevels(String allowLevels) {
        this.allowLevels = allowLevels;
        this.allowLevelList = Splitter.on(",").trimResults().splitToList(this.allowLevels)
                .stream().map(Level::toLevel).collect(Collectors.toList());
    }

    @Override
    public FilterReply decide(ILoggingEvent iLoggingEvent) {
        if (allowLevelList.contains(iLoggingEvent.getLevel())) {
            return FilterReply.ACCEPT;
        }
        return FilterReply.DENY;
    }

    public void start() {
        if (this.allowLevelList != null) {
            super.start();
        }
    }
}
