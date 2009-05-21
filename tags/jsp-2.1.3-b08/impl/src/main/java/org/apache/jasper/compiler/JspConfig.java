/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 *
 * Contributor(s):
 *
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 *
 *
 * This file incorporates work covered by the following copyright and
 * permission notice:
 *
 * Copyright 2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



package org.apache.jasper.compiler;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.net.URL;

import javax.servlet.ServletContext;

import org.apache.jasper.JasperException;

// START GlassFish 740
import org.apache.jasper.Constants;
// END GlassFish 740
// START SJSAS 6384538
import org.apache.jasper.Options;
// END SJSAS 6384538
import org.apache.jasper.xmlparser.ParserUtils;
import org.apache.jasper.xmlparser.TreeNode;
import org.xml.sax.InputSource;

/**
 * Handles the jsp-config element in WEB_INF/web.xml.  This is used
 * for specifying the JSP configuration information on a JSP page
 *
 * @author Kin-man Chung
 */

public class JspConfig {

    private static final String WEB_XML = "/WEB-INF/web.xml";

    // Logger
    private static Logger log = Logger.getLogger(JspConfig.class.getName());

    // START SJSAS 6384538
    private Options options;
    // END SJSAS 6384538
    private Vector jspProperties = null;
    private ServletContext ctxt;
    private boolean initialized = false;

    private String defaultIsXml = null;		// unspecified
    private String defaultIsELIgnored = null;	// unspecified
    private String defaultIsScriptingInvalid = "false";
    private String defaultTrimSpaces = "false";
    private String defaultPoundAllowed = "false";
    private String defaultErrorOnUndeclaredNamespace = "false";
    private JspProperty defaultJspProperty;

    /* SJSAS 6384538
    public JspConfig(ServletContext ctxt) {
    */
    // START SJSAS 6384538
    public JspConfig(ServletContext ctxt, Options options) {
    // END SJSAS 6384538
	this.ctxt = ctxt;
        // START SJSAS 6384538
        this.options = options;
        // END SJSAS 6384538
    }

    private void processWebDotXml(ServletContext ctxt) throws JasperException {

        InputStream is = null;

        try {
            URL uri = ctxt.getResource(WEB_XML);
            if (uri == null) {
                // no web.xml
                return;
            }

            is = uri.openStream();
            InputSource ip = new InputSource(is);
            ip.setSystemId(uri.toExternalForm()); 

            ParserUtils pu = new ParserUtils();
            /* SJSAS 6384538
            TreeNode webApp = pu.parseXMLDocument(WEB_XML, ip);
            */
            // START SJSAS 6384538
            TreeNode webApp = pu.parseXMLDocument(WEB_XML, ip,
                                                  options.isValidationEnabled());
            // END SJSAS 6384538
            if (webApp == null ||
                webApp.findAttribute("version") == null ||
                Double.valueOf(webApp.findAttribute("version")).doubleValue() < 2.4) {
                defaultIsELIgnored = "true";
                return;
            }

            TreeNode jspConfig = webApp.findChild("jsp-config");
            if (jspConfig == null) {
                return;
            }

            jspProperties = new Vector();
            Iterator jspPropertyList = jspConfig.findChildren("jsp-property-group");
            while (jspPropertyList.hasNext()) {

                TreeNode element = (TreeNode) jspPropertyList.next();
                Iterator list = element.findChildren();

                Vector urlPatterns = new Vector();
                String pageEncoding = null;
                String scriptingInvalid = null;
                String elIgnored = null;
                String isXml = null;
                Vector includePrelude = new Vector();
                Vector includeCoda = new Vector();
                String trimSpaces = null;
                String poundAllowed = null;
                String buffer = null;
                String defaultContentType = null;
                String errorOnUndeclaredNamespace = null;

                while (list.hasNext()) {

                    element = (TreeNode) list.next();
                    String tname = element.getName();

                    if ("url-pattern".equals(tname))
                        urlPatterns.addElement( element.getBody() );
                    else if ("page-encoding".equals(tname))
                        pageEncoding = element.getBody();
                    else if ("is-xml".equals(tname))
                        isXml = element.getBody();
                    else if ("el-ignored".equals(tname))
                        elIgnored = element.getBody();
                    else if ("scripting-invalid".equals(tname))
                        scriptingInvalid = element.getBody();
                    else if ("include-prelude".equals(tname))
                        includePrelude.addElement(element.getBody());
                    else if ("include-coda".equals(tname))
                        includeCoda.addElement(element.getBody());
                    else if ("trim-directive-whitespaces".equals(tname))
                        trimSpaces = element.getBody();
                    else if ("deferred-syntax-allowed-as-literal".equals(tname))
                        poundAllowed = element.getBody();
                    else if ("default-content-type".equals(tname))
                        defaultContentType = element.getBody();
                    else if ("buffer".equals(tname))
                        buffer = element.getBody();
                    else if ("error-on-undeclared-namespace".equals(tname))
                        errorOnUndeclaredNamespace = element.getBody();
                }

                if (urlPatterns.size() == 0) {
                    continue;
                }
 
                makeJspPropertyGroups(jspProperties,
                                      urlPatterns, 
                                      isXml,
                                      elIgnored,
                                      scriptingInvalid,
                                      trimSpaces,
                                      poundAllowed,
                                      pageEncoding,
                                      includePrelude,
                                      includeCoda,
                                      defaultContentType,
                                      buffer,
                                      errorOnUndeclaredNamespace);
            }
        } catch (Exception ex) {
            throw new JasperException(ex);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Throwable t) {}
            }
        }
    }


    public static void makeJspPropertyGroups(Vector jspProperties,
                                             Vector urlPatterns,
                                             String isXml,
                                             String elIgnored,
                                             String scriptingInvalid,
                                             String trimSpaces,
                                             String poundAllowed,
                                             String pageEncoding,
                                             Vector includePrelude,
                                             Vector includeCoda) {
        makeJspPropertyGroups(jspProperties, urlPatterns, isXml,
            elIgnored, scriptingInvalid, trimSpaces, poundAllowed,
            pageEncoding, includePrelude, includeCoda, null, null, null);
    }

    /**
     * Creates a JspPropertyGroup for each url pattern in the given
     * <code>urlPatterns</code>, and adds it to the given
     * <code>jspProperties</code>.
     *
     * This simplifies the matching logic.
     */
    public static void makeJspPropertyGroups(Vector jspProperties,
                                             Vector urlPatterns,
                                             String isXml,
                                             String elIgnored,
                                             String scriptingInvalid,
                                             String trimSpaces,
                                             String poundAllowed,
                                             String pageEncoding,
                                             Vector includePrelude,
                                             Vector includeCoda,
                                             String defaultContentType,
                                             String buffer,
                                             String errorOnUndeclaredNamespace){

        if (urlPatterns == null || urlPatterns.size() == 0) {
            return;
        }

        for (int p = 0; p < urlPatterns.size(); p++) {
            String urlPattern = (String)urlPatterns.elementAt( p );
            String path = null;
            String extension = null;
 
            if (urlPattern.indexOf('*') < 0) {
                // Exact match
                path = urlPattern;
            } else {
                int i = urlPattern.lastIndexOf('/');
                String file;
                if (i >= 0) {
                    path = urlPattern.substring(0,i+1);
                    file = urlPattern.substring(i+1);
                } else {
                    file = urlPattern;
                }
 
                // pattern must be "*", or of the form "*.jsp"
                if (file.equals("*")) {
                    extension = "*";
                } else if (file.startsWith("*.")) {
                    extension = file.substring(file.indexOf('.')+1);
                }

                // The url patterns are reconstructed as the following:
                // path != null, extension == null:  / or /foo/bar.ext
                // path == null, extension != null:  *.ext
                // path != null, extension == "*":   /foo/*
                boolean isStar = "*".equals(extension);
                if ((path == null && (extension == null || isStar))
                        || (path != null && !isStar)) {
                    if (log.isLoggable(Level.WARNING)) {
                        log.warning(Localizer.getMessage(
                            "jsp.warning.bad.urlpattern.propertygroup",
                            urlPattern));
                    }
                    continue;
                }
             }
 
             JspProperty property = new JspProperty(isXml,
                                                    elIgnored,
                                                    scriptingInvalid,
                                                    trimSpaces,
                                                    poundAllowed,
                                                    pageEncoding,
                                                    includePrelude,
                                                    includeCoda,
                                                    defaultContentType,
                                                    buffer,
                                                    errorOnUndeclaredNamespace);
             JspPropertyGroup propertyGroup =
                 new JspPropertyGroup(path, extension, property);

             jspProperties.addElement(propertyGroup);
        }
    }

    private synchronized void init() throws JasperException {

	if (!initialized) {
            /* GlassFish 740
            processWebDotXml(ctxt);
            */
            // START GlassFish 740
            jspProperties = (Vector) ctxt.getAttribute(
                Constants.JSP_PROPERTY_GROUPS_CONTEXT_ATTRIBUTE);
            if (jspProperties == null) {
                processWebDotXml(ctxt);
            }

            String version = (String) ctxt.getAttribute(
                Constants.WEB_XML_VERSION_CONTEXT_ATTRIBUTE);
            if (version != null) {
                if (Double.valueOf(version).doubleValue() < 2.4) {
                    defaultIsELIgnored = "true";
                }
            }
            // END GlassFish 740

	    defaultJspProperty = new JspProperty(defaultIsXml,
						 defaultIsELIgnored,
						 defaultIsScriptingInvalid,
                                                 defaultTrimSpaces,
                                                 defaultPoundAllowed,
                                                 null, null, null,
                                                 null, null,
                                                 defaultErrorOnUndeclaredNamespace);
	    initialized = true;
	}
    }

    /**
     * Select the property group that has more restrictive url-pattern.
     * In case of tie, select the first.
     */
    private JspPropertyGroup selectProperty(JspPropertyGroup prev,
                                            JspPropertyGroup curr) {
        if (prev == null) {
            return curr;
        }
        if (prev.getExtension() == null) {
            // exact match
            return prev;
        }
        if (curr.getExtension() == null) {
            // exact match
            return curr;
        }
        String prevPath = prev.getPath();
        String currPath = curr.getPath();
        if (prevPath == null && currPath == null) {
            // Both specifies a *.ext, keep the first one
            return prev;
        }
        if (prevPath == null && currPath != null) {
            return curr;
        }
        if (prevPath != null && currPath == null) {
            return prev;
        }
        if (prevPath.length() >= currPath.length()) {
            return prev;
        }
        return curr;
    }
            

    /**
     * Find a property that best matches the supplied resource.
     * @param uri the resource supplied.
     * @return a JspProperty indicating the best match, or some default.
     */
    public JspProperty findJspProperty(String uri) throws JasperException {

	init();

	// JSP Configuration settings do not apply to tag files	    
	if (jspProperties == null || uri.endsWith(".tag")
	        || uri.endsWith(".tagx")) {
	    return defaultJspProperty;
	}

	String uriPath = null;
	int index = uri.lastIndexOf('/');
	if (index >=0 ) {
	    uriPath = uri.substring(0, index+1);
	}
	String uriExtension = null;
	index = uri.lastIndexOf('.');
	if (index >=0) {
	    uriExtension = uri.substring(index+1);
	}

	Vector includePreludes = new Vector();
	Vector includeCodas = new Vector();

	JspPropertyGroup isXmlMatch = null;
	JspPropertyGroup elIgnoredMatch = null;
	JspPropertyGroup scriptingInvalidMatch = null;
	JspPropertyGroup trimSpacesMatch = null;
	JspPropertyGroup poundAllowedMatch = null;
	JspPropertyGroup pageEncodingMatch = null;
	JspPropertyGroup defaultContentTypeMatch = null;
	JspPropertyGroup bufferMatch = null;
	JspPropertyGroup errorOnUndeclaredNamespaceMatch = null;

	Iterator iter = jspProperties.iterator();
	while (iter.hasNext()) {

	    JspPropertyGroup jpg = (JspPropertyGroup) iter.next();
	    JspProperty jp = jpg.getJspProperty();

             // (arrays will be the same length)
             String extension = jpg.getExtension();
             String path = jpg.getPath();
 
             if (extension == null) {
                 // exact match pattern: /a/foo.jsp
                 if (!uri.equals(path)) {
                     // not matched;
                     continue;
                 }
             } else {
                 // Matching patterns *.ext or /p/*
                 if (path != null && uriPath != null &&
                         ! uriPath.startsWith(path)) {
                     // not matched
                     continue;
                 }
                 if (!extension.equals("*") &&
                                 !extension.equals(uriExtension)) {
                     // not matched
                     continue;
                 }
             }
             // We have a match
             // Add include-preludes and include-codas
             if (jp.getIncludePrelude() != null) {
                 includePreludes.addAll(jp.getIncludePrelude());
             }
             if (jp.getIncludeCoda() != null) {
                 includeCodas.addAll(jp.getIncludeCoda());
             }

             // If there is a previous match for the same property, remember
             // the one that is more restrictive.
             if (jp.isXml() != null) {
                 isXmlMatch = selectProperty(isXmlMatch, jpg);
             }
             if (jp.isELIgnored() != null) {
                 elIgnoredMatch = selectProperty(elIgnoredMatch, jpg);
             }
             if (jp.isScriptingInvalid() != null) {
                 scriptingInvalidMatch =
                     selectProperty(scriptingInvalidMatch, jpg);
             }
             if (jp.getPageEncoding() != null) {
                 pageEncodingMatch = selectProperty(pageEncodingMatch, jpg);
             }
             if (jp.getTrimSpaces() != null) {
                 trimSpacesMatch = selectProperty(trimSpacesMatch, jpg);
             }
             if (jp.getPoundAllowed() != null) {
                 poundAllowedMatch = selectProperty(poundAllowedMatch, jpg);
             }
             if (jp.getDefaultContentType() != null) {
                 defaultContentTypeMatch =
                     selectProperty(defaultContentTypeMatch, jpg);
             }
             if (jp.getBuffer() != null) {
                 bufferMatch = selectProperty(bufferMatch, jpg);
             }
             if (jp.errorOnUndeclaredNamespace() != null) {
                 errorOnUndeclaredNamespaceMatch =
                     selectProperty(errorOnUndeclaredNamespaceMatch, jpg);
             }
	}


	String isXml = defaultIsXml;
	String isELIgnored = defaultIsELIgnored;
	String isScriptingInvalid = defaultIsScriptingInvalid;
        String trimSpaces = defaultTrimSpaces;
        String poundAllowed = defaultPoundAllowed;
	String pageEncoding = null;
        String defaultContentType = null;
        String buffer = null;
        String errorOnUndeclaredNamespace = defaultErrorOnUndeclaredNamespace;

	if (isXmlMatch != null) {
	    isXml = isXmlMatch.getJspProperty().isXml();
	}
	if (elIgnoredMatch != null) {
	    isELIgnored = elIgnoredMatch.getJspProperty().isELIgnored();
	}
	if (scriptingInvalidMatch != null) {
	    isScriptingInvalid =
		scriptingInvalidMatch.getJspProperty().isScriptingInvalid();
	}
	if (trimSpacesMatch != null) {
	    trimSpaces = trimSpacesMatch.getJspProperty().getTrimSpaces();
	}
	if (poundAllowedMatch != null) {
	    poundAllowed = poundAllowedMatch.getJspProperty().getPoundAllowed();
	}
	if (pageEncodingMatch != null) {
	    pageEncoding = pageEncodingMatch.getJspProperty().getPageEncoding();
	}
	if (defaultContentTypeMatch != null) {
	    defaultContentType =
                defaultContentTypeMatch.getJspProperty().getDefaultContentType();
	}
	if (bufferMatch != null) {
	    buffer = bufferMatch.getJspProperty().getBuffer();
	}
	if (errorOnUndeclaredNamespaceMatch != null) {
	    errorOnUndeclaredNamespace = errorOnUndeclaredNamespaceMatch.
                getJspProperty().errorOnUndeclaredNamespace();
	}

	return new JspProperty(isXml, isELIgnored, isScriptingInvalid,
                               trimSpaces, poundAllowed,
			       pageEncoding, includePreludes, includeCodas,
                               defaultContentType, buffer,
                               errorOnUndeclaredNamespace);
    }

    /**
     * To find out if an uri matches an url pattern in jsp config.  If so,
     * then the uri is a JSP page.  This is used primarily for jspc.
     */
    public boolean isJspPage(String uri) throws JasperException {

        init();
        if (jspProperties == null) {
            return false;
        }

        String uriPath = null;
        int index = uri.lastIndexOf('/');
        if (index >=0 ) {
            uriPath = uri.substring(0, index+1);
        }
        String uriExtension = null;
        index = uri.lastIndexOf('.');
        if (index >=0) {
            uriExtension = uri.substring(index+1);
        }

        Iterator iter = jspProperties.iterator();
        while (iter.hasNext()) {

            JspPropertyGroup jpg = (JspPropertyGroup) iter.next();
            JspProperty jp = jpg.getJspProperty();

            String extension = jpg.getExtension();
            String path = jpg.getPath();

            if (extension == null) {
                if (uri.equals(path)) {
                    // There is an exact match
                    return true;
                }
            } else {
                if ((path == null || path.equals(uriPath)) &&
                    (extension.equals("*") || extension.equals(uriExtension))) {
                    // Matches *, *.ext, /p/*, or /p/*.ext
                    return true;
                }
            }
        }
        return false;
    }
}
