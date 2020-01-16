package com.scarlatti;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.File;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Alessandro Scarlatti
 * @since Wednesday, 1/15/2020
 */
public class VelocityUtils {

    private static VelocityEngine velocityEngine;
    private static Path tempDir;

    public static String renderFromRaw(String template, Map<String, Object> context) {
        try {
            if (velocityEngine == null)
                velocityEngine = velocityEngine();

            Path tempFile = Files.createTempFile(tempDir, "template", ".vt");
            Files.write(tempFile, template.getBytes());

            return renderFromFile(tempFile.getFileName().toString(), context);
        } catch (Exception e) {
            throw new RuntimeException("Error rendering template", e);
        }
    }

    public static String renderFromFile(String template, Map<String, Object> context) {
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
        tempDir = Files.createTempDirectory("velocity");
        System.out.println("Created temp dir " + tempDir.toAbsolutePath());
        Path defaultDir = Paths.get("velocity").toAbsolutePath();
        Files.createDirectories(defaultDir);
        List<File> dirs = new ArrayList<>();
        dirs.add(defaultDir.toFile());
        dirs.add(tempDir.toFile());
        velocityEngine.setProperty("file.resource.loader.path", String.join(",", dirs.stream().map(File::toString).collect(Collectors.toList())));
        velocityEngine.init();
        return velocityEngine;
    }
}
