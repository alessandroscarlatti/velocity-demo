package com.scarlatti;

import ch.qos.logback.classic.LoggerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ______    __                         __           ____             __     __  __  _
 * ___/ _ | / /__ ___ ___ ___ ____  ___/ /______    / __/______ _____/ /__ _/ /_/ /_(_)
 * __/ __ |/ / -_|_-<(_-</ _ `/ _ \/ _  / __/ _ \  _\ \/ __/ _ `/ __/ / _ `/ __/ __/ /
 * /_/ |_/_/\__/___/___/\_,_/_//_/\_,_/_/  \___/ /___/\__/\_,_/_/ /_/\_,_/\__/\__/_/
 * Saturday, 2/24/2018
 */
public class LoggingWithVelocityDemo {

    private static final Logger log = LoggerFactory.getLogger(LoggingWithVelocityDemo.class);

    public static void main(String[] args) throws Exception {

        CapturingAppender capturingAppender = newCapturingAppender();

        log.info("hello there {}", "phil");
        log.error("oh no, {}!", "annie", new RuntimeException("what happened?"));

        String html = new VelocityTemplate("loggingReport.vt")
            .withValue("logs", capturingAppender.getLogs()).build();

        Files.write(Paths.get("C:\\Users\\pc\\IdeaProjects\\velocity-demo", "reports",
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("'report-'yyyy-MM-dd'T'HH.mm.ss'.html'"))),
            html.getBytes());
    }

    private static CapturingAppender newCapturingAppender() {
        CapturingAppender capturingAppender = new CapturingAppender();
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        capturingAppender.setContext(lc);
        capturingAppender.start();
        ((ch.qos.logback.classic.Logger) log).addAppender(capturingAppender);

        return capturingAppender;
    }
}
