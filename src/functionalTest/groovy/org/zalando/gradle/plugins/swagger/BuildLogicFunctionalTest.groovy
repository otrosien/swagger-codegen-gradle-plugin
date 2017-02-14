import org.gradle.testkit.runner.GradleRunner
import static org.gradle.testkit.runner.TaskOutcome.*
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Unroll

class BuildLogicFunctionalTest extends Specification {
    @Rule final TemporaryFolder testProjectDir = new TemporaryFolder()
    File buildFile

    def setup() {
        buildFile = testProjectDir.newFile('build.gradle')
        testProjectDir.newFolder('src','main','swagger-codegen')
        testProjectDir.newFile('src/main/swagger-codegen/swagger.yaml') << this.getClass().getResource( '/swagger.yaml' ).text
    }

    @Unroll
    def "can execute swaggerCodegen task with Gradle version #gradleVersion"() {
        given:
        buildFile << """
            plugins {
                id 'org.zalando.swagger-codegen'
            }
            swaggerCodegen {
                apiFile project.file('src/main/swagger-codegen/swagger.yaml')
                language 'jaxrsinterfaces'
                apiPackage 'com.example.project.api'
                modelPackage 'com.example.project.model'
            }

        """

        when:
        def result = GradleRunner.create()
            .withGradleVersion(gradleVersion)
            .withProjectDir(testProjectDir.root)
            .withArguments('swaggerCodegen','--info','--stacktrace')
            .withDebug(true)
            .withPluginClasspath()
            .build()

        then:
        ! result.output.contains('swaggerCodegen UP-TO-DATE')
        result.task(":swaggerCodegen").outcome == SUCCESS

        where:
        gradleVersion << ['3.3']
    }
}