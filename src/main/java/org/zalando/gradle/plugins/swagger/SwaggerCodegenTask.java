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

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import io.swagger.codegen.DefaultGenerator;
import io.swagger.codegen.Generator;
import io.swagger.codegen.config.CodegenConfigurator;

public class SwaggerCodegenTask extends DefaultTask {

    private CodegenConfigurator config = new CodegenConfigurator();

    private File configFile;

    public SwaggerCodegenTask() {
        setOutputDir(getProject().file("build/generated-src/" + this.getName()));
    }
 
    public void fromFile(Object file) {
        this.configFile = getProject().file(file);
        config = CodegenConfigurator.fromFile(this.configFile.getAbsolutePath());
    }

    @InputFile
    public File getConfigFile() {
        return configFile;
    }

    public CodegenConfigurator setLang(String lang) {
        return config.setLang(lang);
    }

    public void setInputSpec(String inputSpec) {
        config.setInputSpec(getProject().file(inputSpec).getAbsolutePath());
    }

    @InputFile
    public File getInputSpec() {
        String inputSpec = config.getInputSpec();
        return inputSpec != null ? getProject().file(inputSpec) : null;
    }

    @OutputDirectory
    public File getOutputDir() {
        String outputDir = config.getOutputDir();
        return outputDir != null ? getProject().file(outputDir) : null;
    }

    public void setOutputDir(Object outputDir) {
        config.setOutputDir(getProject().file(outputDir).getAbsolutePath());
    }

    @Input
    public String getModelPackage() {
        return config.getModelPackage();
    }

    public void setModelPackage(String modelPackage) {
        config.setModelPackage(modelPackage);
    }

    @Input
    public String getModelNamePrefix() {
        return config.getModelNamePrefix();
    }

    public void setModelNamePrefix(String prefix) {
        config.setModelNamePrefix(prefix);
    }

    @Input
    public String getModelNameSuffix() {
        return config.getModelNameSuffix();
    }

    public void setModelNameSuffix(String suffix) {
        config.setModelNameSuffix(suffix);
    }

    @Input
    public boolean isVerbose() {
        return config.isVerbose();
    }

    public void setVerbose(boolean verbose) {
        config.setVerbose(verbose);
    }

    @Input
    public boolean isSkipOverwrite() {
        return config.isSkipOverwrite();
    }

    public void setSkipOverwrite(boolean skipOverwrite) {
        config.setSkipOverwrite(skipOverwrite);
    }

    @Input
    public String getLang() {
        return config.getLang();
    }

    @Input
    public String getTemplateDir() {
        return config.getTemplateDir();
    }

    public void setTemplateDir(String templateDir) {
        config.setTemplateDir(templateDir);
    }

    @Input
    public String getAuth() {
        return config.getAuth();
    }

    public void setAuth(String auth) {
        config.setAuth(auth);
    }

    @Input
    public String getApiPackage() {
        return config.getApiPackage();
    }

    public void setApiPackage(String apiPackage) {
        config.setApiPackage(apiPackage);
    }

    @Input
    public String getInvokerPackage() {
        return config.getInvokerPackage();
    }

    public void setInvokerPackage(String invokerPackage) {
        config.setInvokerPackage(invokerPackage);
    }

    @Input
    public String getGroupId() {
        return config.getGroupId();
    }

    public CodegenConfigurator setGroupId(String groupId) {
        return config.setGroupId(groupId);
    }

    @Input
    public String getArtifactId() {
        return config.getArtifactId();
    }

    public CodegenConfigurator setArtifactId(String artifactId) {
        return config.setArtifactId(artifactId);
    }

    @Input
    public String getArtifactVersion() {
        return config.getArtifactVersion();
    }

    public CodegenConfigurator setArtifactVersion(String artifactVersion) {
        return config.setArtifactVersion(artifactVersion);
    }

    @Input
    public String getLibrary() {
        return config.getLibrary();
    }

    public void setLibrary(String library) {
        config.setLibrary(library);
    }

    public String getGitUserId() {
        return config.getGitUserId();
    }

    @Input
    public void setGitUserId(String gitUserId) {
        config.setGitUserId(gitUserId);
    }

    @Input
    public String getGitRepoId() {
        return config.getGitRepoId();
    }

    public void setGitRepoId(String gitRepoId) {
        config.setGitRepoId(gitRepoId);
    }

    @Input
    public String getReleaseNote() {
        return config.getReleaseNote();
    }

    public void setReleaseNote(String releaseNote) {
        config.setReleaseNote(releaseNote);
    }

    @Input
    public String getHttpUserAgent() {
        return config.getHttpUserAgent();
    }

    public void setHttpUserAgent(String httpUserAgent) {
        config.setHttpUserAgent(httpUserAgent);
    }

    @TaskAction
    public void invokeSwaggerCodegen() {
        codeGenerator()
        .generate();
    }

    CodegenConfigurator config() {
        return config;
    }

    private Generator codeGenerator() {
        return new DefaultGenerator()
        .opts(config.toClientOptInput());
    }

}
