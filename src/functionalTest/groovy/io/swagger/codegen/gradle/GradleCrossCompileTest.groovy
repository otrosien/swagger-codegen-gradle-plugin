package io.swagger.codegen.gradle;
/**
 * Copyright (C) 2015-2017 Zalando SE (http://tech.zalando.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import org.gradle.testkit.runner.GradleRunner
import static org.gradle.testkit.runner.TaskOutcome.*
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Unroll

class GradleCrossCompileTest extends Specification {
    @Rule final TemporaryFolder testProjectDir = new TemporaryFolder()
    File buildFile

    def setup() {
        buildFile = testProjectDir.newFile('build.gradle')
        testProjectDir.newFolder('src','main','swagger-codegen')
        testProjectDir.newFile('src/main/swagger-codegen/swagger.yaml') << GradleCrossCompileTest.getResource( '/swagger.yaml' ).text
    }

    @Unroll
    def "can execute swaggerCodegen task with Gradle version #gradleVersion"() {
        given:
        buildFile << """
            plugins {
                id 'java'
                id 'io.swagger.codegen'
            }
            repositories {
                mavenCentral()
            }
            swaggerCodegen {
                inputSpec project.file('src/main/swagger-codegen/swagger.yaml')
                lang 'jaxrs'
                apiPackage 'com.example.project.api'
                modelPackage 'com.example.project.model'
            }
            swaggerCodegen.doLast {
                fileTree(outputDir).each {
                    println "content: " + it
                }
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
        result.output.contains("build/generated-src/swaggerCodegen/src/gen/java")
        result.output.contains("build/generated-src/swaggerCodegen/src/main/java")
        result.task(":swaggerCodegen").outcome == SUCCESS

        where:
        gradleVersion << ['2.9', '3.0', '3.4']
    }
}