package com.autohome.autolog4j.logback;


import java.io.IOException;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.autohome.autolog4j.common.FillMdcFilter;
import com.google.common.base.Strings;
import org.slf4j.MDC;

public class LogbackMdcFilter extends FillMdcFilter {


    @Override
    protected void fillMdc(Map<String, String> map, HttpServletRequest httpServletRequest,
                           HttpServletResponse httpServletResponse,
                           FilterChain filterChain) throws IOException, ServletException {
        try {
            fillMdc(map);
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } finally {
            clearMdc();
        }
    }

    private void fillMdc(Map<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String value = MDC.get(entry.getKey());
            if (Strings.isNullOrEmpty(value)) {
                MDC.put(entry.getKey(), entry.getValue());
            }
        }
    }

    private void clearMdc() {
        MDC.clear();
    }
}
