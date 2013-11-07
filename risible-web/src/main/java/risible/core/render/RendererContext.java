package risible.core.render;

import java.io.Writer;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Imad
 * Date: 06/11/13
 * Time: 23:07
 */
public class RendererContext {
    private Object controller;
    private String action;
    private Object result;
    private Throwable throwable;
    private Map<String, Object> renderingParameters;
    private Writer writer;

    public RendererContext(Object controller, String action, Object result, Throwable throwable, Map<String, Object> renderingParameters, Writer writer) {
        this.controller = controller;
        this.action = action;
        this.result = result;
        this.throwable = throwable;
        this.renderingParameters = renderingParameters;
        this.writer = writer;
    }

    public Object getController() {
        return controller;
    }

    public String getAction() {
        return action;
    }

    public Object getResult() {
        return result;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public Map<String, Object> getRenderingParameters() {
        return renderingParameters;
    }

    public Writer getWriter() {
        return writer;
    }

    @Override
    public String toString() {
        return "RendererContext{" +
                "controller=" + controller +
                ", action='" + action + '\'' +
                ", result=" + result +
                ", throwable=" + throwable +
                ", renderingParameters=" + renderingParameters +
                ", writer=" + writer +
                "} " + super.toString();
    }
}
