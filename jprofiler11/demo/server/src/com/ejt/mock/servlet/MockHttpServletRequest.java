package com.ejt.mock.servlet;

import org.jetbrains.annotations.NotNull;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Enumeration;

@SuppressWarnings("deprecation")
public class MockHttpServletRequest extends MockServletRequest implements HttpServletRequest {

    private String requestURI;
    private String queryString;
    private MockSession session;

    public MockHttpServletRequest(@NotNull String requestURI, @NotNull String queryString) {
        this(requestURI, queryString, new MockSession());
    }

    public MockHttpServletRequest(@NotNull String requestURI, @NotNull String queryString, @NotNull MockSession session) {
        this.requestURI = requestURI;
        this.queryString = queryString;
        this.session = session;
    }

    @Override
    public String getRemoteAddr() {
        String ip = session.getIp();
        if (ip != null) {
            return ip;
        } else {
            return super.getRemoteAddr();
        }
    }

    @Override
    public String getRequestURI() {
        return requestURI;
    }

    @Override
    public String getQueryString() {
        return queryString;
    }

    @Override
    public String getAuthType() {
        return null;
    }

    @Override
    public Cookie[] getCookies() {
        return new Cookie[0];
    }

    @Override
    public long getDateHeader(String s) {
        return 0;
    }

    @Override
    public String getHeader(String s) {
        return null;
    }

    @Override
    public Enumeration getHeaders(String s) {
        return null;
    }

    @Override
    public Enumeration getHeaderNames() {
        return null;
    }

    @Override
    public int getIntHeader(String name) {
        String value = getHeader(name);
        if (value == null) {
            return -1;
        }

        return Integer.parseInt(value);
    }

    @Override
    public String getMethod() {
        return "GET";
    }

    @Override
    public String getPathInfo() {
        return null;
    }

    @Override
    public String getPathTranslated() {
        return null;
    }

    @Override
    public String getContextPath() {
        return "/myapp";
    }

    @Override
    public String getRemoteUser() {
        return null;
    }

    @Override
    public boolean isUserInRole(String s) {
        return false;
    }

    @Override
    public Principal getUserPrincipal() {
        return null;
    }

    @Override
    public String getRequestedSessionId() {
        return null;
    }

    @Override
    public StringBuffer getRequestURL() {
        StringBuffer buffer = new StringBuffer("http://test.com");
        buffer.append(requestURI);
        if (queryString != null && !queryString.isEmpty()) {
            buffer.append("/?");
            buffer.append(queryString);
        }
        return buffer;
    }

    @Override
    public String getServletPath() {
        return "";
    }

    @Override
    public HttpSession getSession(boolean b) {
        return session;
    }

    @Override
    public HttpSession getSession() {
        return session;
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        return true;
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        return true;
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromUrl() {
        return false;
    }

}
