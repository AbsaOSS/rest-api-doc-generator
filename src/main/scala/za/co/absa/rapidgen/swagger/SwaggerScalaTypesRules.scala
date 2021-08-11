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

package za.co.absa.rapidgen.swagger

import com.fasterxml.classmate.TypeResolver
import org.springframework.core.Ordered
import org.springframework.core.Ordered.HIGHEST_PRECEDENCE
import springfox.documentation.schema.AlternateTypeRules.{GENERIC_SUBSTITUTION_RULE_ORDER, newRule}
import springfox.documentation.schema.{AlternateRecursiveTypeRule, AlternateTypeRule, AlternateTypeRuleConvention, WildcardType}
import za.co.absa.commons.lang.TypeConstraints.not

import java.lang.reflect.Type
import java.util
import scala.collection.JavaConverters._
import scala.concurrent.Future

trait SwaggerScalaTypesRules extends AlternateTypeRuleConvention {
  val typeResolver: TypeResolver

  import typeResolver._

  override def getOrder: Int = HIGHEST_PRECEDENCE

  private def newListCoercionRule[C <: Iterable[_] : not[Map[_, _]]#λ : Manifest] =
    newRecursiveRule(
      resolve(manifest[C].runtimeClass, classOf[WildcardType]),
      resolve(classOf[util.List[_]], classOf[WildcardType]))

  private def newMapCoercionRule[C <: Map[_, _] : Manifest] =
    newRecursiveRule(
      resolve(manifest[C].runtimeClass, classOf[WildcardType], classOf[WildcardType]),
      resolve(classOf[util.Map[_, _]], classOf[WildcardType], classOf[WildcardType]))

  private def newUnboxingRule[T: Manifest] =
    newRecursiveRule(
      resolve(manifest[T].runtimeClass, classOf[WildcardType]),
      resolve(classOf[WildcardType]),
      GENERIC_SUBSTITUTION_RULE_ORDER)

  private def newRecursiveRule(original: Type, alternate: Type, order: Int = Ordered.LOWEST_PRECEDENCE) = {
    val resolver = new TypeResolver
    new AlternateRecursiveTypeRule(resolver.resolve(original), resolver.resolve(alternate), order, rules.asScala)
  }

  override val rules: util.List[AlternateTypeRule] = util.Arrays.asList(
    newRule(classOf[java.util.Date], classOf[Long]),
    newRule(classOf[java.sql.Date], classOf[Long]),
    newRule(classOf[java.sql.Timestamp], classOf[Long]),

    newUnboxingRule[Option[_]],
    newUnboxingRule[Future[_]],

    newListCoercionRule[Set[_]],
    newListCoercionRule[Seq[_]],
    newListCoercionRule[List[_]],
    newListCoercionRule[Vector[_]],

    newMapCoercionRule[Map[_, _]]
  )
}
