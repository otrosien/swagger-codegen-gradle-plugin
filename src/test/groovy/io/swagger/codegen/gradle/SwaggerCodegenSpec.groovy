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
package io.swagger.codegen.gradle

import org.gradle.api.GradleException
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.testfixtures.ProjectBuilder

import spock.lang.Specification

// TODO: Test that the output dir can be seen on compileJava path.
// TODO: Test up-to-date checks
// TODO: Support non-java language based projects.
// TODO: Create documentation snippets from the tests to include in documentation
// see http://mrhaki.blogspot.de/2014/04/awesome-asciidoc-include-partial-parts.html
class SwaggerCodegenSpec extends Specification {

    def "plugin should provide generator task"() {

        given:
        def project = ProjectBuilder.builder().build()

        when:
        project.with {
            apply plugin: 'io.swagger.codegen'
        }

        then:
        // check for task existance and task defaults
        project.tasks.findByName('swaggerCodegen') .with { task ->
            task.group == 'Swagger'
            task.description.contains('Generates code from the swagger spec and the CodegenConfigurator')
            task.outputDir.name == 'swaggerCodegen'
        }

    }

    def "plugin should be configurable"() {

        given:
        def project = ProjectBuilder.builder().build()
        project.file('src/main/swagger-templates').mkdirs()

        when:
        project.with {
            apply plugin: 'io.swagger.codegen'
            swaggerCodegen {
                // check all configuration params
                inputSpec SwaggerCodegenSpec.getResource('/SwaggerCodegenSpec.yaml').path
                lang 'spring'
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
        }

        then:
        project.tasks.findByName('swaggerCodegen').with {
            inputSpec.name   == 'SwaggerCodegenSpec.yaml'
            lang             == 'spring'
            apiPackage       == 'com.example.project.api'
            modelPackage     == 'com.example.project.model'
            invokerPackage   == 'com.example.invoker'
            groupId          == 'org.example'
            artifactId       == 'demo'
            artifactVersion  == '0.0.1'
            library          == 'spring-cloud'
            gitUserId        == 'user'
            gitRepoId        == 'github-repo'
            httpUserAgent    == 'User/Agent 1.0'
            releaseNote      == 'See CHANGELOG.md'
            modelNamePrefix  == 'Default'
            modelNameSuffix  == 'Gen'
            templateDir.name == 'swagger-templates'
            auth             == 'username%3Apassword'
            verbose          == true
            skipOverwrite    == true
            outputDir.name   == 'output-dir'
            systemProperties == [ foo:'bar' ]
            instantiationTypes == ['array':'ArrayList','map':'HashMap']
            languageSpecificPrimitives == ['type1','type2','type3']
            importMappings   == ['id':'identifier']
        }
    }

    def "plugin should throw NPE on missing mandatory parameter"() {

        given:
        def project = ProjectBuilder.builder().build()

        when:
        project.with {
            apply plugin: 'io.swagger.codegen'
            repositories {
                mavenCentral()
            }
            swaggerCodegen {
            }
            tasks.swaggerCodegen.invokeSwaggerCodegen()
        }

        then:
        NullPointerException e = thrown()
        e.message.contains("language must be specified")

    }

    def "plugin should throw on missing swagger file"() {

        given:
        def project = ProjectBuilder.builder().build()

        when:
        project.with {
            apply plugin: 'io.swagger.codegen'
            repositories {
                mavenCentral()
            }
            swaggerCodegen {
                inputSpec 'missing.yaml'
                lang      'spring'
            }
            tasks.swaggerCodegen.invokeSwaggerCodegen()
        }

        then:
        RuntimeException e = thrown()
        e.message.contains("missing swagger input or config!")

    }

    def "should load input spec from a file"() {

        given:
        def project = ProjectBuilder.builder().build()
        project.file('spec.json') << """{"inputSpec":"file.yaml", "lang":"php"}"""

        when:
        project.with {
            apply plugin: 'io.swagger.codegen'
            repositories {
                mavenCentral()
            }
            swaggerCodegen.fromFile 'spec.json'
        }

        then:
        project.tasks.findByName('swaggerCodegen').with {
            inputSpec.name   == 'file.yaml'
            lang             == 'php'
        }
    }

    def "should support a custom codegen task"() {

        given:
        def project = ProjectBuilder.builder().build()

        when:
        project.with {
            apply plugin: 'io.swagger.codegen'
            repositories {
                mavenCentral()
            }
            swaggerCodegen {
                inputSpec 'file1.yaml'
            }
            task('swagMore', type: SwaggerCodegenTask) {
                inputSpec 'file2.yaml'
            }
        }

        then:
        def defaultTask = project.tasks.findByName('swaggerCodegen')
        def customTask =  project.tasks.findByName('swagMore')

        customTask != null
        defaultTask != customTask
        customTask.inputSpec.name != defaultTask.inputSpec.name
    }

    def "should reject project directory as task output directory"() {

        given:
        def project = ProjectBuilder.builder().build()

        when:
        project.with {
            apply plugin: 'io.swagger.codegen'
            repositories {
                mavenCentral()
            }
            swaggerCodegen {
                outputDir = project.rootDir
            }
            tasks.swaggerCodegen.invokeSwaggerCodegen()
        }

        then:
        GradleException e = thrown()
        e.message.contains('Setting output directory to project directory is dangerous, and not supported.')
    }

    def "should add swagger codegen output as java source dir"() {

        given:
        def project = ProjectBuilder.builder().build()

        when:
        project.with {
            apply plugin: 'io.swagger.codegen'
            apply plugin: 'java'

            repositories {
                mavenCentral()
            }
        }
        // force afterEvaluate to fire.
        project.evaluate()

        then:
        project.getConvention().getPlugin(JavaPluginConvention.class)
                                .getSourceSets().getByName("main")
                                .getAllJava()
                                .getSrcDirs()
                                .contains(project.tasks.findByName('swaggerCodegen').getOutputDir())
    }
}