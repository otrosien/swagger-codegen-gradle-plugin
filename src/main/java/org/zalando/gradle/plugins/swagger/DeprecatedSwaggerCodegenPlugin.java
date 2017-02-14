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

import org.gradle.api.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeprecatedSwaggerCodegenPlugin extends SwaggerCodegenPlugin {

    private static final Logger logger = LoggerFactory.getLogger(DeprecatedSwaggerCodegenPlugin.class);

    @Override
    public void apply(Project project) {
        logger.warn("The plugin id 'spring-boot' is deprecated. Please use "
                + "'org.springframework.boot' instead.");
        super.apply(project);
    }
}
