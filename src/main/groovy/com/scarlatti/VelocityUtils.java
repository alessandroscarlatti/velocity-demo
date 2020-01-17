package com.scarlatti;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Alessandro Scarlatti
 * @since Wednesday, 1/15/2020
 */
public class VelocityUtils {

    private static VelocityEngine velocityEngine;
    public static Path velocityTemplatesDir = Paths.get("velocity");

    public static String renderFromRaw(String template, Map<String, Object> context) {
        try {
            if (velocityEngine == null)
                velocityEngine = velocityEngine();

            String id = UUID.randomUUID().toString();
            StringResourceLoader.addRawTemplate(id, template);
            String rendered = renderFromTemplate(id, context);
            StringResourceLoader.removeRawTemplate(id);
            return rendered;
        } catch (Exception e) {
            throw new RuntimeException("Error rendering template", e);
        }
    }

    /**
     * Render from the named template.
     * @param template the named template, eg, template1.vt
     * @param context the context to use
     * @return the rendered template
     */
    public static String renderFromTemplate(String template, Map<String, Object> context) {
        try {
            if (velocityEngine == null)
                velocityEngine = velocityEngine();

            Template velocityTemplate = velocityEngine.getTemplate(template);
            VelocityContext velocityContext = new VelocityContext(context);
            StringWriter writer = new StringWriter();
            velocityTemplate.merge(velocityContext, writer);
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error rendering template", e);
        }
    }

    private static VelocityEngine velocityEngine() throws Exception {
        VelocityEngine velocityEngine = new VelocityEngine();
        Path defaultDir = velocityTemplatesDir.toAbsolutePath();
        Files.createDirectories(defaultDir);
        List<File> dirs = new ArrayList<>();
        dirs.add(defaultDir.toFile());
        velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "file,raw");
        velocityEngine.setProperty("file.resource.loader.path", String.join(",", dirs.stream().map(File::toString).collect(Collectors.toList())));
        velocityEngine.setProperty("raw.resource.loader.class", StringResourceLoader.class.getName());
        velocityEngine.init();
        return velocityEngine;
    }

    public static class StringResourceLoader extends ResourceLoader {

        private static final Map<String, String> templates = new HashMap<>();

        public static void addRawTemplate(String id, String contents) {
            templates.put(id, contents);
        }

        public static void removeRawTemplate(String id) {
            templates.remove(id);
        }

        @Override
        public void init(ExtendedProperties configuration) {
        }

        @Override
        public InputStream getResourceStream(String source) throws ResourceNotFoundException {
            String rawTemplate = templates.get(source);
            if (rawTemplate == null)
                return null;
            else
                return new ByteArrayInputStream(rawTemplate.getBytes());
        }

        @Override
        public boolean isSourceModified(Resource resource) {
            return false;
        }

        @Override
        public long getLastModified(Resource resource) {
            return 0;
        }
    }
}
