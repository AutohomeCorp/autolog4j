package com.autohome.autolog4j.log4j2x;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.autohome.autolog4j.common.FieldName;
import com.autohome.autolog4j.common.JacksonUtil;
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
import org.apache.logging.log4j.core.layout.AbstractStringLayout;
import org.apache.logging.log4j.core.layout.PatternLayout;

/**
 * Created by kcq on 2018/2/5.
 */
@Plugin(name = "Autolog4jJsonLayout",
        category = Node.CATEGORY,
        elementType = Layout.ELEMENT_TYPE,
        printObject = true)
public class Autolog4jJsonLayout extends AbstractStringLayout {
    protected static final String DEFAULT_CHARSET = "UTF-8";
    private String lineSeparator = System.getProperty("line.separator");

    private static final List<String> defaultFieldNames = Arrays.asList(FieldName.LOG_AT,
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
            FieldName.LEVEL,
            FieldName.CLASS_NAME,
            FieldName.METHOD_NAME,
            FieldName.METHOD_PARAMS,
            FieldName.LINE,
            FieldName.LOGGER,
            FieldName.IO_TYPE,
            FieldName.EXCEPTION_TYPE,
            FieldName.EXCEPTION_MESSAGE,
            FieldName.CUSTOM_MESSAGE,
            FieldName.STACK_TRACE,
            FieldName.HAWK_KEY
    );
    private String team;
    private String department;
    private String project;
    private String hawkKey;
    private List<LayoutField> fields;
    private FieldFactory fieldFactory;

    protected Autolog4jJsonLayout(final Configuration config,
                                  final String header,
                                  final String footer,
                                  String department, String team, String project, String hawkKey,
                                  FieldFactory fieldFactoryIn) {
        super(config, Charset.forName(DEFAULT_CHARSET),
                PatternLayout.newSerializerBuilder().setConfiguration(config).setPattern(header).build(),
                PatternLayout.newSerializerBuilder().setConfiguration(config).setPattern(footer).build());

        this.department = department;
        this.team = team;
        this.project = project;
        this.hawkKey = hawkKey;
        if (fieldFactoryIn == null) {
            this.fieldFactory = new DefaultLog4jFieldFactory();
        } else {
            this.fieldFactory = fieldFactoryIn;
        }
    }

    @PluginFactory
    public static Autolog4jJsonLayout createLayout(@PluginConfiguration final Configuration config,
                                                   @PluginAttribute("department") final String department,
                                                   @PluginAttribute("team") final String team,
                                                   @PluginAttribute("project") final String project,
                                                   @PluginAttribute("hawkKey") final String hawkKey,
                                                   @PluginElement("FieldFactory") final FieldFactory fieldFactory) {
        return new Autolog4jJsonLayout(config, null, null, department, team, project, hawkKey, fieldFactory);
    }

    @Override
    public String toSerializable(LogEvent event) {
        if (this.fields == null || this.fields.size() == 0) {
            this.fields = getDefaultFields();
        }

        Map<String, Object> map = new HashMap<>();
        for (LayoutField layoutField : fields) {
            if (layoutField.getName().equals(LayoutField.CUSTOM_MESSAGE.getName())) {
                map.putAll(parseCustomMessage2Map(event));
            } else {
                String val = layoutField.format(event);
                if (!Strings.isNullOrEmpty(val)) {
                    map.put(layoutField.getName(), val);
                }
            }
        }
        return JacksonUtil.serialize(map) + lineSeparator;
    }

    private LayoutField createProjectField() {
        if (!Strings.isNullOrEmpty(this.project)) {
            this.project = this.project.toLowerCase();
        }
        return new LayoutField("Project", this.project);
    }

    private LayoutField createDepartmentField() {
        if (!Strings.isNullOrEmpty(this.department)) {
            this.department = this.department.toLowerCase();
        }
        return new LayoutField("Department", this.department);
    }

    private LayoutField createTeamField() {
        if (!Strings.isNullOrEmpty(this.team)) {
            this.team = this.team.toLowerCase();
        }
        return new LayoutField("Team", this.team);
    }

    private LayoutField createHawkKeyField() {
        return new LayoutField("HawkKey", this.hawkKey);
    }

    private List<LayoutField> getDefaultFields() {
        List<LayoutField> fields = new ArrayList<>();

        for (String fieldName : defaultFieldNameList()) {
            switch (fieldName) {
                case FieldName.PROJECT:
                    fields.add(createProjectField());
                    break;
                case FieldName.DEPARTMENT:
                    fields.add(createDepartmentField());
                    break;
                case FieldName.TEAM:
                    fields.add(createTeamField());
                    break;
                case FieldName.HAWK_KEY:
                    fields.add(createHawkKeyField());
                    break;
                default:
                    fields.add(this.fieldFactory.fetchField(fieldName));
                    break;
            }
        }

        return fields;
    }

    protected List<String> defaultFieldNameList() {
        return defaultFieldNames;
    }

    protected Map<String, Object> parseCustomMessage2Map(LogEvent loggingEvent) {
        Map<String, Object> map = new HashMap<>();
        String customMessage = LayoutField.CUSTOM_MESSAGE.format(loggingEvent);
        if (Strings.isNullOrEmpty(customMessage)) {
            map.put(FieldName.CUSTOM_MESSAGE, "");
        } else {
            map.put(FieldName.CUSTOM_MESSAGE, customMessage);
        }

        return map;
    }
}
