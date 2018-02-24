package com.scarlatti;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
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
public class DemoVelocityPrinter implements Callable<String> {

    /**
     * Build an html string from a template...
     *
     * @return the html string.
     */
    @Override
    public String call() {

        VelocityEngine velocity = new VelocityEngine();
        velocity.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        velocity.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        velocity.init();

        Template template = velocity.getTemplate("report.vt");

        VelocityContext context = new VelocityContext();
        context.put("continent", "Antarctica");
        context.put("penguin", new Puffin("phil", 6));

        try (StringWriter writer = new StringWriter()) {
            template.merge(context, writer);
            return writer.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error filling out template withValue data: " + context, e);
        }
    }

    /**
     * private static class will not work!
     * Must be public.
     */
    public static class Puffin {
        private String name;
        private int age;

        public Puffin(String name, int age) {
            this.name = name;
            this.age = age;
        }

        @Override
        public String toString() {
            return "Puffin{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }
}
