// Copyright 2008 Conan Dalton and Jean-Philippe Hallot
//
// This file is part of risible-web.
//
// risible-web is free software: you can redistribute it and/or modify
// it under the terms of version 3 of the GNU Lesser General Public License as published by
// the Free Software Foundation
//
// risible-db is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// Copies of the GNU General Public License and the GNU Lesser General Public License
// are distributed with this software, see /GPL.txt and /LGPL.txt at the
// root of this distribution.
//

package risible.core;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

public class PropertyPlaceholderConfigurerConfigurer implements InitializingBean, ServletContextAware {
    private String environmentFile;
    private ClassPathResource location;
    private String password = "foo";
    private String environmentName;
    private ServletContext servletContext;

    public void setEnvironmentFile(String environmentFile) {
        this.environmentFile = environmentFile;
    }

    public String getEnvironmentName() {
        return environmentName;
    }

    public String getPassword() {
        return password;
    }

    public ClassPathResource getLocation() {
        return location;
    }

    public void afterPropertiesSet() throws Exception {
        environmentName = System.getProperty("configName");
        if (environmentName == null) {
            File environment = new File(environmentFile);
            if (environment.exists()) {
                Properties p = new Properties();
                p.load(new FileInputStream(environment));
                password = p.getProperty("password");
                environmentName = p.getProperty("environment");
            } else {
                String message = "\nUnable to initialise application: Please provide either:\n" +
                        "- System Property -DconfigName=xxx to select a properties file under classpath://environments/${configName}.properties, or\n" +
                        "- a properties file at " + environmentFile + " containing the keys 'environment' and 'password', such that classpath://environments/${environment}.properties exists, and password decrypts any encrypted properties in this file";
                throw new RuntimeException(message);
            }
        }

        String path = "/environments/" + environmentName + ".properties";
        location = new ClassPathResource(path);
        logProperties(path);
        servletContext.setAttribute("environmentName", environmentName);
    }

    private void logProperties(String path) throws IOException {
        Properties p = new Properties();
        p.load(location.getInputStream());

        System.out.println(
                "*==================" +
                        " Configuration from " + path +
                        " (excluding passwords) " +
                        "==================*");
        Set keys = new TreeSet(p.keySet());
        for (Object key : keys) {
            if (!key.toString().toLowerCase().contains("password")) {
                Object value = p.get(key);
                System.out.println(StringUtils.rightPad((String) key, 35) + " : " + value);
            }
        }
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
