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

import org.apache.commons.io.FileUtils
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito._
import org.scalatest.OneInstancePerTest
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar
import za.co.absa.commons.io.TempDirectory
import za.co.absa.commons.scalatest.SystemExitFixture.ExitException
import za.co.absa.commons.scalatest.{ConsoleStubs, SystemExitFixture}

import java.io.File

class RapidGenCLISpec
  extends AnyFlatSpec
    with OneInstancePerTest
    with MockitoSugar
    with Matchers
    with ConsoleStubs
    with SystemExitFixture.SuiteHook {

  private val genMock = mock[DocGenerator]
  private val cli = new RapidGenCLI(genMock)

  behavior of "RapidGenCLI.main()"

  it should "when called with no args, print welcome message" in intercept[ExitException] {
    captureStdErr(cli.exec(Array.empty)) should include("Try --help for more information")
  }

  it should "when called with a wrong option, print error message" in intercept[ExitException] {
    captureStdErr(cli.exec(Array("--wrong-option"))) should include("Unknown option --wrong-option")
  }

  it should "when called with a wrong argument, print error message" in intercept[ExitException] {
    captureStdErr(cli.exec(Array("wrong-argument"))) should include("Argument <class> failed when given 'wrong-argument'")
  }

  it should "print JSON to stdout by default" in {
    val dummyClass = classOf[Any]
    when(genMock.generateSwagger(ArgumentMatchers.eq(dummyClass), any(), any())).thenReturn("{dummy JSON}")
    captureStdOut(cli.exec(Array("swagger", dummyClass.getName))) should be("{dummy JSON}")
  }

  it should "write to file" in {
    val dummyClass = classOf[Any]
    val tmpDir = TempDirectory().deleteOnExit().path.toFile
    val file = new File(tmpDir, "a/b/c")
    val rapidGenConfig = Some(RapidGenConfig(Command.SwaggerCommand(Some(dummyClass)), Some(file), None, None))
    when(genMock.generateSwagger(ArgumentMatchers.eq(dummyClass), any(), any())).thenReturn("{dummy JSON}")
    cli.exec(Array("swagger", "-o", file.getPath, dummyClass.getName))
    FileUtils.readFileToString(file, "UTF-8") should be("{dummy JSON}")
  }

  it should """remove "host":"localhost" if -h set and no host given""" in {
    val dummyClass = classOf[Any]
    when(genMock.generateSwagger(ArgumentMatchers.eq(dummyClass), any(), any())).
      thenReturn(s"""{"dummy":"JSON","host":"${Constants.BLANK_HOST_PLACE_HOLDER}","basePath":"/"}""")
    captureStdOut(cli.exec(Array("swagger", "-h", "", dummyClass.getName))) should be("""{"dummy":"JSON","basePath":"/"}""")
  }

  it should """remove "host":"localhost" if -h set and null host given""" in {
    val dummyClass = classOf[Any]
    when(genMock.generateSwagger(ArgumentMatchers.eq(dummyClass), any(), any())).
      thenReturn(s"""{"dummy":"JSON","host":"${Constants.BLANK_HOST_PLACE_HOLDER}","basePath":"/"}""")
    captureStdOut(cli.exec(Array("swagger", "-h", null, dummyClass.getName))) should be("""{"dummy":"JSON","basePath":"/"}""")
  }

  it should """replace "host":"localhost" with "host":"my-host" if -h my-host given""" in {
    val dummyClass = classOf[Any]
    when(genMock.generateSwagger(ArgumentMatchers.eq(dummyClass), any(), any())).
      thenReturn(s"""{"dummy":"JSON","host":"my-host","basePath":"/"}""")
    captureStdOut(cli.exec(Array("swagger", "-h", "my-host", dummyClass.getName))) should be("""{"dummy":"JSON","host":"my-host","basePath":"/"}""")
  }

  it should """replace "basePath":"/" with "basePath":"/rest" if -b /rest given""" in {
    val dummyClass = classOf[Any]
    when(genMock.generateSwagger(ArgumentMatchers.eq(dummyClass), any(), any())).
      thenReturn(s"""{"dummy":"JSON","host":"localhost","basePath":"/fff"}""")
    captureStdOut(cli.exec(Array("swagger", "-b", "/rest", dummyClass.getName))) should be("""{"dummy":"JSON","host":"localhost","basePath":"/fff"}""")
  }

  it should """remove "basePath":"/" if -b set and null base Path given""" in {
    val dummyClass = classOf[Any]
    when(genMock.generateSwagger(ArgumentMatchers.eq(dummyClass), any(), any())).
      thenReturn(s"""{"dummy":"JSON","host":"localhost","basePath":"${Constants.BLANK_BASE_PATH_PLACE_HOLDER}"}""")
    captureStdOut(cli.exec(Array("swagger", "-b", "/rest", dummyClass.getName))) should be("""{"dummy":"JSON","host":"localhost"}""")
  }
}

object RapidGenCLISpec {

  implicit class StringWrapper(str: String) {
    def normalizeWhitespaces: String = str.replaceAll("\\s+", " ")
  }

}
