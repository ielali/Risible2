package risible.core.dispatch;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import risible.core.Configuration;
import risible.core.MediaType;
import risible.core.annotations.ModelParam;
import risible.core.annotations.UsesInvoker;

import javax.inject.Inject;
import javax.inject.Named;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Imad
 * Date: 06/11/13
 * Time: 21:54
 */
@Named("dispatcher")
public class Dispatcher {
    private final Logger log = Logger.getLogger(Dispatcher.class);
    private ConfigurableListableBeanFactory beanFactory;
    @Inject
    private Map<String, Invoker> invokers;
    @Inject
    private Configuration configuration;

    public void setActionInvokers(Map<String, Invoker> invokers) {
        this.invokers = invokers;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    @Inject
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.beanFactory = ((ConfigurableApplicationContext) applicationContext).getBeanFactory();

    }

    public DispatcherResult dispatch(DispatcherContext context) {
        if (context == null) {
            throw new IllegalArgumentException("Argument uri can not be null");
        }
        Throwable throwable = null;
        Invocation invocation = null;
        String actionName = null;
        Object result = null;
        Object controller = null;
        MediaType mediaType = MediaType.TEXT_HTML_TYPE;
        try {
            invocation = createInvocation(context);
            controller = createController(invocation);
            Method action = invocation.getActionMethod();
            Invoker invoker = getInvoker(action);
            actionName = action.getName();
            mediaType = invocation.getMediaType();
            result = invoker.invoke(controller, invocation, context.getParameters());
        } catch (Throwable e) {
            throwable = e;
        }

        return new DispatcherResult(controller, actionName, result, context.getParameters().get(ModelParam.class), mediaType, throwable);
    }

    private Invocation createInvocation(DispatcherContext context) throws InvocationFailed {
        return Invocation.create(configuration.getControllerPackageName(), context.getUri());
    }

    private Object createController(Invocation invocation) {
        return beanFactory.autowire(invocation.getTargetClass(), AutowireCapableBeanFactory.AUTOWIRE_BY_NAME, false);
    }

    private Invoker getInvoker(Method action) {
        String invokerName = "defaultActionInvoker";
        if (action != null && action.getAnnotation(UsesInvoker.class) != null) {
            invokerName = action.getAnnotation(UsesInvoker.class).value();
        }
        return invokers.get(invokerName);
    }

}
