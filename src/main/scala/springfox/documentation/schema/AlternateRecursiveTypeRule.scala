/*
 * Copyright 2021 ABSA Group Limited
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

package springfox.documentation.schema

import com.fasterxml.classmate.ResolvedType
import springfox.documentation.schema.WildcardType.hasWildcards

import scala.collection.JavaConverters._

class AlternateRecursiveTypeRule(original: ResolvedType, alternate: ResolvedType, order: Int, rules: => Seq[AlternateTypeRule])
  extends AlternateTypeRule(original, alternate, order) {

  override def alternateFor(`type`: ResolvedType): ResolvedType = {
    if (!appliesTo(`type`)) `type`
    else if (!hasWildcards(original)) alternate
    else {
      val replaceables = WildcardType.collectReplaceables(`type`, original)
      val alternatedReplaceables = replaceables.asScala.map(rules.foldLeft(_)((t, r) => r.alternateFor(t)))
      WildcardType.replaceWildcardsFrom(alternatedReplaceables.asJava, alternate)
    }
  }
}
