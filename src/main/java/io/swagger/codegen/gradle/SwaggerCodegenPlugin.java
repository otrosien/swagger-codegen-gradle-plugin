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

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.DependencySet;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;

/*
 * TODO: dependency configuration for swagger-codegen version 
 * (+ cross-compile integration test)
 */
public class SwaggerCodegenPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getPlugins().apply(JavaPlugin.class);

        configureConfigurations(project);
        configureSourceSet(project);
    }

    private void configureConfigurations(final Project project) {
        final Configuration swaggerCodegenConfiguration = project.getConfigurations().maybeCreate("swaggerCodegen");
        swaggerCodegenConfiguration.defaultDependencies(new Action<DependencySet>() {
            @Override
            public void execute(DependencySet dependencies) {
                dependencies.add(project.getDependencies().create("io.swagger:swagger-codegen:2.2.1"));
            }
        });

    }

    private void configureSourceSet(final Project project) {
        SwaggerCodegenTask swaggerCodegenTask = project.getTasks().create("swaggerCodegen", SwaggerCodegenTask.class);

        swaggerCodegenTask.setDescription("Processes the Open-API definition.");
        swaggerCodegenTask.setGroup("build");

        project.afterEvaluate(new Action<Project>() {
            @Override
            public void execute(Project p) {
                final SourceSet mainSourceSet = project.getConvention().getPlugin(JavaPluginConvention.class)
                        .getSourceSets().getByName("main");
                final Task compileJava = p.getTasks().getByName("compileJava");
                for (SwaggerCodegenTask t : p.getTasks().withType(SwaggerCodegenTask.class)) {
                    mainSourceSet.getJava().srcDir(p.file(t.getOutputDir()));
                    compileJava.dependsOn(t);
                }
            }
        });

    }

}
