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
