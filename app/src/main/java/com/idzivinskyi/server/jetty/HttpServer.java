
package com.idzivinskyi.server.jetty;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.servlet.GuiceFilter;
import com.idzivinskyi.server.jersey.GuiceServletConfigListener;
import com.idzivinskyi.server.jersey.JerseyResourceConfig;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.glassfish.jersey.servlet.ServletContainer;

import javax.servlet.DispatcherType;

import static java.util.EnumSet.allOf;
import static org.glassfish.jersey.servlet.ServletProperties.JAXRS_APPLICATION_CLASS;

@Singleton
public class HttpServer implements Provider<Server> {

    private static final int THREAD_POOL_IDLE_TIMEOUT = 5000;

    private final Injector injector;
    private final GuiceServletConfigListener guiceServletConfigListener;
    private final int port;
    private final String allowedOrigins;
    private final int maxThreads;
    private final int maxIdleTime;
    private final long stopTimeout;

    @Inject
    public HttpServer(@Named("server.port") int port,
                      @Named("allowed.origin.param") String allowedOrigins,
                      @Named("server.max.threads") int maxThreads,
                      @Named("server.max.idle.time") int maxIdleTime,
                      @Named("jersey.stop.timeout") int stopTimeout,
                      Injector injector,
                      GuiceServletConfigListener guiceServletConfigListener) {
        this.port = port;
        this.allowedOrigins = allowedOrigins;
        this.maxThreads = maxThreads;
        this.maxIdleTime = maxIdleTime;
        this.stopTimeout = stopTimeout;
        this.injector = injector;
        this.guiceServletConfigListener = guiceServletConfigListener;
    }

    private Connector[] getServerConnectors(Server server) {
        final ServerConnector connector = new ServerConnector(server);
        connector.setPort(port);
        return new Connector[]{connector};
    }

    private ServletHolder getJerseyServletHolder(Injector injector) {
        ServletHolder jerseyServletHolder = new ServletHolder(new ServletContainer());
        jerseyServletHolder.setInitParameter(JAXRS_APPLICATION_CLASS, jaxrsApplication(injector));
        jerseyServletHolder.setStopTimeout(stopTimeout);
        return jerseyServletHolder;
    }

    public String jaxrsApplication(final Injector injector) {
        JerseyResourceConfig.setInjector(injector);
        return JerseyResourceConfig.class.getName();
    }

    private FilterHolder getOrigins(String allowedOrigins) {
        FilterHolder cofHolder = new FilterHolder(CrossOriginFilter.class);
        cofHolder.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, allowedOrigins);
        cofHolder.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "DELETE,POST,GET,OPTIONS,HEAD");
        return cofHolder;
    }

    private QueuedThreadPool newQueuedThreadPool() {
        QueuedThreadPool tp = new QueuedThreadPool();
        tp.setName("Server");
        tp.setMaxThreads(maxThreads);
        tp.setMinThreads(10);
        tp.setIdleTimeout(THREAD_POOL_IDLE_TIMEOUT);
        tp.setStopTimeout(maxIdleTime);
        return tp;
    }

    private HandlerList getHandlers() {
        final ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);

        context.addFilter(new FilterHolder(injector.getInstance(GuiceFilter.class)), "/*", null);
        context.addFilter(getOrigins(allowedOrigins), "/*", allOf(DispatcherType.class));

        context.setInitParameter("org.eclipse.jetty.servlet.Default.cacheControl", "max-age=0,no-cache");
        context.setInitParameter("org.eclipse.jetty.servlet.Default.dirAllowed", "false");

        ServletHolder sh = getJerseyServletHolder(injector);
        sh.setInitParameter("org.eclipse.jetty.servlet.Default.cacheControl", "max-age=0,no-cache");
        context.addServlet(sh, "/*");
        context.addEventListener(guiceServletConfigListener);

        context.setSessionHandler(new SessionHandler());

        GzipHandler gzipHandler = new GzipHandler();
        gzipHandler.setHandler(context);

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setResourceBase(getWebapp());

        HandlerList handlerList = new HandlerList();
        handlerList.addHandler(resourceHandler);
        handlerList.addHandler(gzipHandler);
        return handlerList;
    }

    private String getWebapp() {
        String path = System.getProperty("webapp_path");
        if (path != null) {
            return path;
        }
        return "web";
    }

    @Override
    public Server get() {
        final Server server = new Server(newQueuedThreadPool());
        server.setConnectors(getServerConnectors(server));
        server.setHandler(getHandlers());
        return server;
    }
}
