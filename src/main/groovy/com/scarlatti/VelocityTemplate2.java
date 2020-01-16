package com.scarlatti;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.runtime.resource.loader.FileResourceLoader;

import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * @author Alessandro Scarlatti
 * @since Wednesday, 1/15/2020
 */
public class VelocityTemplate2 {
    private VelocityEngine velocity = new VelocityEngine();
    private Template template;
    private String rawTemplate;
    private boolean initialized;
    private boolean useClasspathResources;
    private boolean useFileResources;
    private boolean useRawTemplate;
    private Path resourcesDir;
    private String velocityTemplateName;

    public static VelocityTemplate2 fromFile(Path templateFile) {
        VelocityTemplate2 velocityTemplate = new VelocityTemplate2();
        velocityTemplate.useFileResources = true;
        velocityTemplate.resourcesDir = templateFile.getParent();
        velocityTemplate.velocityTemplateName = templateFile.getFileName().toString();
        return velocityTemplate;
    }

    public static VelocityTemplate2 fromClasspath(String resourcePath) {
        VelocityTemplate2 velocityTemplate = new VelocityTemplate2();
        velocityTemplate.useClasspathResources = true;
        velocityTemplate.velocityTemplateName = resourcePath;
        return velocityTemplate;
    }

    public static VelocityTemplate2 fromString(String rawTemplate) {
        VelocityTemplate2 velocityTemplate = new VelocityTemplate2();
        velocityTemplate.useRawTemplate = true;
        velocityTemplate.rawTemplate = rawTemplate;
        velocityTemplate.velocityTemplateName = "template.vt";
        return velocityTemplate;
    }

    private void init() {
        try {
            if (useClasspathResources) {
                velocity.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
                velocity.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
            }

            if (useFileResources) {
                velocity.setProperty(RuntimeConstants.RESOURCE_LOADER, "file");
                velocity.setProperty("file.resource.loader.class", FileResourceLoader.class.getName());
                velocity.setProperty("file.resource.loader.path", resourcesDir.toAbsolutePath().toString());
                velocity.setProperty("file.resource.loader.cache", true);
                template = velocity.getTemplate(velocityTemplateName);
            }

            if (useRawTemplate) {
                Path tempDir = Files.createTempDirectory("velocity");
                Files.write(tempDir.resolve("template.vt"), rawTemplate.getBytes());
                velocity.setProperty(RuntimeConstants.RESOURCE_LOADER, "file");
                velocity.setProperty("file.resource.loader.class", FileResourceLoader.class.getName());
                velocity.setProperty("file.resource.loader.path", tempDir.toAbsolutePath().toString());
                velocity.setProperty("file.resource.loader.cache", true);
            }

            velocity.setProperty("runtime.references.strict", true);
            velocity.getProperty("eventhandler.methodexception.class");

            template = velocity.getTemplate(velocityTemplateName);

            initialized = true;
        } catch (Exception e) {
            throw new RuntimeException("Error initializing velocity", e);
        }
    }

    public String render(Map<String, Object> context) {
        if (!initialized)
            init();

        VelocityContext velocityContext = new VelocityContext(context);

        StringWriter writer = new StringWriter();

        template.merge(velocityContext, writer);

        return writer.toString();
    }
}
