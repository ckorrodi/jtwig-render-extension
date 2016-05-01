package org.jtwig.render.node;

import org.jtwig.escape.NoneEscapeEngine;
import org.jtwig.render.RenderRequest;
import org.jtwig.render.model.RenderNode;
import org.jtwig.render.node.renderer.NodeRender;
import org.jtwig.renderable.Renderable;
import org.jtwig.renderable.impl.StringRenderable;
import org.jtwig.util.ErrorMessageFormatter;
import org.jtwig.value.WrappedCollection;
import org.jtwig.web.servlet.ServletRequestHolder;
import org.jtwig.web.servlet.ServletResponseHolder;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

public class RenderNodeRender implements NodeRender<RenderNode> {
    @Override
    public Renderable render(RenderRequest request, RenderNode node) {
        Object calculate = request.getEnvironment().getRenderEnvironment().getCalculateExpressionService().calculate(request, node.getPathExpression());
        String path = request.getEnvironment().getValueEnvironment().getStringConverter().convert(calculate);
        HttpServletRequest httpServletRequest = getHttpServletRequest();

        if (node.getWithExpression().isPresent()) {
            Object mapValue = request.getEnvironment().getRenderEnvironment().getCalculateExpressionService().calculate(request, node.getWithExpression().get());
            WrappedCollection entries = request.getEnvironment().getValueEnvironment().getCollectionConverter().convert(mapValue)
                    .orThrow(node.getPosition(), String.format("Expecting a map value but got '%s'", mapValue));
            for (Map.Entry<String, Object> entry : entries) {
                if (entry.getKey() != null) {
                    httpServletRequest.setAttribute(entry.getKey(), entry.getValue());
                }
            }
        }

        InMemoryHttpServletResponse response = new InMemoryHttpServletResponse(getHttpServletResponse());

        try {
            httpServletRequest.getRequestDispatcher(path).include(httpServletRequest, response);
            return new StringRenderable(new String(response.getContent()), NoneEscapeEngine.instance());
        } catch (ServletException | IOException e) {
            throw new IllegalArgumentException(ErrorMessageFormatter.errorMessage(node.getPosition(), "Unable to render"), e);
        }
    }



    protected HttpServletResponse getHttpServletResponse() {
        return ServletResponseHolder.get();
    }

    protected HttpServletRequest getHttpServletRequest() {
        return ServletRequestHolder.get();
    }

    public static class InMemoryHttpServletResponse extends HttpServletResponseWrapper {
        private final InMemoryServletOutputStream outputStream = new InMemoryServletOutputStream();
        private final PrintWriter writer = new PrintWriter(outputStream);

        /**
         * Constructs a response adaptor wrapping the given response.
         *
         * @param response
         * @throws IllegalArgumentException if the response is null
         */
        public InMemoryHttpServletResponse(HttpServletResponse response) {
            super(response);
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            return outputStream;
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            return writer;
        }

        public byte[] getContent () {
            return outputStream.getMemory();
        }
    }

    public static class InMemoryServletOutputStream extends ServletOutputStream {
        private ByteArrayOutputStream memory;

        public InMemoryServletOutputStream() {
            this.memory = new ByteArrayOutputStream(1024);
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {

        }

        @Override
        public void write(int b) throws IOException {
            memory.write(b);
        }

        public byte[] getMemory() {
            return memory.toByteArray();
        }

    }
}
