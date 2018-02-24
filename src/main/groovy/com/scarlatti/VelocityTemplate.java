package com.scarlatti;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.app.event.MethodExceptionEventHandler;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.StringWriter;
import java.util.concurrent.Callable;

/**
 * ______    __                         __           ____             __     __  __  _
 * ___/ _ | / /__ ___ ___ ___ ____  ___/ /______    / __/______ _____/ /__ _/ /_/ /_(_)
 * __/ __ |/ / -_|_-<(_-</ _ `/ _ \/ _  / __/ _ \  _\ \/ __/ _ `/ __/ / _ `/ __/ __/ /
 * /_/ |_/_/\__/___/___/\_,_/_//_/\_,_/_/  \___/ /___/\__/\_,_/_/ /_/\_,_/\__/\__/_/
 * Saturday, 2/24/2018
 */
public class VelocityTemplate {

    private VelocityContext context = new VelocityContext();
    private Template template;

    public VelocityTemplate(String resourceName) {
        VelocityEngine velocity = new VelocityEngine();
        velocity.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        velocity.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        velocity.setProperty("eventhandler.methodexception.class", ExceptionHandler.class.getName());
        velocity.init();

        velocity.getProperty("eventhandler.methodexception.class");

        template = velocity.getTemplate(resourceName);
    }

    /**
     * Build an html string from a template...
     *
     * @return the html string.
     */
    public String build() {

        try (StringWriter writer = new StringWriter()) {
            template.merge(context, writer);
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error filling out template withValue data: " + context, e);
        }
    }

    public VelocityTemplate withValue(String key, Object value) {
        context.put(key, value);
        return this;
    }

    public static class ExceptionHandler implements MethodExceptionEventHandler {
        @Override
        public Object methodException(Class claz, String method, Exception e) throws Exception {
            throw new RuntimeException(String.format("Error processing method %s on class %s", method, claz), e);
        }
    }
}
