package com.autohome.autolog4j.log4j2x;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.autohome.autolog4j.common.FieldName;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

/**
 * Created by kcq on 2018/2/6.
 */
@Plugin(name = "Autolog4jSlowLayout",
        category = Node.CATEGORY,
        elementType = Layout.ELEMENT_TYPE,
        printObject = true)
public class Autolog4jSlowLayout extends Autolog4jJsonLayout {

    protected Autolog4jSlowLayout(Configuration config,
                                  String header, String footer,
                                  String department, String team, String project, String hawkKey,
                                  FieldFactory fieldFactoryIn) {
        super(config, header, footer, department, team, project, hawkKey, fieldFactoryIn);
    }

    @PluginFactory
    public static Autolog4jSlowLayout createLayout(@PluginConfiguration final Configuration config,
                                                   @PluginAttribute("department") final String department,
                                                   @PluginAttribute("team") final String team,
                                                   @PluginAttribute("project") final String project,
                                                   @PluginAttribute("hawkKey") final String hawkKey,
                                                   @PluginElement("FieldFactory") final FieldFactory fieldFactory) {
        return new Autolog4jSlowLayout(config, null, null, department, team, project, hawkKey, fieldFactory);
    }

    @Override
    protected List<String> defaultFieldNameList() {
        return Arrays.asList(
                FieldName.LOG_AT,
                FieldName.TRACE_ID,
                FieldName.DEPARTMENT,
                FieldName.TEAM,
                FieldName.PROJECT,
                FieldName.HOST,
                FieldName.SERVER_IP,
                FieldName.CONTEXT_PATH,
                FieldName.URI_STEM,
                FieldName.QUERY_STRING,
                FieldName.FORM_STRING,
                FieldName.USER_AGENT,
                FieldName.CUSTOM_MESSAGE,
                FieldName.HAWK_KEY
        );
    }

    @Override
    protected Map<String, Object> parseCustomMessage2Map(LogEvent loggingEvent) {
        Map<String, Object> map = new HashMap<>();
        String customMessage = LayoutField.CUSTOM_MESSAGE.format(loggingEvent);
        if (Strings.isNullOrEmpty(customMessage)) {
            return map;
        }
        map.putAll(Splitter.on(",")
                .withKeyValueSeparator("=")
                .split(customMessage));

        return map;
    }
}
