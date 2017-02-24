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
package org.zalando.gradle.plugins.swagger;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.gradle.api.GradleException;
import org.gradle.api.internal.AbstractTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;
import org.zalando.stups.swagger.codegen.CodegenerationException;
import org.zalando.stups.swagger.codegen.StandaloneCodegenerator;
import org.zalando.stups.swagger.codegen.YamlToJson;

public class SwaggerCodegenTask extends AbstractTask {

    private File out = getProject().file("build/generated-sources/swagger-codegen");

    private File apiFile = getProject().file("src/main/resources/swagger.yaml");

    private String language;

    private String apiPackage;

    private String modelPackage;

    private boolean skipModelgeneration = false;

    private boolean skipApigeneration = false;

    private boolean enable303 = false;

    private boolean enableBuilderSupport = false;

    private Map<String, Object> additionalProperties = new HashMap<>();

    private ArrayList<String> excludedModels = new ArrayList<>();

    private boolean yamlToJson = false;

    private File yamlToJsonOutputDirectory = getProject().file("build/generated-sources/swagger-codegen");

    @TaskAction
    public void invokeSwaggerCodegen() throws Exception {

        Objects.requireNonNull(apiFile, "Property [apiFile] must be set");
        Objects.requireNonNull(language, "Property [language] must be set");
        Objects.requireNonNull(out, "Property [out] must be set");

        try {
            final StandaloneCodegenerator swaggerGenerator = buildSwaggerGenerator();
            try {
                swaggerGenerator.generate();
                if (yamlToJson) {
                    final YamlToJson converter = buildYamlToJsonGenerator();
                    converter.convert();
                }
            } catch (CodegenerationException e) {
                throw new GradleException(e.getMessage(), e);
            }
        } catch (GradleException e) {
            throw e;
        } catch (Exception e) {
            throw new GradleException("Unexpected error while executing swagger-codegen: " + e.getMessage(), e);
        }
    }

    //@formatter:off
    private YamlToJson buildYamlToJsonGenerator() {
        final YamlToJson converter = YamlToJson.builder()
            .withYamlInputPath(apiFile.getPath())
            .withCodegeneratorLogger(new GradleCodegeneratorLogger(getProject().getLogger()))
            .withOutputDirectoryPath(yamlToJsonOutputDirectory.getAbsolutePath())
            .build();
        return converter;
    }
    //@formatter:on

    //@formatter:off
    private StandaloneCodegenerator buildSwaggerGenerator() {
        return StandaloneCodegenerator.builder()
         .withApiFilePath(apiFile.getPath())
         .forLanguage(language)
         .writeResultsTo(out)
         .withApiPackage(apiPackage)
         .withModelPackage(modelPackage)
         .withLogger(new GradleCodegeneratorLogger(getProject().getLogger()))
         .skipModelgeneration(skipModelgeneration)
         .skipApigeneration(skipApigeneration)
         .withModelsExcluded(excludedModels)
         .additionalProperties(additionalProperties)
         .enable303(enable303)
         .enableBuilderSupport(enableBuilderSupport)
         .build();
    }
    //@formatter:on

    @InputFile
    public File getApiFile() {
        return apiFile;
    }

    public void setApiFile(Object apiFile) {
        this.apiFile = getProject().file(apiFile);
    }

    @Input
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Input
    public String getApiPackage() {
        return apiPackage;
    }

    public void setApiPackage(String apiPackage) {
        this.apiPackage = apiPackage;
    }

    @Input
    public String getModelPackage() {
        return modelPackage;
    }

    public void setModelPackage(String modelPackage) {
        this.modelPackage = modelPackage;
    }

    @Input
    public boolean isSkipModelgeneration() {
        return skipModelgeneration;
    }

    public void setSkipModelgeneration(boolean skipModelgeneration) {
        this.skipModelgeneration = skipModelgeneration;
    }

    @Input
    public boolean isSkipApigeneration() {
        return skipApigeneration;
    }

    public void setSkipApigeneration(boolean skipApigeneration) {
        this.skipApigeneration = skipApigeneration;
    }

    @Input
    public boolean isYamlToJson() {
        return yamlToJson;
    }

    public void setYamlToJson(boolean yamlToJson) {
        this.yamlToJson = yamlToJson;
    }

    @Input
    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties(Map<String, Object> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    public void additionalProperties(Map<String, Object> additionalProperties) {
        this.additionalProperties.putAll(additionalProperties);
    }

    @OutputDirectory
    public File getOut() {
        return this.out;
    }

    public void out(Object dir) {
        this.out = getProject().file(dir);
    }

}
