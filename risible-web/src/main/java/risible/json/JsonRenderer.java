package risible.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import risible.core.MediaType;
import risible.core.annotations.Renders;
import risible.core.render.Renderer;
import risible.core.render.RendererContext;

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
    public void afterPropertiesSet() throws Exception {
        gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Throwable.class, new ExceptionSerializer());
    }

    @Override
    public void render(RendererContext context) throws Exception {
        Object object = context.getThrowable() != null ? context.getThrowable() : context.getResult();
        try {
            Gson gson = gsonBuilder.create();
            context.getWriter().write(gson.toJson(object));
            context.getWriter().flush();
        } catch (Throwable t) {
            log.error("Error rendering json " + object, t);
            throw new Exception(t);
        }
    }
}
