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
package io.swagger.codegen.gradle;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import io.swagger.codegen.DefaultGenerator;
import io.swagger.codegen.config.CodegenConfigurator;

/*
 * TODO: Maps / Sets for additional properties etc
 */
public class SwaggerCodegenTask extends DefaultTask {

    private CodegenConfigurator config = new CodegenConfigurator();

    private File configFile;

    public SwaggerCodegenTask() {
        outputDir(getProject().file("build/generated-src/" + this.getName()));
    }
 
    public void fromFile(Object file) {
        this.configFile = getProject().file(file);
        config = CodegenConfigurator.fromFile(this.configFile.getAbsolutePath());
    }

    @InputFile
    @Optional
    public File getConfigFile() {
        return configFile;
    }

    public void setLanguage(String language) {
        config.setLang(language);
    }

    @Input
    public String getLanguage() {
        return config.getLang();
    }

    public void setInputSpec(Object inputSpec) {
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

    public void outputDir(Object outputDir) {
        config.setOutputDir(getProject().file(outputDir).getAbsolutePath());
    }

    @Input
    @Optional
    public String getModelPackage() {
        return config.getModelPackage();
    }

    public void setModelPackage(String modelPackage) {
        config.setModelPackage(modelPackage);
    }

    @Input
    @Optional
    public String getModelNamePrefix() {
        return config.getModelNamePrefix();
    }

    public void setModelNamePrefix(String prefix) {
        config.setModelNamePrefix(prefix);
    }

    @Input
    @Optional
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
    @Optional
    public String getTemplateDir() {
        return config.getTemplateDir();
    }

    public void setTemplateDir(String templateDir) {
        config.setTemplateDir(templateDir);
    }

    @Input
    @Optional
    public String getAuth() {
        return config.getAuth();
    }

    public void setAuth(String auth) {
        config.setAuth(auth);
    }

    @Input
    @Optional
    public String getApiPackage() {
        return config.getApiPackage();
    }

    public void setApiPackage(String apiPackage) {
        config.setApiPackage(apiPackage);
    }

    @Input
    @Optional
    public String getInvokerPackage() {
        return config.getInvokerPackage();
    }

    public void setInvokerPackage(String invokerPackage) {
        config.setInvokerPackage(invokerPackage);
    }

    @Input
    @Optional
    public String getGroupId() {
        return config.getGroupId();
    }

    public void setGroupId(String groupId) {
        config.setGroupId(groupId);
    }

    @Input
    @Optional
    public String getArtifactId() {
        return config.getArtifactId();
    }

    public void setArtifactId(String artifactId) {
        config.setArtifactId(artifactId);
    }

    @Input
    @Optional
    public String getArtifactVersion() {
        return config.getArtifactVersion();
    }

    public void setArtifactVersion(String artifactVersion) {
        config.setArtifactVersion(artifactVersion);
    }

    @Input
    @Optional
    public String getLibrary() {
        return config.getLibrary();
    }

    public void setLibrary(String library) {
        config.setLibrary(library);
    }

    @Input
    @Optional
    public String getGitUserId() {
        return config.getGitUserId();
    }

    public void setGitUserId(String gitUserId) {
        config.setGitUserId(gitUserId);
    }

    @Input
    @Optional
    public String getGitRepoId() {
        return config.getGitRepoId();
    }

    public void setGitRepoId(String gitRepoId) {
        config.setGitRepoId(gitRepoId);
    }

    @Input
    @Optional
    public String getReleaseNote() {
        return config.getReleaseNote();
    }

    public void setReleaseNote(String releaseNote) {
        config.setReleaseNote(releaseNote);
    }

    @Input
    @Optional
    public String getHttpUserAgent() {
        return config.getHttpUserAgent();
    }

    public void setHttpUserAgent(String httpUserAgent) {
        config.setHttpUserAgent(httpUserAgent);
    }

    @TaskAction
    public void invokeSwaggerCodegen() throws Exception {
        Set<File> files = getProject().getConfigurations().getByName("swaggerCodegen").getFiles();
        Set<URL> urls = new HashSet<>(files.size());
        for (File file : files) {
            urls.add(file.toURI().toURL());
        }
        try (URLClassLoader classLoader = new URLClassLoader(urls.toArray(new URL[0]), 
                Thread.currentThread().getContextClassLoader())) {
            DefaultGenerator generator = (DefaultGenerator) classLoader.loadClass("io.swagger.codegen.DefaultGenerator").newInstance();
            generator.opts(config.toClientOptInput())
            .generate();
        }
    }

}
