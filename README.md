## Swagger-Codegen-Gradle Plugin

[![Build Status](https://travis-ci.org/otrosien/swagger-codegen-gradle-plugin.svg?branch=feature/officialSwaggerCodegen)](https://travis-ci.org/otrosien/swagger-codegen-gradle-plugin)
[![License](https://img.shields.io/hexpm/l/plug.svg)](https://raw.githubusercontent.com/otrosien/swagger-codegen-gradle-plugin/master/LICENSE)

The project provides the tooling around gradle to generate code from OpenAPI-Specs. It pulls in custom templates to support Spring-MVC/Spring-Boot projects. Instead of generating code only once when a project starts (design phase), code will be generated at every build to make sure your code is in sync with your spec. For example, controllers/resources can be generated as interfaces, and developers will have to implement them. And changes in the specification will be reflected immediately on build/compile-step.

### Getting started

To get started in a gradle project, make sure the following configuration is present in your `build.gradle`

```groovy
plugins {
    id 'io.swagger.codegen' version: '0.4.38'
}

swaggerCodegen {
    inputSpec 'src/main/resources/swagger.yaml' // point to your OpenAPI spec file
    lang 'jaxrs'
    // package for your resource models
    apiPackage 'com.example.project.api'
    // package for your domain models
    modelPackage 'com.example.project.model'
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

