package com.autohome.autolog4j.log4j1x;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Created by kcq on 2018/2/5.
 */
public class Autolog4jJsonLayout extends Layout {
    private final static ObjectMapper objectMapper = new ObjectMapper();

    private String team = "unknown";
    private String department = "unknown";
    private String project = "unknown";
    private String hawkKey;

    private String lineSeparator = System.getProperty("line.separator");

    private List<LayoutField> fields;

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getHawkKey() {
        return hawkKey;
    }

    public void setHawkKey(String hawkKey) {
        this.hawkKey = hawkKey;
    }

    @Override
    public String format(LoggingEvent loggingEvent) {
        if (this.fields == null || this.fields.size() == 0) {
            this.fields = getDefaultFields();
        }
        Map<String, Object> map = new HashMap<>();
        for (LayoutField layoutField : fields) {
            if (layoutField.getName().equals(LayoutField.MESSAGE.getName())) {
                map.putAll(parseCustomMessage2Map(loggingEvent));
            } else {
                String val = layoutField.format(loggingEvent);
                if (!Strings.isNullOrEmpty(val)) {
                    map.put(layoutField.getName(), val);
                }
            }
        }
        try {
            return objectMapper.writeValueAsString(map) + lineSeparator;
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public boolean ignoresThrowable() {
        return false;
    }

    @Override
    public void activateOptions() {

    }

    protected LayoutField createDepartmentField() {
        if (!Strings.isNullOrEmpty(this.department)) {
            this.department = this.department.toLowerCase();
        }
        return new LayoutField("Department", this.department);
    }

    protected LayoutField createTeamField() {
        if (!Strings.isNullOrEmpty(this.team)) {
            this.team = this.team.toLowerCase();
        }
        return new LayoutField("Team", this.team);
    }

    protected LayoutField createProjectField() {
        if (!Strings.isNullOrEmpty(this.project)) {
            this.project = this.project.toLowerCase();
        }
        return new LayoutField("Project", this.project);
    }

    protected LayoutField createHawkKeyField() {
        return new LayoutField("HawkKey", this.hawkKey);
    }

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
                LayoutField.LEVEL,
                LayoutField.CLASS_NAME,
                LayoutField.METHOD_NAME,
                LayoutField.METHOD_PARAMS,
                LayoutField.LINE,
                LayoutField.LOGGER,
                LayoutField.IO_TYPE,
                LayoutField.EXCEPTION_TYPE,
                LayoutField.EXCEPTION_MESSAGE,
                LayoutField.MESSAGE,
                LayoutField.STACK_TRACE,
                createHawkKeyField()
        );
    }

    protected Map<String, Object> parseCustomMessage2Map(LoggingEvent loggingEvent) {
        Map<String, Object> map = new HashMap<>();
        String customMessage = LayoutField.MESSAGE.format(loggingEvent);
        if (Strings.isNullOrEmpty(customMessage)) {
            return map;
        }
        map.put("CustomMessage", customMessage);
        return map;
    }
}
