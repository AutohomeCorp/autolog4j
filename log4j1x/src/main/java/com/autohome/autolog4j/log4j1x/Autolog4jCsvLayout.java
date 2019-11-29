package com.autohome.autolog4j.log4j1x;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.google.common.base.Strings;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.QuoteMode;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Created by kcq on 2017/3/10.
 */
public class Autolog4jCsvLayout extends Layout {
    private String team = "unknown";
    private String department = "unknown";
    private String project = "unknown";
    private String hawkKey = "";
    private String separator = "\t";
    private String placeholder = "\"-\"";

    protected static final String DEFAULT_FORMAT = "Default";
    private CSVFormat csvFormat;

    protected final int bufSize;
    protected final int maxCapacity;
    private StringBuffer sbuf;

    private List<LayoutField> fields;

    public Autolog4jCsvLayout() {
        this.bufSize = 1024;
        this.maxCapacity = 2048;
        this.sbuf = new StringBuffer(this.bufSize);

        this.csvFormat = createFormat(DEFAULT_FORMAT, '\t', '\\', '\"',
                QuoteMode.ALL, "\"-\"", System.getProperty("line.separator"));
    }

    @Override
    public String format(LoggingEvent loggingEvent) {
        if (this.fields == null || this.fields.size() == 0) {
            this.fields = getDefaultFields();
        }
        if (this.sbuf.capacity() > this.maxCapacity) {
            this.sbuf = new StringBuffer(this.bufSize);
        } else {
            this.sbuf.setLength(0);
        }

        Iterator<LayoutField> fieldIterator = this.fields.iterator();
        try {
            if (fieldIterator.hasNext()) {
                this.csvFormat.print(handleEscapeChar(fieldIterator.next().format(loggingEvent)), this.sbuf, true);
                while (fieldIterator.hasNext()) {
                    this.csvFormat.print(handleEscapeChar(fieldIterator.next().format(loggingEvent)), this.sbuf, false);
                }
            }
            this.csvFormat.println(this.sbuf);
        } catch (IOException ex) {
            return ex.toString();
        }
        return this.sbuf.toString();
    }

    @Override
    public boolean ignoresThrowable() {
        return false;
    }

    @Override
    public void activateOptions() {
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
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

    public List<LayoutField> getFields() {
        return fields;
    }

    public void setFields(List<LayoutField> fields) {
        this.fields = fields;
    }

    private String handleEscapeChar(String value) {
        if (Strings.isNullOrEmpty(value) || !value.contains("\\\"")) {
            return value;
        }

        return value.replace("\\\"", "\"");
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
                LayoutField.MESSAGE,
                LayoutField.STACK_TRACE,
                createHawkKeyField()
        );
    }

    private static CSVFormat createFormat(final String format, final Character delimiter, final Character escape,
                                          final Character quote, final QuoteMode quoteMode, final String nullString,
                                          final String recordSeparator) {
        CSVFormat csvFormat = CSVFormat.valueOf(format);
        if (isNotNul(delimiter)) {
            csvFormat = csvFormat.withDelimiter(delimiter);
        }
        if (isNotNul(escape)) {
            csvFormat = csvFormat.withEscape(escape);
        }
        if (isNotNul(quote)) {
            csvFormat = csvFormat.withQuote(quote);
        }
        if (quoteMode != null) {
            csvFormat = csvFormat.withQuoteMode(quoteMode);
        }
        if (nullString != null) {
            csvFormat = csvFormat.withNullString(nullString);
        }
        if (recordSeparator != null) {
            csvFormat = csvFormat.withRecordSeparator(recordSeparator);
        }
        return csvFormat;
    }

    private static boolean isNotNul(final Character character) {
        return character != null && character != 0;
    }

    public String getHawkKey() {
        return hawkKey;
    }

    public void setHawkKey(String hawkKey) {
        this.hawkKey = hawkKey;
    }
}
