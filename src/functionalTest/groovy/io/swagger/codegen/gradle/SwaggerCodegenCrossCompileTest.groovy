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

import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll

class SwaggerCodegenCrossCompileTest extends Specification {
    @Rule final TemporaryFolder testProjectDir = new TemporaryFolder()
    File buildFile

    def setup() {
        buildFile = testProjectDir.newFile('build.gradle')
        testProjectDir.newFolder('src','main','swagger-codegen')
        testProjectDir.newFolder('src','main','swagger-templates')
        testProjectDir.newFile('src/main/swagger-codegen/swagger.yaml') << SwaggerCodegenCrossCompileTest.getResource( '/swagger.yaml' ).text
    }

    @Unroll
    def "can execute swaggerCodegen task with swagger-codegen version #swaggerCodegenVersion"() {
        given:
        buildFile << """
            plugins {
                id 'io.swagger.codegen'
            }
            repositories {
                mavenCentral()
            }
            swaggerTooling {
                codegenVersion = "$swaggerCodegenVersion"
            }
            swaggerCodegen {
                inputSpec project.file('src/main/swagger-codegen/swagger.yaml')
                language 'spring'
                apiPackage 'com.example.project.api'
                modelPackage 'com.example.project.model'
                invokerPackage 'com.example.invoker'
                groupId 'org.example'
                artifactId 'demo'
                artifactVersion '0.0.1'
                library 'spring-cloud'
                gitUserId 'user'
                gitRepoId 'github-repo'
                httpUserAgent 'User/Agent 1.0'
                releaseNote 'See CHANGELOG.md'
                modelNamePrefix 'Default'
                modelNameSuffix 'Gen'
                templateDir 'src/main/swagger-templates'
                auth 'username%3Apassword'
                verbose true
                skipOverwrite true
                outputDir 'build/output-dir'
                systemProperties  foo:'bar'
                instantiationTypes 'array':'ArrayList','map':'HashMap'
                importMappings 'id':'identifier'
                languageSpecificPrimitives = ['type1','type2','type3']
            }
        """

        when:
        def result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments('swaggerCodegen','--info','--stacktrace')
            .withDebug(true)
            .withPluginClasspath()
            .build()

        then:
        ! result.output.contains('swaggerCodegen UP-TO-DATE')
        result.task(":swaggerCodegen").outcome == SUCCESS

        where:
        swaggerCodegenVersion << ['2.1.6', '2.2.1', '2.2.2']
    }

    @Ignore("not yet working")
    def "can execute swaggerCodegen task with custom code template"() {
        given:
        buildFile << """
            plugins {
                id 'io.swagger.codegen'
            }
            repositories {
                mavenCentral()
            }
            dependencies {
                swaggerCodegen "org.zalando.stups:swagger-codegen-template-spring-interfaces:0.4.38"
            }
            swaggerCodegen {
                inputSpec project.file('src/main/swagger-codegen/swagger.yaml')
                language 'springinterfaces'
                apiPackage 'com.example.project.api'
                modelPackage 'com.example.project.model'
            }

        """

        when:
        def result = GradleRunner.create()
            .withProjectDir(testProjectDir.root)
            .withArguments('swaggerCodegen','--info','--stacktrace')
            .withDebug(true)
            .withPluginClasspath()
            .build()

        then:
        ! result.output.contains('swaggerCodegen UP-TO-DATE')
        result.task(":swaggerCodegen").outcome == SUCCESS
    }
}