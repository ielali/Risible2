package risible.freemarker;

import risible.core.Configuration;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created with IntelliJ IDEA.
 * User: Imad
 * Date: 24/11/13
 * Time: 22:06
 */
@Named("freemarkerConfiguration")
public class FreemarkerConfiguration extends freemarker.template.Configuration {
    @Inject
    private Configuration configuration;
    @Inject
    private ClasspathTemplateLoader classpathTemplateLoader;
    @Inject
    private SeriousBeanModelObjectWrapper beanModelObjectWrapper;

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public void setClasspathTemplateLoader(ClasspathTemplateLoader classpathTemplateLoader) {
        this.classpathTemplateLoader = classpathTemplateLoader;
    }

    public void setBeanModelObjectWrapper(SeriousBeanModelObjectWrapper beanModelObjectWrapper) {
        this.beanModelObjectWrapper = beanModelObjectWrapper;
    }

    @PostConstruct
    private void init() {
        setTemplateLoader(classpathTemplateLoader);
        setEncoding(configuration.getLocale(), configuration.getFreemarkerCharacterEncoding());
        setTagSyntax(configuration.getFreemarkerTagSyntax());
        setBeanModelObjectWrapper(beanModelObjectWrapper);
    }
}
