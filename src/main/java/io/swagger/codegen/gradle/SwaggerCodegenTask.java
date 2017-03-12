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
import org.gradle.api.GradleException;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import io.swagger.codegen.DefaultGenerator;
import io.swagger.codegen.config.CodegenConfigurator;

public class SwaggerCodegenTask extends DefaultTask {

    private CodegenConfigurator config = new CodegenConfigurator();

    private File configFile;

    private static final Object LOCK = new Object();

    public SwaggerCodegenTask() {
        setDescription("Generates code from the swagger spec and the CodegenConfigurator.");
        setGroup("Swagger");
        setOutputDir("build/generated-src/" + this.getName());
        getExtensions().add("config", config);
    }

    public void fromFile(Object file) {
        this.configFile = getProject().file(file);
        config = CodegenConfigurator.fromFile(this.configFile.getAbsolutePath());
    }

    @Input
    public CodegenConfigurator config() {
        return config;
    }

    @InputFile
    @Optional
    public File getConfigFile() {
        return configFile;
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

    @InputDirectory
    @Optional
    public File getTemplateDir() {
        return
                config.getTemplateDir() != null
                ? getProject().file(config.getTemplateDir())
                : null;
    }

    /**
     * Guard against deleting the directory being passed.
     * I accidentally did this to the root project directory.
     */
    private void validateOutputDir() {
        if (getOutputDir().equals(getProject().getProjectDir())) {
            throw new GradleException("Setting output directory to project directory is dangerous, and not supported.");
        }
        if (getOutputDir().equals(getProject().getRootDir())) {
            throw new GradleException("Setting output directory to rootProject directory is dangerous, and not supported.");
        }
    }

    @TaskAction
    public void invokeSwaggerCodegen() throws Exception {
        validateOutputDir();
        Set<File> files = getProject().getConfigurations().getByName("swaggerCodegen").getFiles();
        Set<URL> urls = new HashSet<>(files.size());
        for (File file : files) {
            urls.add(file.toURI().toURL());
            // System.out.println(file.toString());
        }

        /*
         * Since the generator sets system properties we need to ensure that two
         * tasks don't try to have system properties set in the same JVM.
         * https://github.com/swagger-api/swagger-codegen/issues/4788
         */
        synchronized (LOCK) {
            try (URLClassLoader classLoader = new URLClassLoader(urls.toArray(new URL[0]),
                    Thread.currentThread().getContextClassLoader())) {
                DefaultGenerator generator = (DefaultGenerator) classLoader
                        .loadClass("io.swagger.codegen.DefaultGenerator").newInstance();
                generator.opts(config.toClientOptInput()).generate();
            } finally {
                for (String key : config.getSystemProperties().keySet()) {
                    System.clearProperty(key);
                }
            }
        }
    }

}
