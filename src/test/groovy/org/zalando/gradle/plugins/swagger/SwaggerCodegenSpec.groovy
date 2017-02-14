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
        project.tasks.findByName('swaggerCodegen')

    }


    def "plugin should be configurable"() {

        given:
        def project = ProjectBuilder.builder().build()

        when:
        project.with {
            apply plugin: 'org.zalando.swagger-codegen'
            swaggerCodegen {
                apiFile SwaggerCodegenSpec.getResource('/swagger.yaml').path
                language 'springinterfaces'
                apiPackage 'com.example.project.api'
                modelPackage 'com.example.project.model'
                skipModelgeneration true
                skipApigeneration true
                yamlToJson true
                out 'build/some-otherdir'
            }
        }

        then:
        project.tasks.findByName('swaggerCodegen').with {
            apiFile.name == 'swagger.yaml'
            apiPackage   == 'com.example.project.api'
            modelPackage == 'com.example.project.model'
            skipModelgeneration == true
            skipApigeneration   == true
            yamlToJson          == true
            out.name == 'some-otherdir'
        }

    }

    def "plugin should throw on missing swagger file"() {

        given:
        def project = ProjectBuilder.builder().build()

        when:
        project.with {
            apply plugin: 'org.zalando.swagger-codegen'
            swaggerCodegen {
                apiFile 'missing.yaml'
            }
            tasks.swaggerCodegen.invokeSwaggerCodegen()
        }

        then:
        GradleException e = thrown()
        e.message.contains("The 'apiFile' does not exists at")

  } 

}