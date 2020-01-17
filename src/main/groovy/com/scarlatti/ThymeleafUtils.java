package com.scarlatti;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import org.slf4j.LoggerFactory;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.IContext;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.templateresource.StringTemplateResource;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * @author Alessandro Scarlatti
 * @since Wednesday, 1/15/2020
 */
public class ThymeleafUtils {

    private static Map<String, ThymeleafTemplateUtil> utils = new HashMap<>();
    public static Path defaultThymeleafTemplatesDir = Paths.get("thymeleaf");

    public static String render(@DelegatesTo(ThymeleafSpec.class) Closure config) {
        ThymeleafSpec spec = new ThymeleafSpec();
        config.setDelegate(spec);
        config.setResolveStrategy(Closure.DELEGATE_FIRST);
        config.call();

        return "Asdf";
    }

    public static String renderFromRaw(String template, Map<String, Object> context) {
        try {
            return renderFromRaw(template, defaultThymeleafTemplatesDir, context);
        } catch (Exception e) {
            throw new RuntimeException("Error rendering template", e);
        }
    }

    public static String renderFromRaw(String template, Path templatesDir, Map<String, Object> context) {
        try {
            String id = templatesDir.toAbsolutePath().toString();
            if (!utils.containsKey(id)) {
                utils.put(id, new ThymeleafTemplateUtil(templatesDir));
            }

            return utils.get(id).renderFromRaw(template, context);
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
            return renderFromTemplate(template, defaultThymeleafTemplatesDir, context);
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
    public static String renderFromTemplate(String template, Path templatesDir, Map<String, Object> context) {
        try {
            String id = templatesDir.toAbsolutePath().toString();
            if (!utils.containsKey(id)) {
                utils.put(id, new ThymeleafTemplateUtil(templatesDir));
            }

            return utils.get(id).renderFromTemplate(template, context);
        } catch (Exception e) {
            throw new RuntimeException("Error rendering template", e);
        }
    }

    // todo this is still a work in progress
    // todo not sure of the api just yet
    public static class ThymeleafSpec {
        private String rawTemplate;
        private Path templateFile;
        private Path templatesDir;
        private Map<String, Object> context;

        public String getRawTemplate() {
            return rawTemplate;
        }

        public void setRawTemplate(String rawTemplate) {
            this.rawTemplate = rawTemplate;
        }

        public Path getTemplateFile() {
            return templateFile;
        }

        public void setTemplateFile(Path templateFile) {
            this.templateFile = templateFile;
        }

        public void setTemplate(Path template) {
            this.templateFile = template;
        }

        public void setTemplate(String template) {
            this.rawTemplate = template;
        }

        public Path getTemplatesDir() {
            return templatesDir;
        }

        public void setTemplatesDir(Path templatesDir) {
            this.templatesDir = templatesDir;
        }

        public Map<String, Object> getContext() {
            return context;
        }

        public void setContext(Map<String, Object> context) {
            this.context = context;
        }
    }

    private static class ThymeleafTemplateUtil {
        private Path templatesDir;
        private SpringTemplateEngine springTemplateEngine;
        private StringTemplateResolver stringTemplateResolver;
        private FileTemplateResolver fileTemplateResolver;

        private ThymeleafTemplateUtil(Path templatesDir) {
            this.templatesDir = templatesDir;
        }

        private void initThymeleaf() {
            try {
                if (stringTemplateResolver == null)
                    stringTemplateResolver = new StringTemplateResolver();
                if (fileTemplateResolver == null)
                    fileTemplateResolver = fileTemplateResolver();
                if (springTemplateEngine == null)
                    springTemplateEngine = springTemplateEngine();
            } catch (Exception e) {
                throw new RuntimeException("Error initializing Thymeleaf.", e);
            }
        }

        private String renderFromRaw(String template, Map<String, Object> context) {
            try {
                initThymeleaf();

                String id = UUID.randomUUID().toString();
                stringTemplateResolver.addRawTemplate(id, template);
                String rendered = renderFromTemplate(id, context);
                stringTemplateResolver.removeRawTemplate(id);
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
        private String renderFromTemplate(String template, Map<String, Object> context) {
            try {
                initThymeleaf();
                IContext thymeleafContext = new MapThymeleafContext(context);
                return springTemplateEngine.process(template, thymeleafContext);
            } catch (Exception e) {
                throw new RuntimeException("Error rendering template", e);
            }
        }

        private SpringTemplateEngine springTemplateEngine() throws Exception {
            SpringTemplateEngine springTemplateEngine = new SpringTemplateEngine();

            // resolve from file location
            springTemplateEngine.addTemplateResolver(fileTemplateResolver);

            // resolve from raw string
            springTemplateEngine.addTemplateResolver(stringTemplateResolver);

            // suppress logging for thymeleaf
            Logger root = (Logger)LoggerFactory.getLogger("org.thymeleaf");
            root.setLevel(Level.INFO);

            return springTemplateEngine;
        }

        private FileTemplateResolver fileTemplateResolver() {
            FileTemplateResolver resolver = new FileTemplateResolver();
            resolver.setPrefix(templatesDir.toAbsolutePath() + "/");
            resolver.setSuffix(".html");
            resolver.setOrder(2);
            resolver.setCacheable(true);
            return resolver;
        }

        private class StringTemplateResolver extends AbstractConfigurableTemplateResolver {

            private final Map<String, String> templates = new HashMap<>();

            public void addRawTemplate(String id, String contents) {
                templates.put(id, contents);
            }

            public void removeRawTemplate(String id) {
                templates.remove(id);
            }

            public StringTemplateResolver() {
                setOrder(1);
            }

            @Override
            protected ITemplateResource computeTemplateResource(IEngineConfiguration configuration, String ownerTemplate, String template, String resourceName, String characterEncoding, Map<String, Object> templateResolutionAttributes) {
                String rawTemplate = templates.get(template);
                if (rawTemplate == null)
                    return null;
                else
                    return new StringTemplateResource(rawTemplate);
            }
        }

        private static class MapThymeleafContext implements IContext {

            private Map<String, Object> context;

            public MapThymeleafContext(Map<String, Object> context) {
                this.context = context;
            }

            @Override
            public Locale getLocale() {
                return Locale.getDefault();
            }

            @Override
            public boolean containsVariable(String name) {
                return context.containsKey(name);
            }

            @Override
            public Set<String> getVariableNames() {
                return context.keySet();
            }

            @Override
            public Object getVariable(String name) {
                return context.get(name);
            }
        }
    }
}
