import com.scarlatti.ThymeleafUtils
import org.junit.Test

import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * @author Alessandro Scarlatti
 * @since Wednesday , 1/15/2020
 */
class ThymeleafTemplateUtilsTest {

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
    <title>Report 2 from <span th:text="${continent}">continent</span></title>
</head>
<body>
<h1>Report from <span th:text="${continent}">continent</span></h1>
<ul>
    <li th:each="penguin: ${penguins}">
        <span th:text="${penguin.name}">name</span> is <span th:text="${penguin.age}">age</span> year(s) old
    </li>
</ul>
<div th:replace="fragment.html">The particle will go here</div>
</body>
</html>
'''

        String html = ThymeleafUtils.renderFromRaw(template,  context)

        Files.write(Paths.get("reports",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("'report-'yyyy-MM-dd'T'HH.mm.ss'.html'"))),
                html.getBytes());
    }

    @Test
    void test3() {
        def model = [
                continent: "South America",
                penguins: [
                        [name: "Annie2", age: 1],
                        [name: "Annie3", age: 2],
                        [name: "Annie4", age: 3],
                        [name: "Annie5", age: 4],
                ]
        ]

        String html = ThymeleafUtils.renderFromTemplate("complexReport.html", Paths.get("thymeleaf2"), model)

        ThymeleafUtils.render {
            template = "asdf"
            template = Paths.get("asdf.html")
            context = model
            templatesDir = Paths.get("asdf")
        }

        Files.write(Paths.get("reports",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("'report-'yyyy-MM-dd'T'HH.mm.ss'.html'"))),
                html.getBytes());
    }
}
