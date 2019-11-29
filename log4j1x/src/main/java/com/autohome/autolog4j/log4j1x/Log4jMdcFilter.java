package com.autohome.autolog4j.log4j1x;

import java.io.IOException;

import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.autohome.autolog4j.common.FillMdcFilter;
import org.apache.log4j.MDC;


/**
 * Created by fanbaoyin on 2015/10/19.
 */
public class Log4jMdcFilter extends FillMdcFilter {

    private void clearMmc() {
        MDC.clear();
    }

    @Override
    protected void fillMdc(Map<String, String> map, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws IOException, ServletException {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            MDC.put(entry.getKey(), entry.getValue());
        }
        try {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } finally {
            clearMmc();
        }
    }
}
