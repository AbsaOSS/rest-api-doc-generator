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

  behavior of "generateSwagger()"

  private val swaggerJson = DocGenerator.generateSwagger(classOf[FooRESTConfig], None)

  it should "generate a valid Swagger definition" in {
    swaggerJson should not be empty
    swaggerJson should startWith("""{"swagger":"2.0"""")
    swaggerJson should include("/foo")
    swaggerJson should include("\"doh\"")
    swaggerJson should endWith("}")
  }

  it should "return properly encoded UTF-8 content" in {
    swaggerJson should not include "Â"
    swaggerJson should not include "Ñ"
    swaggerJson should include("Операция FOO")
  }

  it should "generate a codegen friendly Swagger" in {
    swaggerJson should not include "#/definitions/Seq«"
    swaggerJson should not include "#/definitions/SeqOf"
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
    @ApiOperation("Операция FOO")
    def foo(): Foo = null
  }

  case class Foo(
    maybeBar: Option[Seq[Bar]],
    seqOfBars: Seq[Seq[Bar]]
  )

  case class Bar(doh: Map[Int, Seq[String]])

}
