

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
package org.apache.catalina.security;


import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.Principal;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.HashMap;

import javax.security.auth.Subject;
import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.catalina.Globals;
import org.apache.catalina.util.StringManager;
/**
 * This utility class associates a <code>Subject</code> to the current 
 * <code>AccessControlContext</code>. When a <code>SecurityManager</code> is
 * used, the container will always associate the called thread with an 
 * AccessControlContext containing only the principal of the requested
 * Servlet/Filter.
 *
 * This class uses reflection to invoke the invoke methods.
 *
 * @author Jean-Francois Arcand
 */

public final class SecurityUtil{
    
    private final static int INIT= 0;
    private final static int SERVICE = 1;
    private final static int DOFILTER = 1;
    private final static int DESTROY = 2;
    
    private final static String INIT_METHOD = "init";
    private final static String DOFILTER_METHOD = "doFilter";
    private final static String SERVICE_METHOD = "service";
    private final static String DESTROY_METHOD = "destroy";
   
    /**
     * Cache every object for which we are creating method on it.
     */
    private static HashMap objectCache = new HashMap();
        
    private static com.sun.org.apache.commons.logging.Log log=
        com.sun.org.apache.commons.logging.LogFactory.getLog( SecurityUtil.class );
    
    private static String PACKAGE = "org.apache.catalina.security";
    
    private static boolean packageDefinitionEnabled =  
         System.getProperty("package.definition") == null ? false : true;
    
    /**
     * The string resources for this package.
     */
    private static final StringManager sm =
        StringManager.getManager(PACKAGE);    

    // START SJS WS 7.0 6236329
    /**
     * Do we need to execute all invokation under a Subject.doAs call.
     */
    public static boolean executeUnderSubjectDoAs = true;
    // END SJS WS 7.0 6236329
   
    
    /**
     * Perform work as a particular </code>Subject</code>. Here the work
     * will be granted to a <code>null</code> subject. 
     *
     * @param methodName the method to apply the security restriction
     * @param targetObject the <code>Servlet</code> on which the method will
     * be called.
     */
    public static void doAsPrivilege(final String methodName, 
                                     final Servlet targetObject) throws java.lang.Exception{
         doAsPrivilege(methodName, targetObject, null, null, null);                                
    }

    
    /**
     * Perform work as a particular </code>Subject</code>. Here the work
     * will be granted to a <code>null</code> subject. 
     *
     * @param methodName the method to apply the security restriction
     * @param targetObject the <code>Servlet</code> on which the method will
     * be called.
     * @param targetType <code>Class</code> array used to instanciate a i
     * <code>Method</code> object.
     * @param targetObject <code>Object</code> array contains the runtime 
     * parameters instance.
     */
    public static void doAsPrivilege(final String methodName, 
                                     final Servlet targetObject, 
                                     final Class[] targetType,
                                     final Object[] targetArguments) 
        throws java.lang.Exception{    

         doAsPrivilege(methodName, 
                       targetObject, 
                       targetType, 
                       targetArguments, 
                       null);                                
    }
    
    
    /**
     * Perform work as a particular </code>Subject</code>. Here the work
     * will be granted to a <code>null</code> subject. 
     *
     * @param methodName the method to apply the security restriction
     * @param targetObject the <code>Servlet</code> on which the method will
     * be called.
     * @param targetType <code>Class</code> array used to instanciate a 
     * <code>Method</code> object.
     * @param targetArgumentst <code>Object</code> array contains the 
     * runtime parameters instance.
     * @param principal the <code>Principal</code> to which the security 
     * privilege apply..
     */    
    public static void doAsPrivilege(final String methodName, 
                                     final Servlet targetObject, 
                                     final Class[] targetType,
                                     final Object[] targetArguments,
                                     Principal principal) 
        throws java.lang.Exception{

        Method method = null;
        Method[] methodsCache = null;
        if(objectCache.containsKey(targetObject)){
            methodsCache = (Method[])objectCache.get(targetObject);
            method = findMethod(methodsCache, methodName);
            if (method == null){
                method = createMethodAndCacheIt(methodsCache,
                                                methodName,
                                                targetObject,
                                                targetType);
            }
        } else {
            method = createMethodAndCacheIt(methodsCache,
                                            methodName,
                                            targetObject,
                                            targetType);                     
        }

        execute(method, targetObject, targetArguments, principal);
    }
 
    
    /**
     * Perform work as a particular </code>Subject</code>. Here the work
     * will be granted to a <code>null</code> subject. 
     *
     * @param methodName the method to apply the security restriction
     * @param targetObject the <code>Filter</code> on which the method will 
     * be called.
     */    
    public static void doAsPrivilege(final String methodName, 
                                     final Filter targetObject) 
        throws java.lang.Exception{

         doAsPrivilege(methodName, targetObject, null, null);                                
    }
 
    
    /**
     * Perform work as a particular </code>Subject</code>. Here the work
     * will be granted to a <code>null</code> subject. 
     *
     * @param methodName the method to apply the security restriction
     * @param targetObject the <code>Filter</code> on which the method will 
     * be called.
     * @param targetType <code>Class</code> array used to instanciate a
     * <code>Method</code> object.
     * @param targetArgumentst <code>Object</code> array contains the 
     * runtime parameters instance.
     */    
    public static void doAsPrivilege(final String methodName, 
                                     final Filter targetObject, 
                                     final Class[] targetType,
                                     final Object[] targetArguments) 
        throws java.lang.Exception{
        Method method = null;

        Method[] methodsCache = null;
        if(objectCache.containsKey(targetObject)){
            methodsCache = (Method[])objectCache.get(targetObject);
            method = findMethod(methodsCache, methodName);
            if (method == null){
                method = createMethodAndCacheIt(methodsCache,
                                                methodName,
                                                targetObject,
                                                targetType);
            }
        } else {
            method = createMethodAndCacheIt(methodsCache,
                                            methodName,
                                            targetObject,
                                            targetType);                     
        }

        execute(method, targetObject, targetArguments, null);
    }
    
    
    /**
     * Perform work as a particular </code>Subject</code>. Here the work
     * will be granted to a <code>null</code> subject. 
     *
     * @param methodName the method to apply the security restriction
     * @param targetObject the <code>Servlet</code> on which the method will
     * be called.
     * @param targetType <code>Class</code> array used to instanciate a 
     * <code>Method</code> object.
     * @param targetArgumentst <code>Object</code> array contains the 
     * runtime parameters instance.
     * @param principal the <code>Principal</code> to which the security 
     * privilege apply..
     */    
    private static void execute(final Method method,
                                final Object targetObject, 
                                final Object[] targetArguments,
                                Principal principal) 
        throws java.lang.Exception{
       
        try{   
            Subject subject = null;
            PrivilegedExceptionAction pea = new PrivilegedExceptionAction(){
                    public Object run() throws Exception{
                       method.invoke(targetObject, targetArguments);
                       return null;
                    }
            };

            // The first argument is always the request object
            if (targetArguments != null 
                    && targetArguments[0] instanceof HttpServletRequest){
                HttpServletRequest request = 
                    (HttpServletRequest)targetArguments[0];

                boolean hasSubject = false;
                HttpSession session = request.getSession(false);
                if (session != null){
                    subject = 
                        (Subject)session.getAttribute(Globals.SUBJECT_ATTR);
                    hasSubject = (subject != null);
                }

                if (subject == null){
                    subject = new Subject();
                    
                    if (principal != null){
                        subject.getPrincipals().add(principal);
                    }
                }

                if (session != null && !hasSubject) {
                    session.setAttribute(Globals.SUBJECT_ATTR, subject);
                }

            }

            Subject.doAsPrivileged(subject, pea, null);       
       } catch( PrivilegedActionException pe) {
            Throwable e = ((InvocationTargetException)pe.getException())
                                .getTargetException();
            
            if (log.isDebugEnabled()){
                log.debug(sm.getString("SecurityUtil.doAsPrivilege"), e); 
            }
            
            if (e instanceof UnavailableException)
                throw (UnavailableException) e;
            else if (e instanceof ServletException)
                throw (ServletException) e;
            else if (e instanceof IOException)
                throw (IOException) e;
            else if (e instanceof RuntimeException)
                throw (RuntimeException) e;
            else
                throw new ServletException(e.getMessage(), e);
        }  
    }
    
    
    /**
     * Find a method stored within the cache.
     * @param methodsCache the cache used to store method instance
     * @param methodName the method to apply the security restriction
     * @return the method instance, null if not yet created.
     */
    private static Method findMethod(Method[] methodsCache,
                                     String methodName){
        if (methodName.equalsIgnoreCase(INIT_METHOD) 
                && methodsCache[INIT] != null){
            return methodsCache[INIT];
        } else if (methodName.equalsIgnoreCase(DESTROY_METHOD) 
                && methodsCache[DESTROY] != null){
            return methodsCache[DESTROY];            
        } else if (methodName.equalsIgnoreCase(SERVICE_METHOD) 
                && methodsCache[SERVICE] != null){
            return methodsCache[SERVICE];
        } else if (methodName.equalsIgnoreCase(DOFILTER_METHOD) 
                && methodsCache[DOFILTER] != null){
            return methodsCache[DOFILTER];          
        } 
        return null;
    }
    
    
    /**
     * Create the method and cache it for further re-use.
     * @param methodsCache the cache used to store method instance
     * @param methodName the method to apply the security restriction
     * @param targetObject the <code>Servlet</code> on which the method will
     * be called.
     * @param targetType <code>Class</code> array used to instanciate a 
     * <code>Method</code> object.
     * @return the method instance.
     */
    private static Method createMethodAndCacheIt(Method[] methodsCache,
                                                 String methodName,
                                                 Object targetObject,
                                                 Class[] targetType) 
            throws Exception{
        
        if ( methodsCache == null){
            methodsCache = new Method[3];
        }               
                
        Method method = 
            targetObject.getClass().getMethod(methodName, targetType); 

        if (methodName.equalsIgnoreCase(INIT_METHOD)){
            methodsCache[INIT] = method;
        } else if (methodName.equalsIgnoreCase(DESTROY_METHOD)){
            methodsCache[DESTROY] = method;
        } else if (methodName.equalsIgnoreCase(SERVICE_METHOD)){
            methodsCache[SERVICE] = method;
        } else if (methodName.equalsIgnoreCase(DOFILTER_METHOD)){
            methodsCache[DOFILTER] = method;
        } 
         
        objectCache.put(targetObject, methodsCache );
                                           
        return method;
    }

    
    /**
     * Remove the object from the cache.
     */
    public static void remove(Object cachedObject){
        objectCache.remove(cachedObject);
    }
    
    
    /**
     * Return the <code>SecurityManager</code> only if Security is enabled AND
     * package protection mechanism is enabled.
     */
    public static boolean isPackageProtectionEnabled(){
        if (packageDefinitionEnabled && System.getSecurityManager() !=  null){
            return true;
        }
        return false;
    }
    
    
    // START OF SJS WS 7.0 6236329
    /**
     * Return true if a <code>SecurityManager</code> is used and is 
     * <code>isDoAsRequired</code> is required.
     */
    public static boolean executeUnderSubjectDoAs(){
        if (executeUnderSubjectDoAs && System.getSecurityManager() !=  null){
            return true;
        }
        return false;
    }    
    // END OF SJS WS 7.0 6236329

}
