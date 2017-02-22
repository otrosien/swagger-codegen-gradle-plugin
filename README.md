## Swagger-Codegen-Gradle Plugin

[![Build Status](https://travis-ci.org/zalando-incubator/swagger-codegen-gradle-plugin.svg?branch=master)](https://travis-ci.org/zalando-incubator/swagger-codegen-gradle-plugin)
[![License](https://img.shields.io/hexpm/l/plug.svg)](https://raw.githubusercontent.com/zalando-incubator/swagger-codegen-gradle-plugin/master/LICENSE)
[![Coverage Status](https://coveralls.io/repos/github/zalando-incubator/swagger-codegen-gradle-plugin/badge.svg?branch=master)](https://coveralls.io/github/zalando-incubator/swagger-codegen-gradle-plugin?branch=master)

The project provides the tooling around gradle to generate code from OpenAPI-Specs. It pulls in custom templates to support Spring-MVC/Spring-Boot projects. Instead of generating code only once when a project starts (design phase), code will be generated at every build to make sure your code is in sync with your spec. For example, controllers/resources can be generated as interfaces, and developers will have to implement them. And changes in the specification will be reflected immediately on build/compile-step.

It is a spin-off from the original [swagger-codegen-tooling](https://github.com/zalando-stups/swagger-codegen-tooling) project. Most of its [documentation](https://stups.io/swagger-codegen-tooling/) also applies to the gradle plugin.

### Getting started

To get started in a gradle project, make sure the following configuration is present in your `build.gradle`

```groovy
plugins {
    id 'org.zalando.swagger-codegen' version: '0.4.38'
}

// Full DSL description below, including their defaults.

swaggerCodegen {
    // location of your OpenAPI spec file. (mandatory)
    apiFile 'src/main/resources/swagger.yaml'

    // code template to use. See swagger-codegen-tooling documentation at 
    // https://stups.io/swagger-codegen-tooling/ for details. (mandatory)
    language 'jaxrsinterfaces'

    // package for your resource models (mandatory)
    apiPackage 'com.example.project.api'

    // package for your domain models (mandatory)
    modelPackage 'com.example.project.model'

    // code generation output directory (mandatory)
    out 'build/generated-sources/swagger-codegen'

    // skip model code generation (optional)
    skipModelgeneration false

    // skip api resource code generation (optional)
    skipApigeneration false

    // convert apiFile from YAML to JSON (optional)
    yamlToJson false

    // output directory for YAML to JSON conversion (optional)
    yamlToJsonOutputDirectory 'build/generated-sources/swagger-codegen'
}
```

NOTE: The Swagger-Codegen-Gradle-Plugin is currently in development. So be prepared for changes. Specifically a release to the gradle plugin portal is still in the making.


## License

Copyright 2015-2017 Zalando SE

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   [http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

