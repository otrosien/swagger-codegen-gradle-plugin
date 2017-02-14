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

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;

public class SwaggerCodegenPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getPlugins().apply(JavaPlugin.class);

        configureConfigurations(project);
        configureSourceSet(project);
    }

    private void configureConfigurations(final Project project) {
        final Configuration swaggerCodegenConfiguration = project.getConfigurations().create("swagger-codegen")
                .setVisible(false);

        project.getConfigurations().getByName(JavaPlugin.COMPILE_CONFIGURATION_NAME).extendsFrom(swaggerCodegenConfiguration);

    }

    private void configureSourceSet(final Project project) {
        SwaggerCodegenTask swaggerCodegenTask = project.getTasks().create("swaggerCodegen", SwaggerCodegenTask.class);

        swaggerCodegenTask
                .setDescription("Processes the Open-API definition.");

        //
        // Set up the swagger-codegen output directory (adding to
        // javac inputs)
        //
        final String outputDirectoryName =
                String.format("%s/generated-src/swagger-codegen", project.getBuildDir());
        final File outputDirectory = new File(outputDirectoryName);
        swaggerCodegenTask.out(outputDirectory);
        project.getConvention().getPlugin(JavaPluginConvention.class).getSourceSets().getByName("main", new Action<SourceSet>() {
            @Override
            public void execute(SourceSet sourceSet) {
                sourceSet.getJava().srcDir(outputDirectory);
            }
        });

        //
        // Register the fact that swagger-codegen should run before
        // compiling.
        //
        project.getTasks().getByName("compileJava").dependsOn("swaggerCodegen");
    }


}
