

/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the "License").  You may not use this file except
 * in compliance with the License.
 *
 * You can obtain a copy of the license at
 * glassfish/bootstrap/legal/CDDLv1.0.txt or
 * https://glassfish.dev.java.net/public/CDDLv1.0.html.
 * See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * HEADER in each file and include the License file at
 * glassfish/bootstrap/legal/CDDLv1.0.txt.  If applicable,
 * add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your
 * own identifying information: Portions Copyright [yyyy]
 * [name of copyright owner]
 *
 * Copyright 2005 Sun Microsystems, Inc. All rights reserved.
 *
 * Portions Copyright Apache Software Foundation.
 */


package org.apache.catalina.core;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.IOException;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.security.AccessController;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.security.PrivilegedActionException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.naming.NamingException;
import javax.naming.Binding;
import javax.naming.directory.DirContext;
import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.tomcat.util.buf.MessageBytes;
import org.apache.naming.resources.Resource;
import org.apache.naming.resources.DirContextURLStreamHandler;
import org.apache.naming.resources.DirContextURLConnection;
import org.apache.catalina.Connector;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.Globals;
import org.apache.catalina.Host;
import org.apache.catalina.HttpRequest;
import org.apache.catalina.Logger;
import org.apache.catalina.Response;
import org.apache.catalina.ValveContext;
import org.apache.catalina.Wrapper;
import org.apache.catalina.deploy.ApplicationParameter;
import org.apache.catalina.util.Enumerator;
import org.apache.catalina.util.ResourceSet;
import org.apache.catalina.util.ServerInfo;
import org.apache.catalina.util.StringManager;


/**
 * Dummy request object, used for request dispatcher mapping, as well as
 * JSP precompilation.
 *
 * @author Remy Maucherat
 * @version $Revision: 1.3 $ $Date: 2005/12/08 01:27:34 $
 */

public class DummyRequest
    implements HttpRequest, HttpServletRequest {

    public DummyRequest() {
    }

    public DummyRequest(String contextPath, String decodedURI,
                        String queryString) {
        this.contextPath = contextPath;
        this.decodedURI = decodedURI;
        this.queryString = queryString;
    }

    protected String contextPath = null;
    protected String decodedURI = null;
    protected String queryString = null;

    protected String pathInfo = null;
    protected String servletPath = null;
    protected Wrapper wrapper = null;

    protected FilterChain filterChain = null;
    protected ValveContext valveContext = null;

    // START CR 6415120
    /**
     * Whether or not access to resources in WEB-INF or META-INF needs to be
     * checked.
     */
    protected boolean checkRestrictedResources = true;
    // END CR 6415120

    // START PWC 4707989
    private String method;
    // END PWC 4707989
    
    private static Enumeration dummyEnum = new Enumeration(){
        public boolean hasMoreElements(){
            return false;
        }
        public Object nextElement(){
            return null;
        }
    };

    public String getContextPath() {
        return (contextPath);
    }

    public MessageBytes getContextPathMB() {
        return null;
    }

    public ServletRequest getRequest() {
        return (this);
    }

    public String getDecodedRequestURI() {
        return decodedURI;
    }

    public MessageBytes getDecodedRequestURIMB() {
        return null;
    }

    public FilterChain getFilterChain() {
        return (this.filterChain);
    }

    public void setFilterChain(FilterChain filterChain) {
        this.filterChain = filterChain;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String query) {
        queryString = query;
    }

    public String getPathInfo() {
        return pathInfo;
    }

    public void setPathInfo(String path) {
        pathInfo = path;
    }

    public MessageBytes getPathInfoMB() {
        return null;
    }

    public MessageBytes getRequestPathMB() {
        return null;
    }

    public String getServletPath() {
        return servletPath;
    }

    public void setServletPath(String path) {
        servletPath = path;
    }

    public MessageBytes getServletPathMB() {
        return null;
    }

    public ValveContext getValveContext() {
        return (this.valveContext);
    }

    public void setValveContext(ValveContext valveContext) {
        this.valveContext = valveContext;
    }

    public Wrapper getWrapper() {
        return (this.wrapper);
    }

    public void setWrapper(Wrapper wrapper) {
        this.wrapper = wrapper;
    }

    // START PWC 4707989
    public void setMethod(String method) {
        this.method = method;
    }

    public String getMethod() {
        return this.method;
    }
    // END PWC 4707989

    public String getAuthorization() { return null; }
    public void setAuthorization(String authorization) {}
    public Connector getConnector() { return null; }
    public void setConnector(Connector connector) {}
    public Context getContext() { return null; }
    public void setContext(Context context) {}
    public Host getHost() { return null; }
    public void setHost(Host host) {}
    public String getInfo() { return null; }
    public Response getResponse() { return null; }
    public void setResponse(Response response) {}
    public Socket getSocket() { return null; }
    public void setSocket(Socket socket) {}
    public InputStream getStream() { return null; }
    public void setStream(InputStream input) {}
    public void addLocale(Locale locale) {}
    public ServletInputStream createInputStream() throws IOException {
        return null;
    }
    public void finishRequest() throws IOException {}
    public Object getNote(String name) { return null; }
    public Iterator getNoteNames() { return null; }
    public void removeNote(String name) {}
    public void setContentType(String type) {}
    public void setNote(String name, Object value) {}
    public void setProtocol(String protocol) {}
    public void setRemoteAddr(String remoteAddr) {}
    public void setRemoteHost(String remoteHost) {}
    public void setScheme(String scheme) {}
    public void setServerName(String name) {}
    public void setServerPort(int port) {}
    public Object getAttribute(String name) { return null; }
    public Enumeration getAttributeNames() { return null; }
    public String getCharacterEncoding() { return null; }
    public int getContentLength() { return -1; }
    public void setContentLength(int length) {}
    public String getContentType() { return null; }
    public ServletInputStream getInputStream() throws IOException {
        return null;
    }
    public Locale getLocale() { return null; }
    public Enumeration getLocales() { return null; }
    public String getProtocol() { return null; }
    public BufferedReader getReader() throws IOException { return null; }
    public String getRealPath(String path) { return null; }
    public String getRemoteAddr() { return null; }
    public String getRemoteHost() { return null; }
    public String getScheme() { return null; }
    public String getServerName() { return null; }
    public int getServerPort() { return -1; }
    public boolean isSecure() { return false; }
    public void removeAttribute(String name) {}
    public void setAttribute(String name, Object value) {}
    public void setCharacterEncoding(String enc)
        throws UnsupportedEncodingException {}
    public void addCookie(Cookie cookie) {}
    public void addHeader(String name, String value) {}
    public void addParameter(String name, String values[]) {}
    public void clearCookies() {}
    public void clearHeaders() {}
    public void clearLocales() {}
    public void clearParameters() {}
    public void recycle() {}
    public void setAuthType(String authType) {}
    public void setContextPath(String path) {}
    /* START PWC 4707989
    public void setMethod(String method) {}
    */
    public void setRequestedSessionCookie(boolean flag) {}
    public void setRequestedSessionId(String id) {}
    public void setRequestedSessionURL(boolean flag) {}
    public void setRequestURI(String uri) {}
    public void setSecure(boolean secure) {}
    public void setUserPrincipal(Principal principal) {}
    public String getParameter(String name) { return null; }
    public Map getParameterMap() { return null; }
    public Enumeration getParameterNames() { return dummyEnum; }
    public String[] getParameterValues(String name) { return null; }
    public RequestDispatcher getRequestDispatcher(String path) {
        return null;
    }
    public String getAuthType() { return null; }
    public Cookie[] getCookies() { return null; }
    public long getDateHeader(String name) { return -1; }
    public String getHeader(String name) { return null; }
    public Enumeration getHeaders(String name) { return null; }
    public Enumeration getHeaderNames() { return null; }
    public int getIntHeader(String name) { return -1; }
    /* START PWC 4707989
    public String getMethod() { return null; }
    */
    public String getPathTranslated() { return null; }
    public String getRemoteUser() { return null; }
    public String getRequestedSessionId() { return null; }
    public String getRequestURI() { return null; }
    public void setDecodedRequestURI(String uri) {}
    public StringBuffer getRequestURL() { return null; }
    public HttpSession getSession() { return null; }
    public HttpSession getSession(boolean create) { return null; }
    public boolean isRequestedSessionIdFromCookie() { return false; }
    public boolean isRequestedSessionIdFromURL() { return false; }
    public boolean isRequestedSessionIdFromUrl() { return false; }
    public boolean isRequestedSessionIdValid() { return false; }
    public boolean isUserInRole(String role) { return false; }
    public Principal getUserPrincipal() { return null; }
    public String getLocalAddr() { return null; }    
    public String getLocalName() { return null; }
    public int getLocalPort() { return -1; }
    public int getRemotePort() { return -1; }


    // START CR 6415120
    /**
     * Set whether or not access to resources under WEB-INF or META-INF
     * needs to be checked.
     */
    public void setCheckRestrictedResources(boolean check) {

        this.checkRestrictedResources = check;

    }

    /**
     * Return whether or not access to resources under WEB-INF or META-INF
     * needs to be checked.
     */
    public boolean getCheckRestrictedResources() {

        return this.checkRestrictedResources;

    }
    // END CR 6415120


    // START SJSAS 6346226
    /**
     * Gets the jroute id of this request, which may have been 
     * sent as a separate <code>JROUTE</code> cookie or appended to the
     * session identifier encoded in the URI (if cookies have been disabled).
     * 
     * @return The jroute id of this request, or null if this request does not
     * carry any jroute id
     */
    public String getJrouteId() {
        return null;
    }
    // END SJSAS 6346226
    
}

