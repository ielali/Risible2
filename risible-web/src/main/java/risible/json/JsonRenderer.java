package risible.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import risible.core.MediaType;
import risible.core.dispatch.NotFound;
import risible.core.render.Renderer;
import risible.core.annotations.Renders;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Created with IntelliJ IDEA.
 * User: Imad
 * Date: 10/15/13
 * Time: 4:43 PM
 * To change this template use File | Settings | File Templates.
 */
@Renders(MediaType.APPLICATION_JSON)
public class JsonRenderer implements Renderer, InitializingBean {
    private final Logger log = Logger.getLogger(JsonRenderer.class);
    private GsonBuilder gsonBuilder;

    @Override
    public void renderResult(HttpServletRequest req, HttpServletResponse res, Object controller, String action, Object result) throws Exception {
        renderAsJson(res, result);
    }

    @Override
    public void renderException(HttpServletRequest req, HttpServletResponse res, Object controller, String action, Throwable throwable) throws Exception {
        if (throwable instanceof NotFound) {
            res.setStatus(404);
        }
        renderAsJson(res, throwable);
    }

    private void renderAsJson(HttpServletResponse res, Object result) throws IOException {
        try {
            Writer writer = createWriter(res);
            Gson gson = gsonBuilder.create();
            writer.write(gson.toJson(result));
            writer.flush();
        } catch (Throwable t) {
            log.error("Error rendering json " + result, t);
            throw new RuntimeException(t);
        }
    }

    private Writer createWriter(ServletResponse res) throws IOException {
        return new OutputStreamWriter(res.getOutputStream());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Throwable.class, new ExceptionSerializer());
    }
}
