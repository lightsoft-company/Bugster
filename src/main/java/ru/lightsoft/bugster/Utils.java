package ru.lightsoft.bugster;

import ch.qos.logback.classic.pattern.ThrowableProxyConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.core.CoreConstants;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.MDC;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

import static com.google.common.collect.Maps.newHashMap;

/**
 * Доп. методы для обработки ошибок
 */
public class Utils {
    public static final String POST = "post";
    public static final String COOKIE = "cookie";
    public static final String REQUEST = "request";
    public static final String GET = "get";
    public static final String REQUEST_URI = "REQUEST_URI";
    public static final String HTTP_HOST = "HTTP_HOST";
    public static final String REQUEST_METHOD = "REQUEST_METHOD";
    public static final String QUERY_STRING = "QUERY_STRING";
    public static final String REFERER = "REFERER";

    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.101 Safari/537.36";
    public static final String TIME_FORMAT = "dd.MM.yyyy H:m:s";
    private static final LsDateFormatter dateTimeFormatter = LsDateFormatter.getInstance(TIME_FORMAT);
    private static ObjectMapper mapper = new ObjectMapper();

    public static Map<String, Object> collectExtraInfo(ILoggingEvent event) {
        Map<String, Object> info = newHashMap();

        try {
            if (MDC.get(REQUEST) != null) {
                info.put(REQUEST, mapper.readValue(MDC.get(REQUEST), Map.class));
            }

            if (MDC.get(GET) != null) {
                info.put(GET, mapper.readValue(MDC.get(GET), Map.class));
            }

            if (MDC.get(POST) != null) {
                info.put(POST, mapper.readValue(MDC.get(POST), Map.class));
            }

            if (MDC.get(COOKIE) != null) {
                info.put(COOKIE, mapper.readValue(MDC.get(COOKIE), Map.class));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (MDC.get(REQUEST_URI) != null) {
            Map<String, String> server = newHashMap();
            server.put(REQUEST_URI, MDC.get(REQUEST_URI));
            server.put(HTTP_HOST, MDC.get(HTTP_HOST));
            server.put(REQUEST_METHOD, MDC.get(REQUEST_METHOD));
            server.put(QUERY_STRING, MDC.get(QUERY_STRING));
            server.put(REFERER, MDC.get(REFERER));
            info.put("server", server);
        }

        Date time = new Date(new Timestamp(event.getTimeStamp()).getTime());

        info.put("logger", event.getLoggerName());
        info.put("time", dateTimeFormatter.format(time));
        info.put("level", event.getLevel().levelStr);

        return info;
    }

    public static String print(Object value) {
        try {
            return mapper.writeValueAsString(value);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String formatStackTrace(ILoggingEvent event) {

        StringBuilder sb = new StringBuilder(128);
        IThrowableProxy proxy = event.getThrowableProxy();
        if (proxy != null) {
            ThrowableProxyConverter converter = new ThrowableProxyConverter();
            converter.setOptionList(Collections.singletonList("full"));
            converter.start();
            sb.append(converter.convert(event));
            sb.append(CoreConstants.LINE_SEPARATOR);
        }
        return sb.toString();
    }

    public static boolean httpPost(String url, String httpParams, HashMap<String, String> headers) throws Exception {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(url);

        // add header
        post.setHeader("User-Agent", USER_AGENT);
        post.setHeader("Accept-Language", "en-US,en;q=0.5");

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            post.setHeader(entry.getKey(), entry.getValue());
        }

        post.setEntity(new StringEntity(httpParams, "UTF-8"));

        HttpResponse response = client.execute(post);

        return response.getStatusLine().getStatusCode() == 200;
    }
}