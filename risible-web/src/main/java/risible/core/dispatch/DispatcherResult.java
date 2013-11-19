package risible.core.dispatch;

import risible.core.MediaType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Imad
 * Date: 06/11/13
 * Time: 23:10
 */
public class DispatcherResult {
    private Object controller;
    private String action;
    private Object result;
    private Map<String, Map> modelParameters = new HashMap<String, Map>();
    private MediaType mediaType;
    private Throwable throwable;

    public DispatcherResult(Object controller, String action, Object result, Map<String, Object> modelParameters, MediaType mediaType, Throwable throwable) {
        this.controller = controller;
        this.action = action;
        this.result = result;
        if (modelParameters != null) {
            for (String key : modelParameters.keySet()) {
                Object value = modelParameters.get(key);
                if (value != null && value instanceof Map) {
                    this.modelParameters.put(key, (Map) value);
                }
            }
        }
        this.mediaType = mediaType;
        this.throwable = throwable;
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

    public Map<String, Map> getModelParameters() {
        return modelParameters;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    @Override
    public String toString() {
        return "DispatcherResult{" +
                "controller=" + controller +
                ", action='" + action + '\'' +
                ", result=" + result +
                ", mediaType=" + mediaType +
                ", throwable=" + throwable +
                "} " + super.toString();
    }
}
