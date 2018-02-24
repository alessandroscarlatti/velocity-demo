import com.scarlatti.Penguin;
import com.scarlatti.DemoVelocityPrinter;
import com.scarlatti.VelocityTemplate;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

/**
 * ______    __                         __           ____             __     __  __  _
 * ___/ _ | / /__ ___ ___ ___ ____  ___/ /______    / __/______ _____/ /__ _/ /_/ /_(_)
 * __/ __ |/ / -_|_-<(_-</ _ `/ _ \/ _  / __/ _ \  _\ \/ __/ _ `/ __/ / _ `/ __/ __/ /
 * /_/ |_/_/\__/___/___/\_,_/_//_/\_,_/_/  \___/ /___/\__/\_,_/_/ /_/\_,_/\__/\__/_/
 * Saturday, 2/24/2018
 */
public class Demo {

    private String projectDir;

    @Before
    public void setup() {
        try {
            projectDir = System.getProperty("com.scarlatti.project.dir");
            Files.createDirectories(Paths.get(projectDir, "reports"));
        } catch (Exception e) {
            throw new RuntimeException("Error setting up project directories", e);
        }
    }

    @Test
    public void createHardCodedReport() throws Exception {
        String html = new DemoVelocityPrinter().call();
        Files.write(Paths.get(projectDir, "reports",
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("'report-'yyyy-MM-dd'T'HH.mm.ss'.html'"))),
            html.getBytes());
    }

    @Test
    public void createReport() throws Exception {
        String html = new VelocityTemplate("report.vt")
            .withValue("continent", "Africa")
            .withValue("penguin", new Penguin("Annie", 1))
            .build();

        Files.write(Paths.get(projectDir, "reports",
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("'report-'yyyy-MM-dd'T'HH.mm.ss'.html'"))),
            html.getBytes());
    }

    @Test
    public void createComplexReport() throws Exception {
        String html = new VelocityTemplate("complexReport.vt")
            .withValue("continent", "Africa")
            .withValue("penguin", new Penguin("Annie", 1))
            .build();

        Files.write(Paths.get(projectDir, "reports",
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("'report-'yyyy-MM-dd'T'HH.mm.ss'.html'"))),
            html.getBytes());
    }


    @Test
    public void createListReport() throws Exception {
        String html = new VelocityTemplate("forEachDemo.vt")
            .withValue("continent", "South America")
            .withValue("penguins", Arrays.asList(
                new Penguin("Annie", 1),
                new Penguin("Phil", 5),
                new Penguin("Charlotte", 8)
            ))
            .build();

        Files.write(Paths.get(projectDir, "reports",
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("'report-'yyyy-MM-dd'T'HH.mm.ss'.html'"))),
            html.getBytes());
    }

    @Test
    public void createListReportThatFails() throws Exception {
        String html = new VelocityTemplate("forEachDemoThatFails.vt")
            .withValue("continent", "South America")
            .withValue("penguins", Arrays.asList(
                new Penguin("Annie", 1),
                new Penguin("Phil", 5),
                new Penguin("Charlotte", 8)
            ))
            .build();

        Files.write(Paths.get(projectDir, "reports",
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("'report-'yyyy-MM-dd'T'HH.mm.ss'.html'"))),
            html.getBytes());
    }
}
