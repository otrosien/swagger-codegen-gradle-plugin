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
import org.gradle.testfixtures.ProjectBuilder

import spock.lang.Specification

// TODO: Test that the output dir can be seen on compileJava path.
// TODO: Test creation of additional swagger codegen task
// TODO: Test up-to-date checks
// TODO: Create documentation snippets from the tests to include in README / cross-compile tests
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
        project.tasks.findByName('swaggerCodegen') .with {
            outputDir.name == 'swaggerCodegen'
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
}