package bo.com.skysoft.core.server.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Created by Ricardo Laredo on 19-04-18.
 */
@Slf4j
public class EchoPostHandler implements HttpHandler {



    public EchoPostHandler() {

    }

    @Override
    public void handle(HttpExchange he) throws IOException {

    }



}
