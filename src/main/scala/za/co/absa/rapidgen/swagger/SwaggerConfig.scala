/*
 * Copyright 2019 ABSA Group Limited
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

package za.co.absa.rapidgen.swagger

import com.fasterxml.classmate.TypeResolver
import org.slf4s.Logging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import springfox.documentation.PathProvider
import springfox.documentation.builders.{PathSelectors, RequestHandlerSelectors}
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2
import za.co.absa.rapidgen.{Constants, RapidGenConfig}

@Configuration
@EnableSwagger2
class SwaggerConfig(@Autowired() val typeResolver: TypeResolver,
                    @Autowired val appContext: ApplicationContext)
  extends WebMvcConfigurer
    with SwaggerScalaTypesRules
    with SwaggerUISupport {

  @Bean def api: Docket = {
    var docket = new Docket(DocumentationType.SWAGGER_2).
      forCodeGeneration(true)

    if (appContext.isInstanceOf[SwaggerDocGenAppContext]) {
      val rapidGenConfig = appContext.asInstanceOf[SwaggerDocGenAppContext].getRapidGenConfig
      if (rapidGenConfig.isDefined) {
        if (rapidGenConfig.get.overrideHost.isDefined) {
          docket = docket.host(blankReplace(rapidGenConfig.get.overrideHost.get,
            Constants.BLANK_HOST_PLACE_HOLDER))
        }
        if (rapidGenConfig.get.overrideBasePath.isDefined) {
          val basePath = blankReplace(rapidGenConfig.get.overrideBasePath.get,
            Constants.BLANK_BASE_PATH_PLACE_HOLDER)
          docket = docket.pathProvider(new PathProvider() {
            override def getApplicationBasePath: String = basePath

            override def getOperationPath(operationPath: String): String = operationPath.replace(basePath, "")

            override def getResourceListingPath(groupName: String, apiDeclaration: String): String = null
          })
        }
      }
    }
    docket.
      select.
      apis(RequestHandlerSelectors.any).
      paths(PathSelectors.any).
      build
  }

  @Bean def rpbPlugin = new SwaggerRequiredPropertyBuilderPlugin

  private def blankReplace(original: String, replacement: String): String = {
    if (original != null && !original.isBlank)
      original
    else
      replacement
  }
}
