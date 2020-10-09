# REST API Doc Generator

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
