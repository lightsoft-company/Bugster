package ru.lightsoft.bugster;

import org.slf4j.MDC;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;

import static com.google.common.collect.Maps.newHashMap;

/**
 * Получает данные из запроса и добавляет их в MDC
 */
public class RequestParamsCollector {
    public void collectParams(HttpServletRequest req) {
        Map<String, String[]> params = req.getParameterMap();
        Map<String, String> cookies = getCookies(req);
        Map<String, String[]> requestParams = getRequestParams(params, cookies);

        Map<String, Object> normalizedParams = normalizeParams(params);

        String paramsStr = Utils.print(normalizedParams);
        if (Objects.equals(req.getMethod(), "POST")) {
            MDC.put(Utils.POST, paramsStr);
        } else if (Objects.equals(req.getMethod(), "GET")) {
            MDC.put(Utils.GET, paramsStr);
        }

        MDC.put(Utils.REQUEST, Utils.print(normalizeParams(requestParams)));
        MDC.put(Utils.COOKIE, Utils.print(cookies));

        MDC.put(Utils.REQUEST_URI, req.getRequestURI() + "?" + req.getQueryString());
        MDC.put(Utils.HTTP_HOST, req.getRemoteHost());
        MDC.put(Utils.REQUEST_METHOD, req.getMethod());
        MDC.put(Utils.QUERY_STRING, req.getQueryString());

        MDC.put(Utils.REFERER, noNull(req.getHeader("referer")));
    }

    /**
     * Преобразует значение параметра из массива в строку, если всего одно значение
     */
    private Map<String, Object> normalizeParams(Map<String, String[]> params) {
        Map<String, Object> res = newHashMap();

        for (Map.Entry<String, String[]> entry : params.entrySet()) {
            if (entry.getValue().length > 1) {
                res.put(entry.getKey(), entry.getValue());
            } else {
                res.put(entry.getKey(), entry.getValue()[0]);
            }
        }

        return res;
    }

    private Map<String, String[]> getRequestParams(Map<String, String[]> params, Map<String, String> cookies) {
        Map<String, String[]> result = newHashMap(params);

        for (Map.Entry<String, String> cookie : cookies.entrySet()) {
            result.put(cookie.getKey(), new String[]{cookie.getValue()});
        }

        return result;
    }

    private Map<String, String> getCookies(HttpServletRequest req) {
        Map<String, String> result = newHashMap();

        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                result.put(cookie.getName(), cookie.getValue());
            }
        }

        return result;
    }

    private String noNull(String s) {
        if (s == null) return "";
        return s;
    }
}