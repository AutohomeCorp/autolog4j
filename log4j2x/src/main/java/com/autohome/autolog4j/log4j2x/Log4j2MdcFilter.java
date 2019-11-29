package com.autohome.autolog4j.log4j2x;

import java.io.IOException;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.autohome.autolog4j.common.FillMdcFilter;
import org.apache.logging.log4j.CloseableThreadContext;

/**
 * Created by kcq on 2017/6/9.
 */
public class Log4j2MdcFilter extends FillMdcFilter {

    @Override
    protected void fillMdc(Map<String, String> map, HttpServletRequest httpServletRequest,
                           HttpServletResponse httpServletResponse, FilterChain filterChain) throws IOException, ServletException {
        CloseableThreadContext.Instance ctc = putAll(map);
        try {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } finally {
            if (ctc != null) {
                ctc.close();
            }
        }
    }

    private CloseableThreadContext.Instance putAll(Map<String, String> mdcMap) {
        CloseableThreadContext.Instance ctc = null;
        for (Map.Entry<String, String> entry : mdcMap.entrySet()) {
            if (ctc == null) {
                ctc = CloseableThreadContext.put(entry.getKey(), entry.getValue());
            }
            ctc.put(entry.getKey(), entry.getValue());
        }
        return ctc;
    }
}
