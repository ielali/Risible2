package risible.core.dispatch;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created with IntelliJ IDEA.
 * User: Imad
 * Date: 06/11/13
 * Time: 22:07
 */
public class DispatcherContext {
    private String uri;
    private Map<Class<? extends Annotation>, TreeMap<String, Object>> parameters;

    public DispatcherContext(String uri, Map<Class<? extends Annotation>, TreeMap<String, Object>> parameters) {
        if (uri == null || uri.trim().equals("")) {
            throw new IllegalArgumentException("Argument uri can not be null");
        }
        this.uri = uri;
        this.parameters = parameters;
    }

    public String getUri() {
        return uri;
    }

    public Map<Class<? extends Annotation>, TreeMap<String, Object>> getParameters() {
        return parameters;
    }

    @Override
    public String toString() {
        return "DispatcherContext{" +
                ", uri='" + uri + '\'' +
                ", parameters=" + parameters +
                "} " + super.toString();
    }
}
