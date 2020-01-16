import com.scarlatti.Penguin
import com.scarlatti.VelocityTemplate2
import com.scarlatti.VelocityUtils
import org.junit.Test

import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * @author Alessandro Scarlatti
 * @since Wednesday , 1/15/2020
 */
class VelocityTemplate2Test {

    @Test
    void test() {

        VelocityTemplate2 template = VelocityTemplate2.fromFile(Paths.get("src/main/resources/forEachDemo.vt"))

        def context = [
                continent: "South America",
                penguins: [
                        new Penguin("Annie", 1),
                        new Penguin("Phil", 5),
                        new Penguin("Charlotte", 8)
                ]
        ]

        Files.write(Paths.get("reports",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("'report-'yyyy-MM-dd'T'HH.mm.ss'.html'"))),
                template.render(context).getBytes());
    }

    @Test
    void test2() {
        def context = [
                continent: "South America",
                penguins: [
                        [name: "Annie2", age: 1],
                        [name: "Annie3", age: 2],
                        [name: "Annie4", age: 3],
                        [name: "Annie5", age: 4],
                ]
        ]

        String template = '''
<html>
<head>
    <title>Report 2 from $continent</title>
</head>
<body>
<h1>Report from $continent</h1>
<ul>
    #foreach($penguin in $penguins)
    <li> $penguin.name is $penguin.age year(s) old</li>
    #end
</ul>
</body>
</html>
'''

        String html = VelocityUtils.renderFromRaw(template, context)

        Files.write(Paths.get("reports",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("'report-'yyyy-MM-dd'T'HH.mm.ss'.html'"))),
                html.getBytes());
    }

    @Test
    void test3() {
        def context = [
                continent: "South America",
                penguins: [
                        [name: "Annie2", age: 1],
                        [name: "Annie3", age: 2],
                        [name: "Annie4", age: 3],
                        [name: "Annie5", age: 4],
                ]
        ]

        String html = VelocityUtils.renderFromFile("complexReport.vt", context)

        Files.write(Paths.get("reports",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("'report-'yyyy-MM-dd'T'HH.mm.ss'.html'"))),
                html.getBytes());
    }
}
