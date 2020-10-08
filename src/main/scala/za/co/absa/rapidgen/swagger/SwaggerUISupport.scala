package za.co.absa.rapidgen.swagger

import org.springframework.web.servlet.config.annotation.{ResourceHandlerRegistry, WebMvcConfigurer}

trait SwaggerUISupport {
  this: WebMvcConfigurer =>

  override def addResourceHandlers(registry: ResourceHandlerRegistry): Unit = {
    registry.
      addResourceHandler("swagger-ui.html").
      addResourceLocations("classpath:/META-INF/resources/")
    registry.
      addResourceHandler("/webjars/**").
      addResourceLocations("classpath:/META-INF/resources/webjars/")
  }
}
