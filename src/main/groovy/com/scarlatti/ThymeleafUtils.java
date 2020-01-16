package com.scarlatti;

import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.IContext;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresource.ITemplateResource;
import org.thymeleaf.templateresource.StringTemplateResource;

import java.nio.file.Paths;
import java.util.*;

/**
 * @author Alessandro Scarlatti
 * @since Wednesday, 1/15/2020
 */
public class ThymeleafUtils {

    private static SpringTemplateEngine springTemplateEngine;

    public static String renderFromRaw(String template, Map<String, Object> context) {
        try {
            if (springTemplateEngine == null)
                springTemplateEngine = springTemplateEngine();

            String id = UUID.randomUUID().toString();
            StringTemplateResolver.addRawTemplate(id, template);
            String rendered = renderFromTemplate(id, context);
            StringTemplateResolver.removeRawTemplate(id);
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
            if (springTemplateEngine == null)
                springTemplateEngine = springTemplateEngine();

            IContext thymeleafContext = new MapThymeleafContext(context);

            return springTemplateEngine.process(template, thymeleafContext);
        } catch (Exception e) {
            throw new RuntimeException("Error rendering template", e);
        }
    }

    private static SpringTemplateEngine springTemplateEngine() throws Exception {
        SpringTemplateEngine springTemplateEngine = new SpringTemplateEngine();
        springTemplateEngine.addTemplateResolver(fileTemplateResolver());
        springTemplateEngine.addTemplateResolver(new StringTemplateResolver());
        return springTemplateEngine;
    }

    private static ITemplateResolver fileTemplateResolver() {
        FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setPrefix(Paths.get("thymeleaf").toAbsolutePath().toString() + "/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setOrder(2);
        resolver.setCacheable(true);
        return resolver;
    }

    public static class StringTemplateResolver extends AbstractConfigurableTemplateResolver {

        private static final Map<String, String> templates = new HashMap<>();

        public static void addRawTemplate(String id, String contents) {
            templates.put(id, contents);
        }

        public static void removeRawTemplate(String id) {
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

    public static class MapThymeleafContext implements IContext {

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
