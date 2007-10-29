/*
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * Copyright 1999,2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.apache.coyote;


/**
 * Structure holding the Request and Response objects. It also holds statistical
 * informations about request processing and provide management informations
 * about the requests beeing processed.
 *
 * Each thread uses a Request/Response pair that is recycled on each request.
 * This object provides a place to collect global low-level statistics - without
 * having to deal with synchronization ( since each thread will have it's own
 * RequestProcessorMX ).
 *
 * TODO: Request notifications will be registered here.
 *
 * @author Costin Manolache
 */
public class RequestInfo  {

    private RequestGroupInfo global=null;

    private Request req;

    private int stage = Constants.STAGE_NEW;

    /*
     * Statistical data collected at the end of each request.
     */
    private long bytesSent;

    private long bytesReceived;

    // Total time = divide by requestCount to get average.
    private long processingTime;

    // The longest response time for a request
    private long maxTime;

    // URI of the request that took maxTime
    private String maxRequestUri;

    private int requestCount;

    // number of response codes >= 400
    private int errorCount;

    // START S1AS
    // Number of responses with a status code in the 2xx range
    private long count2xx;

    // Number of responses with a status code in the 3xx range
    private long count3xx;

    // Number of responses with a status code in the 4xx range
    private long count4xx;

    // Number of responses with a status code in the 5xx range
    private long count5xx;

    // Number of responses with a status code outside the 2xx, 3xx, 4xx,
    // and 5xx range
    private long countOther;

    // Number of responses with a status code equal to 200
    private long count200;

    // Number of responses with a status code equal to 302
    private long count302;

    // Number of responses with a status code equal to 304
    private long count304;

    // Number of responses with a status code equal to 400
    private long count400;

    // Number of responses with a status code equal to 401
    private long count401;

    // Number of responses with a status code equal to 403
    private long count403;

    // Number of responses with a status code equal to 404
    private long count404;

    // Number of responses with a status code equal to 503
    private long count503;

    private Thread workerThread;
    // END S1AS


    /**
     * Constructor
     */
    public RequestInfo( Request req) {
        this.req=req;
    }

    public RequestGroupInfo getGlobalProcessor() {
        return global;
    }
    
    public void setGlobalProcessor(RequestGroupInfo global) {
        if( global != null) {
            this.global=global;
            global.addRequestProcessor( this );
        } else {
            if (this.global != null) {
                this.global.removeRequestProcessor( this ); 
                this.global = null;
            }
        }
    }


    // ------------------- Information about the current request  -----------
    // This is useful for long-running requests only

    public String getMethod() {
        return req.method().toString();
    }

    public String getCurrentUri() {
        return req.requestURI().toString();
    }

    public String getCurrentQueryString() {
        return req.queryString().toString();
    }

    public String getProtocol() {
        return req.protocol().toString();
    }

    public String getVirtualHost() {
        return req.serverName().toString();
    }

    public int getServerPort() {
        return req.getServerPort();
    }

    public String getRemoteAddr() {
        req.action(ActionCode.ACTION_REQ_HOST_ADDR_ATTRIBUTE, null);
        return req.remoteAddr().toString();
    }

    public int getContentLength() {
        return req.getContentLength();
    }

    public long getRequestBytesReceived() {
        return req.getBytesRead();
    }

    public long getRequestBytesSent() {
        return req.getResponse().getBytesWritten();
    }

    public long getRequestProcessingTime() {
        return (System.currentTimeMillis() - req.getStartTime());
    }


    /**
     * Called by the processor before recycling the request. It'll collect
     * statistic information.
     */
    void updateCounters() {
        bytesReceived+=req.getBytesRead();
        bytesSent+=req.getResponse().getBytesWritten();

        requestCount++;

        int responseStatus = req.getResponse().getStatus();

        // START S1AS
        if (responseStatus >= 200 && responseStatus < 299) {
            // 2xx
            count2xx++;
            if (responseStatus == 200) {
                count200++;
            }
        } else if (responseStatus >= 300 && responseStatus < 399) {
            // 3xx
            count3xx++;
            if (responseStatus == 302) {
                count302++;
            } else if (responseStatus == 304) {
                count304++;
            }
        } else if (responseStatus >= 400 && responseStatus < 499) {
            // 4xx
            count4xx++;
            if (responseStatus == 400) {
                count400++;
            } else if (responseStatus == 401) {
                count401++;
            } else if (responseStatus == 403) {
                count403++;
            } else if (responseStatus == 404) {
                count404++;
            }
        } else if (responseStatus >= 500 && responseStatus < 599) {
            // 5xx
            count5xx++;
            if (responseStatus == 503) {
                count503++;
            }
        } else {
            // Other
            countOther++;
        }
        // END S1AS

        if (responseStatus >= 400) {
            errorCount++;
        }

        long t0=req.getStartTime();
        long t1=System.currentTimeMillis();
        long time=t1-t0;
        processingTime+=time;
        if( maxTime < time ) {
            maxTime=time;
            maxRequestUri=req.requestURI().toString();
        }
    }

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    public long getBytesSent() {
        return bytesSent;
    }

    public void setBytesSent(long bytesSent) {
        this.bytesSent = bytesSent;
    }

    public long getBytesReceived() {
        return bytesReceived;
    }

    public void setBytesReceived(long bytesReceived) {
        this.bytesReceived = bytesReceived;
    }

    public long getProcessingTime() {
        return processingTime;
    }

    public void setProcessingTime(long processingTime) {
        this.processingTime = processingTime;
    }

    public long getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(long maxTime) {
        this.maxTime = maxTime;
    }

    public String getMaxRequestUri() {
        return maxRequestUri;
    }

    public void setMaxRequestUri(String maxRequestUri) {
        this.maxRequestUri = maxRequestUri;
    }

    public int getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(int requestCount) {
        this.requestCount = requestCount;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }

    // START S1AS
    public long getCount2xx() {
        return count2xx;
    }

    public void setCount2xx(long count2xx) {
        this.count2xx = count2xx;
    }

    public long getCount3xx() {
        return count3xx;
    }

    public void setCount3xx(long count3xx) {
        this.count3xx = count3xx;
    }

    public long getCount4xx() {
        return count4xx;
    }

    public void setCount4xx(long count4xx) {
        this.count4xx = count4xx;
    }

    public long getCount5xx() {
        return count5xx;
    }

    public void setCount5xx(long count5xx) {
        this.count5xx = count5xx;
    }

    public long getCountOther() {
        return countOther;
    }

    public void setCountOther(long countOther) {
        this.countOther = countOther;
    }

    public long getCount200() {
        return count200;
    }

    public void setCount200(long count200) {
        this.count200 = count200;
    }

    public long getCount302() {
        return count302;
    }

    public void setCount302(long count302) {
        this.count302 = count302;
    }

    public long getCount304() {
        return count304;
    }

    public void setCount304(long count304) {
        this.count304 = count304;
    }

    public long getCount400() {
        return count400;
    }

    public void setCount400(long count400) {
        this.count400 = count400;
    }

    public long getCount401() {
        return count401;
    }

    public void setCount401(long count401) {
        this.count401 = count401;
    }

    public long getCount403() {
        return count403;
    }

    public void setCount403(long count403) {
        this.count403 = count403;
    }

    public long getCount404() {
        return count404;
    }

    public void setCount404(long count404) {
        this.count404 = count404;
    }

    public long getCount503() {
        return count503;
    }

    public void setCount503(long count503) {
        this.count503 = count503;
    }

    /**
     * Gets the worker thread which is processing the request associated
     * with this RequestInfo.
     * 
     * @return The worker thread
     */
    public Thread getWorkerThread() {
        return workerThread;
    }

    /**
     * Sets the worker thread responsible for processing the request 
     * associated with this RequestInfo.
     *
     * @param workerThread The worker thread
     */
    public void setWorkerThread(Thread workerThread) {
        this.workerThread = workerThread;
    }
    // END S1AS


    // START S1AS
    /**
     * Resets this <code>RequestInfo</code>.
     */
    public void reset() {
        setBytesSent(0);
        setBytesReceived(0);
        setProcessingTime(0);
        setMaxTime(0);
        setMaxRequestUri(null);
        setRequestCount(0);
        setErrorCount(0);
        setCount2xx(0);
        setCount3xx(0);
        setCount4xx(0);
        setCount5xx(0);
        setCountOther(0);
        setCount200(0);
        setCount302(0);
        setCount304(0);
        setCount400(0);
        setCount401(0);
        setCount403(0);
        setCount404(0);
        setCount503(0);
        setWorkerThread(null);
    }
    // END S1AS
}
