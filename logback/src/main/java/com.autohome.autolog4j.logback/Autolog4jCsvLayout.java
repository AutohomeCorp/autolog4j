package com.autohome.autolog4j.logback;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.LayoutBase;
import com.autohome.autolog4j.common.FieldName;
import com.google.common.base.Strings;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.QuoteMode;

public class Autolog4jCsvLayout extends LayoutBase<ILoggingEvent> {

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

    private static final ThreadLocal<StringBuilder> threadLocal = new ThreadLocal();
    private static final int DEFAULT_STRING_BUILDER_SIZE = 1024;
    private static final int MAX_STRING_BUILDER_SIZE = 2048;

    private List<LayoutField> fields;

    private String department;

    private String team;

    private String project;

    private String hawkKey;

    private CSVFormat csvFormat;

    public Autolog4jCsvLayout() {
        this.csvFormat = createFormat("Default", '\t', '\\', '\"',
                QuoteMode.ALL, "\"-\"", System.getProperty("line.separator"));
    }

    @Override
    public String doLayout(ILoggingEvent loggingEvent) {
        final StringBuilder buffer = getStringBuilder();
        if (this.fields == null || this.fields.size() == 0) {
            this.fields = getDefaultFields();
        }
        Iterator<LayoutField> fieldIterator = this.fields.iterator();
        try {
            if (fieldIterator.hasNext()) {
                this.csvFormat.print(handleEscapeChar(fieldIterator.next().format(loggingEvent)), buffer, true);
                while (fieldIterator.hasNext()) {
                    this.csvFormat.print(handleEscapeChar(fieldIterator.next().format(loggingEvent)), buffer, false);
                }
            }
            this.csvFormat.println(buffer);
        } catch (IOException ex) {
            return ex.toString();
        }
        return buffer.toString();
    }

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

    private static StringBuilder getStringBuilder() {
        StringBuilder result = (StringBuilder) threadLocal.get();
        if (result == null) {
            result = new StringBuilder(1024);
            threadLocal.set(result);
        }

        trimToMaxSize(result);
        result.setLength(0);
        return result;
    }

    protected static void trimToMaxSize(StringBuilder stringBuilder) {
        if (stringBuilder.length() > MAX_STRING_BUILDER_SIZE) {
            stringBuilder.setLength(MAX_STRING_BUILDER_SIZE);
            stringBuilder.trimToSize();
        }
    }

    private static CSVFormat createFormat(final String format, final Character delimiter, final Character escape,
                                          final Character quote, final QuoteMode quoteMode, final String nullString,
                                          final String recordSeparator) {
        CSVFormat csvFormat = CSVFormat.valueOf(format);
        if (isNotNul(quote)) {
            csvFormat = csvFormat.withQuote(quote);
        }
        if (quoteMode != null) {
            csvFormat = csvFormat.withQuoteMode(quoteMode);
        }
        if (nullString != null) {
            csvFormat = csvFormat.withNullString(nullString);
        }
        if (isNotNul(delimiter)) {
            csvFormat = csvFormat.withDelimiter(delimiter);
        }
        if (isNotNul(escape)) {
            csvFormat = csvFormat.withEscape(escape);
        }
        if (recordSeparator != null) {
            csvFormat = csvFormat.withRecordSeparator(recordSeparator);
        }
        return csvFormat;
    }

    private static boolean isNotNul(final Character character) {
        return character != null && character != 0;
    }

    private String handleEscapeChar(String value) {
        if (Strings.isNullOrEmpty(value) || !value.contains("\\\"")) {
            return value;
        }

        return value.replace("\\\"", "\"");
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

    private LayoutField createProjectField() {
        if (!Strings.isNullOrEmpty(this.project)) {
            this.project = this.project.toLowerCase();
        }
        return new LayoutField("Project", this.project);
    }

    private LayoutField createHawkKeyField() {
        return new LayoutField("HawkKey", this.hawkKey);
    }

    private List<LayoutField> getDefaultFields() {
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
                LayoutField.CUSTOM_MESSAGE,
                LayoutField.STACK_TRACE,
                createHawkKeyField()
        );
    }
}
