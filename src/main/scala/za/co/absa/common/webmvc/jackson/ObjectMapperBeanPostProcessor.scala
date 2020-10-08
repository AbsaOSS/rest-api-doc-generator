package za.co.absa.common.webmvc.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter

import scala.collection.JavaConverters._

class ObjectMapperBeanPostProcessor(postProcess: ObjectMapper => Unit) extends BeanPostProcessor {

  override def postProcessBeforeInitialization(bean: AnyRef, beanName: String): AnyRef = {
    bean match {
      case adapter: RequestMappingHandlerAdapter =>
        adapter
          .getMessageConverters
          .asScala
          .collect({ case hmc: MappingJackson2HttpMessageConverter => hmc.getObjectMapper })
          .foreach(postProcess(_))
      case _ =>
    }
    bean
  }
}
