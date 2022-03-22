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

import org.slf4s.Logging
import za.co.absa.commons.lang.ARM._
import za.co.absa.rapidgen.Command.SwaggerCommand

import java.io.{File, FileWriter, OutputStreamWriter}

object RapidGenCLI extends App {
  new RapidGenCLI(DocGenerator).exec(args)
}

class RapidGenCLI(gen: DocGenerator) extends Logging {
  def exec(args: Array[String]): Unit = {

    val cliParser = new scopt.OptionParser[RapidGenConfig]("rest-doc-gen-tool") {
      head("REST OpenAPI v2 spec generation tool", RapidGenBuildInfo.Version)
      help("help").text("prints this usage text")

      (opt[String]('o', "output")
        valueName "<file>"
        text "OpenAPI JSON output file name"
        action ((path, conf) => conf.copy(maybeOutputFile = Some(new File(path).getAbsoluteFile))))

      (opt[String]('h', "host")
        valueName "<host>"
        text "OpenAPI JSON override host"
        action ((overrideHost, conf) => conf.copy(maybeHost = Some(overrideHost))))

      (opt[String]('b', "basePath")
        valueName "<host>"
        text "OpenAPI JSON override basePath"
        action ((overrideBasePath, conf) => conf.copy(maybeBasePath = Some(overrideBasePath))))

      (cmd("swagger")
        action ((_, conf) => conf.copy(command = SwaggerCommand()))
        children (
        arg[String]("<class>")
          text "Fully specified class name of a Spring context to generate a swagger definition for"
          action {
          case (className, conf@RapidGenConfig(sc: SwaggerCommand, _, _, _)) =>
            conf.copy(command = sc.copy(restContextClass = Some(Class.forName(className))))
        }
        ))

      checkConfig {
        case RapidGenConfig(null, _, _, _) =>
          failure("No command given")
        case _ =>
          success
      }
    }

    val rapidGenConfig = cliParser.parse(args, RapidGenConfig())
    rapidGenConfig match {
      case Some(RapidGenConfig(command, maybeOutFile, maybeHost, maybeBasePath)) =>
        val writer = fileOrStdoutWriter(maybeOutFile)
        val result = execute(command, maybeHost, maybeBasePath).
          replace(s""","host":"${Constants.BLANK_HOST_PLACE_HOLDER}"""", "").
          replace(s""","basePath":"${Constants.BLANK_BASE_PATH_PLACE_HOLDER}"""", "")
        using(writer)(_.write(result))
      case _ => sys.exit(1)
    }
  }

  private def fileOrStdoutWriter(maybeOutFile: Option[File]) = {
    maybeOutFile
      .map { file =>
        file.getParentFile.mkdirs()
        new FileWriter(file)
      }
      .getOrElse(
        new OutputStreamWriter(Console.out))
  }

  private def execute(
    command: Command,
    maybeHost: Option[String],
    maybeBasePath: Option[String]
  ) = {
    command match {
      case SwaggerCommand(Some(restContextClass)) => gen.generateSwagger(restContextClass, maybeHost, maybeBasePath)
    }
  }
}
