package org.jtwig.render;

import org.apache.http.client.fluent.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.FileResource;
import org.jtwig.environment.EnvironmentConfigurationBuilder;
import org.jtwig.web.servlet.JtwigRenderer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class RenderExtensionTest {
    protected static int port;
    protected static Server server;

    @Before
    public void setUp() throws Exception {
        server = new Server(0);
        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        context.setBaseResource(new FileResource(new File("src/main/webapp").getAbsoluteFile().toURI().toURL()));

        context.addServlet(new ServletHolder(new HelloServlet()), "/hello");
        context.addServlet(new ServletHolder(new RenderServlet()), "/render");

        server.setHandler(context);
        server.start();

        port = ((ServerConnector) server.getConnectors()[0]).getLocalPort();

    }

    @After
    public void tearDown() throws Exception {
        server.stop();
    }

    public static String serverUrl() {
        return String.format("http://localhost:%d/", port);
    }
    @Test
    public void renderTest() throws Exception {

        String content = Request.Get(String.format("%s/render", serverUrl()))
                .execute().returnContent().asString();

        assertThat(content, is("Hello World"));
    }


    public static class HelloServlet extends HttpServlet {
        private final JtwigRenderer renderer = new JtwigRenderer(EnvironmentConfigurationBuilder.configuration()
                .extensions().add(new RenderExtension()).and()
                .build());

        @Override
        protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            renderer.inlineDispatcherFor("World")
                    .render(request, response);
        }
    }


    public static class RenderServlet extends HttpServlet {
        private final JtwigRenderer renderer = new JtwigRenderer(EnvironmentConfigurationBuilder.configuration()
                .extensions().add(new RenderExtension()).and()
                .build());

        @Override
        protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            renderer.inlineDispatcherFor("Hello {% render '/hello' %}")
                    .render(request, response);
        }
    }

}