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
package org.zalando.gradle.plugins.swagger

import org.gradle.api.GradleException
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class SwaggerCodegenSpec extends Specification {

    def "plugin should provide generator task"() {

        given:
        def project = ProjectBuilder.builder().build()

        when:
        project.with {
            apply plugin: 'org.zalando.swagger-codegen'
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

        when:
        project.with {
            apply plugin: 'org.zalando.swagger-codegen'
            swaggerCodegen {
                // check configuration params
                inputSpec SwaggerCodegenSpec.getResource('/SwaggerCodegenSpec.yaml').path
                lang 'springinterfaces'
                apiPackage 'com.example.project.api'
                modelPackage 'com.example.project.model'
                outputDir 'build/other-dir'
            }
        }

        then:
        project.tasks.findByName('swaggerCodegen').with {
            inputSpec.name == 'SwaggerCodegenSpec.yaml'
            lang           == 'springinterfaces'
            apiPackage     == 'com.example.project.api'
            modelPackage   == 'com.example.project.model'
            outputDir.name == 'other-dir'
        }
    }


    def "plugin should throw NPE on missing mandatory parameter"() {

        given:
        def project = ProjectBuilder.builder().build()

        when:
        project.with {
            apply plugin: 'org.zalando.swagger-codegen'
            swaggerCodegen {
            }
            tasks.swaggerCodegen.invokeSwaggerCodegen()
        }

        then:
        NullPointerException e = thrown()
        e.message.contains("language must be specified")

    }
//
//    def "plugin should throw on missing swagger file"() {
//
//        given:
//        def project = ProjectBuilder.builder().build()
//
//        when:
//        project.with {
//            apply plugin: 'org.zalando.swagger-codegen'
//            swaggerCodegen {
//                apiFile 'missing.yaml'
//                language 'springinterfaces'
//            }
//            tasks.swaggerCodegen.invokeSwaggerCodegen()
//        }
//
//        then:
//        GradleException e = thrown()
//        e.message.contains("The 'apiFile' does not exists at")
//
//    }
//
//    def "plugin can be used by its deprecated name"() {
//
//        given:
//        def project = ProjectBuilder.builder().build()
//
//        when:
//        project.with {
//            apply plugin: 'swagger-codegen'
//        }
//
//        then:
//        project.tasks.findByName('swaggerCodegen')
//
//    }
}