# REST API Doc Generator

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/za.co.absa.utils/rest-api-doc-generator/badge.svg)](https://search.maven.org/search?q=g:za.co.absa.utils)
[![TeamCity build](https://teamcity.jetbrains.com/app/rest/builds/aggregated/strob:%28locator:%28buildType:%28id:OpenSourceProjects_AbsaOSS_RestApiDocGenerator_AutoBuild%29,branch:master%29%29/statusIcon.svg)](https://teamcity.jetbrains.com/viewType.html?buildTypeId=OpenSourceProjects_AbsaOSS_RestApiDocGenerator_AutoBuild&branch=master&tab=buildTypeStatusDiv)
[![Sonarcloud Status](https://sonarcloud.io/api/project_badges/measure?project=AbsaOSS_rest-api-doc-generator&metric=alert_status)](https://sonarcloud.io/dashboard?id=AbsaOSS_rest-api-doc-generator)
[![SonarCloud Maintainability](https://sonarcloud.io/api/project_badges/measure?project=AbsaOSS_rest-api-doc-generator&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=AbsaOSS_rest-api-doc-generator)
[![SonarCloud Reliability](https://sonarcloud.io/api/project_badges/measure?project=AbsaOSS_rest-api-doc-generator&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=AbsaOSS_rest-api-doc-generator)
[![SonarCloud Security](https://sonarcloud.io/api/project_badges/measure?project=AbsaOSS_rest-api-doc-generator&metric=security_rating)](https://sonarcloud.io/dashboard?id=AbsaOSS_rest-api-doc-generator)
### Usage

```shell script
# print usage
java -jar rest-api-doc-generator.jar --help

# generate Swagger definition from a Spring MVC Context class (defined in a foo.jar)
java -cp ./rest-api-doc-generator.jar:./foo.jar za.co.absa.rapidgen.RapidGenCLI \
  swagger -o swagger.json com.example.foo.MySpringWebMVCContextConfiguration

# or with the resulted JSON written to the standard output
java -cp ./rest-api-doc-generator.jar:./foo.jar za.co.absa.rapidgen.RapidGenCLI \
  swagger com.example.foo.MySpringWebMVCContextConfiguration 1> swagger.json
```

---

    Copyright 2020 ABSA Group Limited
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
        http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
