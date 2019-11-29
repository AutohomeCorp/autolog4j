/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// Contributors: Dan MacDonald <dan@redknee.com>

package com.autohome.autolog4j.log4j1x.appenders;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;

import com.autohome.autolog4j.log4j1x.helpers.ScoketLineProtocolErrorHandler;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.net.SocketNode;
import org.apache.log4j.net.ZeroConfSupport;
import org.apache.log4j.spi.ErrorCode;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Sends {@link LoggingEvent} objects to a remote a log server,
 * usually a {@link SocketNode}.
 * <p>
 * <p>The SocketLineProtocolAppender has the following properties:
 * <p>
 * <ul>
 * <p>
 * <p><li>If sent to a {@link SocketNode}, remote logging is
 * non-intrusive as far as the log event is concerned. In other
 * words, the event will be logged with the same time stamp, {@link
 * org.apache.log4j.NDC}, location info as if it were logged locally by
 * the client.
 * <p>
 * <p><li>SocketAppenders do not use a layout. They ship a
 * serialized {@link LoggingEvent} object to the server side.
 * <p>
 * <p><li>Remote logging uses the TCP protocol. Consequently, if
 * the server is reachable, then log events will eventually arrive
 * at the server.
 * <p>
 * <p><li>If the remote server is down, the logging requests are
 * simply dropped. However, if and when the server comes back up,
 * then event transmission is resumed transparently. This
 * transparent reconneciton is performed by a <em>connector</em>
 * thread which periodically attempts to connect to the server.
 * <p>
 * <p><li>Logging events are automatically <em>buffered</em> by the
 * native TCP implementation. This means that if the link to server
 * is slow but still faster than the rate of (log) event production
 * by the client, the client will not be affected by the slow
 * network connection. However, if the network connection is slower
 * then the rate of event production, then the client can only
 * progress at the network rate. In particular, if the network link
 * to the the server is down, the client will be blocked.
 * <p>
 * <p>On the other hand, if the network link is up, but the server
 * is down, the client will not be blocked when making log requests
 * but the log events will be lost due to server unavailability.
 * <p>
 * <p><li>Even if a <code>SocketLineProtocolAppender</code> is no longer
 * attached to any category, it will not be garbage collected in
 * the presence of a connector thread. A connector thread exists
 * only if the connection to the server is down. To avoid this
 * garbage collection problem, you should {@link #close} the the
 * <code>SocketLineProtocolAppender</code> explicitly. See also next item.
 * <p>
 * <p>Long lived applications which create/destroy many
 * <code>SocketLineProtocolAppender</code> instances should be aware of this
 * garbage collection problem. Most other applications can safely
 * ignore it.
 * <p>
 * <p><li>If the JVM hosting the <code>SocketLineProtocolAppender</code> exits
 * before the <code>SocketLineProtocolAppender</code> is closed either
 * explicitly or subsequent to garbage collection, then there might
 * be untransmitted data in the pipe which might be lost. This is a
 * common problem on Windows based systems.
 * <p>
 * <p>To avoid lost data, it is usually sufficient to {@link
 * #close} the <code>SocketLineProtocolAppender</code> either explicitly or by
 * calling the {@link org.apache.log4j.LogManager#shutdown} method
 * before exiting the application.
 * <p>
 * <p>
 * </ul>
 *
 * @author Ceki G&uuml;lc&uuml;
 * @since 0.8.4
 */

public class SocketLineProtocolAppender extends AppenderSkeleton {

    /**
     * The default port number of remote logging server (4560).
     *
     * @since 1.2.15
     */
    public static final int DEFAULT_PORT = 4560;
    /**
     * The MulticastDNS zone advertised by a SocketLineProtocolAppender
     */
    public static final String ZONE = "_log4j_obj_tcpconnect_appender.local.";
    /**
     * The default reconnection delay (30000 milliseconds or 30 seconds).
     */
    static final int DEFAULT_RECONNECTION_DELAY = 30000;
    // reset the ObjectOutputStream every 70 calls
    //private static final int RESET_FREQUENCY = 70;
    private static final int RESET_FREQUENCY = 1;
    /**
     * We remember host name as String in addition to the resolved
     * InetAddress so that it can be returned via getOption().
     */
    String remoteHost;
    boolean ignoreExceptions;
    InetAddress address;
    int port = DEFAULT_PORT;
    //OutputStream os;
    Writer writer;
    int reconnectionDelay = DEFAULT_RECONNECTION_DELAY;
    boolean locationInfo = false;
    //int counter = 0;
    private String application;
    private Connector connector;
    private boolean advertiseViaMulticastDNS;
    private ZeroConfSupport zeroConf;

    public SocketLineProtocolAppender() {
    }

    /**
     * Connects to remote server at <code>address</code> and <code>port</code>.
     */
    public SocketLineProtocolAppender(InetAddress address, int port) {
        this.address = address;
        this.remoteHost = address.getHostName();
        this.port = port;
        connect(address, port);
    }

    /**
     * Connects to remote server at <code>host</code> and <code>port</code>.
     */
    public SocketLineProtocolAppender(String host, int port) {
        this.port = port;
        this.address = getAddressByName(host);
        this.remoteHost = host;
        connect(address, port);
    }

    static InetAddress getAddressByName(String host) {
        try {
            return InetAddress.getByName(host);
        } catch (Exception e) {
            if (e instanceof InterruptedIOException || e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            LogLog.error("Could not find address of [" + host + "].", e);
            return null;
        }
    }

    /**
     * Connect to the specified <b>RemoteHost</b> and <b>Port</b>.
     */
    @Override
    public void activateOptions() {
        if (advertiseViaMulticastDNS) {
            zeroConf = new ZeroConfSupport(ZONE, port, getName());
            zeroConf.advertise();
        }
        connect(address, port);
    }

    /**
     * Close this appender.
     * <p>
     * <p>This will mark the appender as closed and call then {@link
     * #cleanUp} method.
     */
    @Override
    public synchronized void close() {
        if (closed) {
            return;
        }

        this.closed = true;
        if (advertiseViaMulticastDNS) {
            zeroConf.unadvertise();
        }

        cleanUp();
    }

    /**
     * Drop the connection to the remote host and release the underlying
     * connector thread if it has been created
     */
    public void cleanUp() {
        if (writer != null) {
            try {
                writer.close();
            } catch (IOException e) {
                if (e instanceof InterruptedIOException) {
                    Thread.currentThread().interrupt();
                }
                LogLog.error("Could not close writer.", e);
                //errorHandler.error("Could not close writer.",e,ErrorCode.CLOSE_FAILURE);
            }
            writer = null;
        }
        if (connector != null) {
            //LogLog.debug("Interrupting the connector.");
            connector.interrupted = true;
            connector = null;  // allow gc
        }
    }

    void connect(InetAddress address, int port) {
        if (this.address == null) {
            errorHandler.error("No remote host is set for SocketLineProtocolAppender named \""
                    + this.name + "\".");
            return;
        }
        try {
            // First, close the previous connection if any.
            cleanUp();
            //os = new BufferedOutputStream(new Socket(address, port).getOutputStream());
            this.writer = new BufferedWriter(new OutputStreamWriter(new Socket(address, port).getOutputStream(), Charset.forName("UTF-8")));
        } catch (IOException e) {
            if (e instanceof InterruptedIOException) {
                Thread.currentThread().interrupt();
            }
            String msg = "Could not connect to remote log4j server at ["
                    + address.getHostName() + "].";
            if (reconnectionDelay > 0) {
                msg += " We will try again later.";
                fireConnector(); // fire the connector thread
                //errorHandler.error(msg,e,ErrorCode.CLOSE_FAILURE);
            } else {
                msg += " We are not retrying.";
                errorHandler.error(msg, e, ErrorCode.GENERIC_FAILURE);
            }
            LogLog.error(msg);
        }
    }

    @Override
    public void append(LoggingEvent event) {
        if (event == null) {
            return;
        }
        if (address == null) {
            errorHandler.error("No remote host is set for SocketLineProtocolAppender named \""
                    + this.name + "\".");
            return;
        }
        if (writer != null) {
            try {
                if (locationInfo) {
                    event.getLocationInformation();
                }
                if (application != null) {
                    event.setProperty("application", application);
                }
                event.getNDC();
                event.getThreadName();
                event.getMDCCopy();
                event.getRenderedMessage();
                event.getThrowableStrRep();
                writer.write(event.getMessage().toString());
                writer.write('\n');
                //LogLog.debug("=========Flushing.");
                writer.flush();
            } catch (IOException e) {
                if (e instanceof InterruptedIOException) {
                    Thread.currentThread().interrupt();
                }
                writer = null;
                LogLog.warn("Detected problem with connection: " + e);
                if (reconnectionDelay > 0) {
                    fireConnector();
                    errorHandler.error("Detected write with connection: ", e, ErrorCode.WRITE_FAILURE);
                } else {
                    errorHandler.error("Detected problem with connection, not reconnecting.", e, ErrorCode.GENERIC_FAILURE);
                }
            }
        } else {
            if (reconnectionDelay > 0) {
                fireConnector();
            }
            errorHandler.error("Detected problem with write: writer is null ");
        }
    }

    public boolean isAdvertiseViaMulticastDNS() {
        return advertiseViaMulticastDNS;
    }

    public void setAdvertiseViaMulticastDNS(boolean advertiseViaMulticastDNS) {
        this.advertiseViaMulticastDNS = advertiseViaMulticastDNS;
    }

    void fireConnector() {
        if (connector == null) {
            LogLog.debug("Starting a new connector thread.");
            connector = new Connector();
            connector.setDaemon(true);
            connector.setPriority(Thread.MIN_PRIORITY);
            connector.start();
        }
    }

    /**
     * The SocketLineProtocolAppender does not use a layout. Hence, this method
     * returns <code>false</code>.
     */
    @Override
    public boolean requiresLayout() {
        return false;
    }

    /**
     * Returns value of the <b>RemoteHost</b> option.
     */
    public String getRemoteHost() {
        return remoteHost;
    }

    /**
     * The <b>RemoteHost</b> option takes a string value which should be
     * the host name of the server where a {@link SocketNode} is
     * running.
     */
    public void setRemoteHost(String host) {
        address = getAddressByName(host);
        remoteHost = host;
    }

    public boolean isIgnoreExceptions() {
        return ignoreExceptions;
    }

    public void setIgnoreExceptions(boolean ignoreExceptions) {
        this.ignoreExceptions = ignoreExceptions;
        errorHandler = new ScoketLineProtocolErrorHandler(ignoreExceptions);
    }

    /**
     * Returns value of the <b>Port</b> option.
     */
    public int getPort() {
        return port;
    }

    /**
     * The <b>Port</b> option takes a positive integer representing
     * the port where the server is waiting for connections.
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Returns value of the <b>LocationInfo</b> option.
     */
    public boolean getLocationInfo() {
        return locationInfo;
    }

    /**
     * The <b>LocationInfo</b> option takes a boolean value. If true,
     * the information sent to the remote host will include location
     * information. By default no location information is sent to the server.
     */
    public void setLocationInfo(boolean locationInfo) {
        this.locationInfo = locationInfo;
    }

    /**
     * Returns value of the <b>Application</b> option.
     *
     * @since 1.2.15
     */
    public String getApplication() {
        return application;
    }

    /**
     * The <b>App</b> option takes a string value which should be the name of the
     * application getting logged.
     * If property was already set (via system property), don't set here.
     *
     * @since 1.2.15
     */
    public void setApplication(String lapp) {
        this.application = lapp;
    }

    /**
     * Returns value of the <b>ReconnectionDelay</b> option.
     */
    public int getReconnectionDelay() {
        return reconnectionDelay;
    }

    /**
     * The <b>ReconnectionDelay</b> option takes a positive integer
     * representing the number of milliseconds to wait between each
     * failed connection attempt to the server. The default value of
     * this option is 30000 which corresponds to 30 seconds.
     * <p>
     * <p>Setting this option to zero turns off reconnection
     * capability.
     */
    public void setReconnectionDelay(int delay) {
        this.reconnectionDelay = delay;
    }

    /**
     * The Connector will reconnect when the server becomes available
     * again.  It does this by attempting to open a new connection every
     * <code>reconnectionDelay</code> milliseconds.
     * <p>
     * <p>It stops trying whenever a connection is established. It will
     * restart to try reconnect to the server when previously open
     * connection is droppped.
     *
     * @author Ceki G&uuml;lc&uuml;
     * @since 0.8.4
     */
    class Connector extends Thread {

        boolean interrupted = false;

        @Override
        public void run() {
            Socket socket;
            while (!interrupted) {
                try {
                    sleep(reconnectionDelay);
                    LogLog.debug("Attempting connection to " + address.getHostName());
                    socket = new Socket(address, port);
                    synchronized (this) {
                        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
                        connector = null;
                        LogLog.debug("Connection established. Exiting connector thread.");
                        break;
                    }
                } catch (InterruptedException e) {
                    LogLog.debug("Connector interrupted. Leaving loop.");
                    return;
                } catch (java.net.ConnectException e) {
                    LogLog.debug("Remote host " + address.getHostName()
                            + " refused connection.");
                } catch (IOException e) {
                    if (e instanceof InterruptedIOException) {
                        Thread.currentThread().interrupt();
                    }
                    LogLog.debug("Could not connect to " + address.getHostName()
                            + ". Exception is " + e);
                }
            }
            //LogLog.debug("Exiting Connector.run() method.");
        }

        /**
         public
         void finalize() {
         LogLog.debug("Connector finalize() has been called.");
         }
         */
    }

}
