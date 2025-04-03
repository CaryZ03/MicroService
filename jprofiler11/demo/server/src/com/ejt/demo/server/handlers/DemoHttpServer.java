package com.ejt.demo.server.handlers;

import com.ejt.mock.MockCallable;
import com.ejt.mock.MockHelper;
import com.ejt.mock.servlet.MockHttpServletRequest;
import com.ejt.mock.servlet.MockServlet;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

public class DemoHttpServer {
    public static final String PATH = "/exchangeRate";
    public static final int PORT = 7098;

    public static final String PARAMETER_FROM = "from";
    public static final String PARAMETER_TO = "to";

    private static final Executor EXECUTOR = Executors.newCachedThreadPool();

    public static void start() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
            server.createContext(PATH, new HttpHandler() {
                @Override
                public void handle(HttpExchange httpExchange) throws IOException {
                    try {
                        createServlet(httpExchange).service(createServletRequest(httpExchange), null);
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            });
            server.setExecutor(EXECUTOR);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @NotNull
    private static MockServlet createServlet(final HttpExchange httpExchange) {
        return new MockServlet(new MockCallable() {
            @Override
            public void run() throws Exception {
                Map<String, String> parameters = toParameterMap(httpExchange.getRequestURI().getQuery());
                String from = parameters.get(PARAMETER_FROM);
                String response = String.valueOf(lookupExchangeRate(from));
                httpExchange.sendResponseHeaders(200, response.length());
                OutputStream responseBody = httpExchange.getResponseBody();
                responseBody.write(response.getBytes());
                responseBody.close();
            }
        });
    }

    @NotNull
    private static MockHttpServletRequest createServletRequest(final HttpExchange httpExchange) {
        URI requestURI = httpExchange.getRequestURI();
        return new MockHttpServletRequest(requestURI.getPath(), requestURI.getQuery()) {
            @Override
            public String getHeader(String name) {
                return httpExchange.getRequestHeaders().getFirst(name);
            }

            @Override
            public Enumeration getHeaderNames() {
                return Collections.enumeration(httpExchange.getRequestHeaders().keySet());
            }

            @Override
            public Enumeration getHeaders(String name) {
                return Collections.enumeration(httpExchange.getRequestHeaders().get(name));
            }
        };
    }

    private static Map<String, String> toParameterMap(String query) {
        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(entry[0], entry[1]);
            } else {
                result.put(entry[0], "");
            }
        }
        return result;
    }

    private static double lookupExchangeRate(String from) {
        int base;
        switch (from) {
            case "USD":
                base = 50000;
                break;
            case "EUR":
                base = 30000;
                break;
            case "GBP":
                base = 20000;
                break;
            default:
                base = 5000;
                break;
        }
        MockHelper.runnable(base + ThreadLocalRandom.current().nextInt(50000));
        return 1.5;
    }
}
