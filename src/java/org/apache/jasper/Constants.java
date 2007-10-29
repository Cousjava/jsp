

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

package org.apache.jasper;

import java.util.ResourceBundle;
import java.util.MissingResourceException;
import java.text.MessageFormat;

/**
 * Some constants and other global data that are used by the compiler and the runtime.
 *
 * @author Anil K. Vijendran
 * @author Harish Prabandham
 * @author Shawn Bayern
 * @author Mark Roth
 */
public class Constants {
    /**
     * The base class of the generated servlets. 
     */
    public static final String JSP_SERVLET_BASE = "org.apache.jasper.runtime.HttpJspBase";

    /**
     * _jspService is the name of the method that is called by 
     * HttpJspBase.service(). This is where most of the code generated
     * from JSPs go.
     */
    public static final String SERVICE_METHOD_NAME = "_jspService";

    /**
     * Default servlet content type.
     */
    public static final String SERVLET_CONTENT_TYPE = "text/html";

    /**
     * These classes/packages are automatically imported by the
     * generated code. 
     */
    public static final String[] STANDARD_IMPORTS = { 
	"javax.servlet.*", 
	"javax.servlet.http.*", 
	"javax.servlet.jsp.*"
    };

    /**
     * FIXME
     * ServletContext attribute for classpath. This is tomcat specific. 
     * Other servlet engines may choose to support this attribute if they 
     * want to have this JSP engine running on them. 
     */
    public static final String SERVLET_CLASSPATH = "org.apache.catalina.jsp_classpath";

    /**
     * FIXME
     * Request attribute for <code>&lt;jsp-file&gt;</code> element of a
     * servlet definition.  If present on a request, this overrides the
     * value returned by <code>request.getServletPath()</code> to select
     * the JSP page to be executed.
     */
    public static final String JSP_FILE = "org.apache.catalina.jsp_file";


    /**
     * FIXME
     * ServletContext attribute for class loader. This is tomcat specific. 
     * Other servlet engines can choose to have this attribute if they 
     * want to have this JSP engine running on them. 
     */
    //public static final String SERVLET_CLASS_LOADER = "org.apache.tomcat.classloader";
    public static final String SERVLET_CLASS_LOADER = "org.apache.catalina.classloader";

    /**
     * Default size of the JSP buffer.
     */
    public static final int K = 1024;
    public static final int DEFAULT_BUFFER_SIZE = 8*K;

    /**
     * Default size for the tag buffers.
     */
    public static final int DEFAULT_TAG_BUFFER_SIZE = 512;

    /**
     * Default tag handler pool size.
     */
    public static final int MAX_POOL_SIZE = 5;

    /**
     * The query parameter that causes the JSP engine to just
     * pregenerated the servlet but not invoke it. 
     */
    public static final String PRECOMPILE = "jsp_precompile";

    /**
     * The default package name for compiled jsp pages.
     */
    public static final String JSP_PACKAGE_NAME = "org.apache.jsp";

    /**
     * The default package name for tag handlers generated from tag files
     */
    public static final String TAG_FILE_PACKAGE_NAME = "org.apache.jsp.tag";

    /**
     * Servlet context and request attributes that the JSP engine
     * uses. 
     */
    public static final String INC_REQUEST_URI = "javax.servlet.include.request_uri";
    public static final String INC_SERVLET_PATH = "javax.servlet.include.servlet_path";
    public static final String TMP_DIR = "javax.servlet.context.tempdir";
    public static final String FORWARD_SEEN = "javax.servlet.forward.seen";
    public static final String FIRST_REQUEST_SEEN = "jspx.1st.request";

    // Must be kept in sync with org/apache/catalina/Globals.java
    public static final String ALT_DD_ATTR = "org.apache.catalina.deploy.alt_dd";

    /**
     * Public Id and the Resource path (of the cached copy) 
     * of the DTDs and schemas for tag library descriptors. 
     */
    public static final String TAGLIB_DTD_PUBLIC_ID_11 = 
	"-//Sun Microsystems, Inc.//DTD JSP Tag Library 1.1//EN";
    public static final String TAGLIB_DTD_RESOURCE_PATH_11 = 
	"/javax/servlet/jsp/resources/web-jsptaglibrary_1_1.dtd";
    public static final String TAGLIB_DTD_PUBLIC_ID_12 = 
	"-//Sun Microsystems, Inc.//DTD JSP Tag Library 1.2//EN";
    public static final String TAGLIB_DTD_RESOURCE_PATH_12 = 
	"/javax/servlet/jsp/resources/web-jsptaglibrary_1_2.dtd";
    public static final String TAGLIB_SCHEMA_PUBLIC_ID_20 =
        "web-jsptaglibrary_2_0.xsd";
    public static final String TAGLIB_SCHEMA_RESOURCE_PATH_20 =
        "/javax/servlet/jsp/resources/web-jsptaglibrary_2_0.xsd";
    public static final String TAGLIB_SCHEMA_PUBLIC_ID_21 =
        "web-jsptaglibrary_2_1.xsd";
    public static final String TAGLIB_SCHEMA_RESOURCE_PATH_21 =
        "/javax/servlet/jsp/resources/web-jsptaglibrary_2_1.xsd";
    public static final String SCHEMA_LOCATION_JSP_20
        = "http://java.sun.com/xml/ns/j2ee http://java.sun.com/xml/ns/j2ee/web-jsptaglibrary_2_0.xsd";
    public static final String SCHEMA_LOCATION_JSP_21
        = "http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-jsptaglibrary_2_1.xsd";

    /**
     * Public Id and the Resource path (of the cached copy) 
     * of the DTDs and schemas for web application deployment descriptors
     */
    public static final String WEBAPP_DTD_PUBLIC_ID_22 = 
	"-//Sun Microsystems, Inc.//DTD Web Application 2.2//EN";
    public static final String WEBAPP_DTD_RESOURCE_PATH_22 = 
	"/javax/servlet/resources/web-app_2_2.dtd";
    public static final String WEBAPP_DTD_PUBLIC_ID_23 = 
	"-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN";
    public static final String WEBAPP_DTD_RESOURCE_PATH_23 = 
	"/javax/servlet/resources/web-app_2_3.dtd";
    public static final String WEBAPP_SCHEMA_PUBLIC_ID_24 =
        "web-app_2_4.xsd";
    public static final String WEBAPP_SCHEMA_RESOURCE_PATH_24 =
        "/javax/servlet/resources/web-app_2_4.xsd";
    public static final String WEBAPP_SCHEMA_PUBLIC_ID_25 =
        "web-app_2_5.xsd";
    public static final String WEBAPP_SCHEMA_RESOURCE_PATH_25 =
        "/javax/servlet/resources/web-app_2_5.xsd";
    public static final String SCHEMA_LOCATION_WEBAPP_24
        = "http://java.sun.com/xml/ns/j2ee http://java.sun.com/xml/ns/j2ee/web-app_2_4.xsd";
    public static final String SCHEMA_LOCATION_WEBAPP_25
        = "http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd";

    /**
     * List of the Public IDs that we cache, and their
     * associated location. This is used by 
     * an EntityResolver to return the location of the
     * cached copy of a DTD.
     */
    public static final String[] CACHED_DTD_PUBLIC_IDS = {
	TAGLIB_DTD_PUBLIC_ID_11,
	TAGLIB_DTD_PUBLIC_ID_12,
	WEBAPP_DTD_PUBLIC_ID_22,
	WEBAPP_DTD_PUBLIC_ID_23,
    };

    /* PWC 6386258
    public static final String[] CACHED_DTD_RESOURCE_PATHS = {
	TAGLIB_DTD_RESOURCE_PATH_11,
	TAGLIB_DTD_RESOURCE_PATH_12,
	WEBAPP_DTD_RESOURCE_PATH_22,
	WEBAPP_DTD_RESOURCE_PATH_23,
    };
    */
    
    /**
     * Default URLs to download the pluging for Netscape and IE.
     */
    public static final String NS_PLUGIN_URL = 
        "http://java.sun.com/products/plugin/";

    public static final String IE_PLUGIN_URL = 
        "http://java.sun.com/products/plugin/1.2.2/jinstall-1_2_2-win.cab#Version=1,2,2,0";

    /**
     * Prefix to use for generated temporary variable names
     */
    public static final String TEMP_VARIABLE_NAME_PREFIX =
        "_jspx_temp";

    /**
     * A replacement char for "\$".
     * XXX This is a hack to avoid changing EL interpreter to recognize "\$"
     */
    public static final char ESC='\u001b';
    public static final String ESCStr="'\\u001b'";

    public static final Double JSP_VERSION_2_0 = Double.valueOf("2.0");
    public static final Double JSP_VERSION_2_1 = Double.valueOf("2.1");

    // START SJSWS
    /*
     * Default initial capacity of HashMap which maps JSPs to their
     * corresponding servlets
     */
    public static final int DEFAULT_INITIAL_CAPACITY = 32;
    // END SJSWS

    // START PWC 6441271
    /*
     * Minimum number of java compiler threads created during startup
     * and present during idle time
     */
    public static final int DEFAULT_MIN_THREADS = 1;

    /*
     * Maximum number of java compiler threads in the pool
     */
    public static final int DEFAULT_MAX_THREADS = 20;
    // END PWC 6441271

    // START GlassFish 750
    public static final String JSP_TAGLIBRARY_CACHE = "com.sun.jsp.taglibraryCache";
    // END GlassFish 750
}

