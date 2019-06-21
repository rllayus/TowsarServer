package bo.com.skysoft.core.server.http;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by :MC4
 * Autor      :Ricardo Laredo
 * Email      :rlaredo@mc4.com.bo
 * Date       :14-04-19
 * Project    :WebProxy
 * Package    :bo.com.mc4.webproxyapp.server.http
 * Copyright  : MC4
 */
@Slf4j
public class HttpRequest {

    List<String> headers = new ArrayList<>();

    Method method;

    String uri;

    String version;

    public HttpRequest(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String str = reader.readLine();
        parseRequestLine(str);

        while (!str.equals("")) {
            str = reader.readLine();
            parseRequestHeader(str);
        }
    }

    private void parseRequestLine(String str) {
        log.info(str);
        String[] split = str.split("\\s+");
        try {
            method = Method.valueOf(split[0]);
        } catch (Exception e) {
            method = Method.UNRECOGNIZED;
        }
        uri = split[1];
        version = split[2];
    }

    private void parseRequestHeader(String str) {
        log.info(str);
        headers.add(str);
    }
}
