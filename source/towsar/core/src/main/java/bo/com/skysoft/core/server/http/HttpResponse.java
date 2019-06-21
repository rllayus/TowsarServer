package bo.com.skysoft.core.server.http;

/**
 * Created by :MC4
 * Autor      :Ricardo Laredo
 * Email      :rlaredo@mc4.com.bo
 * Date       :14-04-19
 * Project    :WebProxy
 * Package    :bo.com.mc4.webproxyapp.server.http
 * Copyright  : MC4
 */

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
@Slf4j
public class HttpResponse {

    public static final String VERSION = "HTTP/1.0";

    List<String> headers = new ArrayList<String>();

    byte[] body;

    public HttpResponse(HttpRequest req) throws IOException {

        switch (req.method) {
            case HEAD:
                fillHeaders(Status._200);
                break;
            case GET:
                try {
                    // TODO fix dir bug http://localhost:8080/src/test
                    File file = new File("." + req.uri);
                    if (file.isDirectory()) {
                        fillHeaders(Status._200);

                        headers.add(ContentType.HTML.toString());
                        StringBuilder result = new StringBuilder("<html><head><title>Index of ");
                        result.append(req.uri);
                        result.append("</title></head><body><h1>Index of ");
                        result.append(req.uri);
                        result.append("</h1><hr><pre>");

                        // TODO add Parent Directory
                        File[] files = file.listFiles();
                        for (File subfile : files) {
                            result.append(" <a href=\"" + subfile.getPath() + "\">" + subfile.getPath() + "</a>\n");
                        }
                        result.append("<hr></pre></body></html>");
                        fillResponse(result.toString());
                    } else if (file.exists()) {
                        fillHeaders(Status._200);
                        setContentType(req.uri, headers);
                        fillResponse(getBytes(file));
                    } else {
                        log.info("File not found:" + req.uri);
                        fillHeaders(Status._404);
                        fillResponse(Status._404.toString());
                    }
                } catch (Exception e) {
                    log.error("Response Error", e);
                    fillHeaders(Status._400);
                    fillResponse(Status._400.toString());
                }

                break;
            case UNRECOGNIZED:
                fillHeaders(Status._400);
                fillResponse(Status._400.toString());
                break;
            default:
                fillHeaders(Status._501);
                fillResponse(Status._501.toString());
        }

    }

    private byte[] getBytes(File file) throws IOException {
        int length = (int) file.length();
        byte[] array = new byte[length];
        InputStream in = new FileInputStream(file);
        int offset = 0;
        while (offset < length) {
            int count = in.read(array, offset, (length - offset));
            offset += count;
        }
        in.close();
        return array;
    }

    private void fillHeaders(Status status) {
        headers.add(HttpResponse.VERSION + " " + status.toString());
        headers.add("Connection: close");
        headers.add("Server: SimpleWebServer");
    }

    private void fillResponse(String response) {
        body = response.getBytes();
    }

    private void fillResponse(byte[] response) {
        body = response;
    }

    public void write(OutputStream os) throws IOException {
        DataOutputStream output = new DataOutputStream(os);
        for (String header : headers) {
            output.writeBytes(header + "\r\n");
        }
        output.writeBytes("\r\n");
        if (body != null) {
            output.write(body);
        }
        output.writeBytes("\r\n");
        output.flush();
    }

    private void setContentType(String uri, List<String> list) {
        try {
            String ext = uri.substring(uri.indexOf(".") + 1);
            list.add(ContentType.valueOf(ext.toUpperCase()).toString());
        } catch (Exception e) {
            log.error("ContentType not found: " + e, e);
        }
    }
}
