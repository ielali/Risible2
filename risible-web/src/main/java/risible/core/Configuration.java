package risible.core;

import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: Imad
 * Date: 20/11/13
 * Time: 23:28
 */
public class Configuration {
    private String controllerPackageName = "risible.controller";
    private int freemarkerTagSyntax = 2;
    private String freemarkerCharacterEncoding = "UTF-8";
    private Locale locale=Locale.ENGLISH;

    public String getControllerPackageName() {
        return controllerPackageName;
    }

    public int getFreemarkerTagSyntax() {
        return freemarkerTagSyntax;
    }

    public String getFreemarkerCharacterEncoding() {
        return freemarkerCharacterEncoding;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setControllerPackageName(String controllerPackageName) {
        this.controllerPackageName = controllerPackageName;
    }

    public void setFreemarkerTagSyntax(int freemarkerTagSyntax) {
        this.freemarkerTagSyntax = freemarkerTagSyntax;
    }

    public void setFreemarkerCharacterEncoding(String freemarkerCharacterEncoding) {
        this.freemarkerCharacterEncoding = freemarkerCharacterEncoding;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }
}
