package risible.demo.app.servlet;

import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.servlet.AllHttpScopesHashModel;
import freemarker.ext.servlet.FreemarkerServlet;
import freemarker.ext.servlet.ServletContextHashModel;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class TemplateServlet extends FreemarkerServlet {

    @Override
    public void init() throws ServletException {
        super.init();
        getConfiguration().setTagSyntax(getBackEndFreemarkerConfiguration().getTagSyntax());
    }

    @Override
    protected ObjectWrapper createObjectWrapper() {
        return getBackEndFreemarkerConfiguration().getObjectWrapper();
    }

    @Override
    protected TemplateModel createModel(ObjectWrapper wrapper, ServletContext servletContext, HttpServletRequest request, HttpServletResponse response) throws TemplateModelException {
        AllHttpScopesHashModel templateModel = (AllHttpScopesHashModel) super.createModel(wrapper, servletContext, request, response);
        templateModel.put("request",new BeanModel(request, (BeansWrapper) getObjectWrapper()));
        return templateModel;
    }

    private Configuration getBackEndFreemarkerConfiguration() {
        WebApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
        return applicationContext.getBean(Configuration.class);
    }
}
