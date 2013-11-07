package risible.core.dispatch;

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
    private TreeMap<String, String> requestPrameters;
    private Map<String, String> headerPrameters;

    public DispatcherContext(String uri, TreeMap<String, String> requestPrameters, Map<String, String> headerPrameters) {
        if (uri == null || uri.trim().equals("")) {
            throw new IllegalArgumentException("Argument uri can not be null");
        }
        this.uri = uri;
        this.requestPrameters = requestPrameters;
        this.headerPrameters = headerPrameters;
    }

    public String getUri() {
        return uri;
    }

    public TreeMap<String, String> getRequestPrameters() {
        return requestPrameters;
    }

    public Map<String, String> getHeaderPrameters() {
        return headerPrameters;
    }

    @Override
    public String toString() {
        return "DispatcherContext{" +
                ", uri='" + uri + '\'' +
                ", requestPrameters=" + requestPrameters +
                ", headerPrameters=" + headerPrameters +
                "} " + super.toString();
    }
}
