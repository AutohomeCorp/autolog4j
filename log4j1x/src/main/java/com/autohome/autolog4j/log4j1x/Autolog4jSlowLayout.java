package com.autohome.autolog4j.log4j1x;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Created by kcq on 2018/2/6.
 */
public class Autolog4jSlowLayout extends Autolog4jJsonLayout {

    @Override
    protected List<LayoutField> getDefaultFields() {
        return Arrays.asList(
                LayoutField.LOG_AT,
                LayoutField.TRACE_ID,
                createDepartmentField(),
                createTeamField(),
                createProjectField(),
                LayoutField.HOST,
                LayoutField.SERVER_IP,
                LayoutField.CONTEXT_PATH,
                LayoutField.URI_STEM,
                LayoutField.QUERY_STRING,
                LayoutField.FORM_STRING,
                LayoutField.USER_AGENT,
                LayoutField.MESSAGE,
                createHawkKeyField()
        );
    }

    @Override
    protected Map<String, Object> parseCustomMessage2Map(LoggingEvent loggingEvent) {
        Map<String, Object> map = new HashMap<>();
        String customMessage = LayoutField.MESSAGE.format(loggingEvent);
        if (Strings.isNullOrEmpty(customMessage)) {
            return map;
        }
        map.putAll(Splitter.on(",")
                .withKeyValueSeparator("=")
                .split(customMessage));
        return map;
    }
}
