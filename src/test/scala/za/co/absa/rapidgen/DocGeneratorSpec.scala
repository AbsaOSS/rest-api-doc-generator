/*
 * Copyright 2020 ABSA Group Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package za.co.absa.rapidgen

import com.fasterxml.jackson.module.scala.DefaultScalaModule
import io.swagger.annotations.ApiOperation
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.web.bind.annotation.{GetMapping, RequestMapping, RestController}
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import za.co.absa.common.webmvc.jackson.ObjectMapperBeanPostProcessor
import za.co.absa.rapidgen.DocGeneratorSpec.FooRESTConfig

class DocGeneratorSpec extends AnyFlatSpec with Matchers {

  it should "generate a valid Swagger definition" in {
    val output = DocGenerator.generateSwagger(classOf[FooRESTConfig])
    output should not be empty
    output should startWith("""{"swagger":"2.0"""")
    output should include("""/foo""")
    output should include("""FOO-OPERATION""")
    output should endWith("}")
  }
}

object DocGeneratorSpec {

  @EnableWebMvc
  @Configuration
  class FooRESTConfig {
    @Bean def fooController = new FooController

    @Bean def jacksonConfigurer = new ObjectMapperBeanPostProcessor(_.registerModule(DefaultScalaModule))
  }

  @RestController
  @RequestMapping(produces = Array("application/json"))
  class FooController {
    @GetMapping(Array("/foo"))
    @ApiOperation("FOO-OPERATION")
    def foo(): Foo = null
  }

  case class Foo(bars: Seq[Bar])

  case class Bar(doh: Map[Int, Seq[String]])

}
