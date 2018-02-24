package com.scarlatti;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.app.event.MethodExceptionEventHandler;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.Map;
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
        velocity.setProperty("runtime.references.strict", true);
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
            throw new RuntimeException(String.format("Error filling out template within context: %n%s", printContext(context)), e);
        }
    }

    public VelocityTemplate withValue(String key, Object value) {
        context.put(key, value);
        return this;
    }

    private String printContext(VelocityContext context) {
        StringBuilder sb = new StringBuilder(String.format("VelocityContext%n"));
        sb.append(String.format("%-60s %s%n", "key", "value"));
        for (Object key : context.getKeys()) {
            sb.append(String.format("%-60s %s%n", key, context.get((String)key)));
        }

        return sb.toString();
    }
}
