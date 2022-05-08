/*
 * Copyright 2019-2022 James McCabe
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

package com.github.oranda.libanius.cli

import java.io.IOException
import zio.{ Console, IO, ZEnvironment, ZIO, ZIOAppDefault }
import com.github.oranda.libanius.cli.QuizCLI.validateEnv
import com.oranda.libanius.model.quizgroup.QuizGroupHeader
import com.oranda.libanius.dependencies.AppDependencyAccess
import com.oranda.libanius.model.Quiz
import zio.Console._

object QuizCLI extends ZIOAppDefault with AppDependencyAccess {
  def run = quizCLI

  val quizCLI: ZIO[Any, IOException, Unit] =
    for
      qgHeaders <- availableQgHeaders
      quiz      <- if qgHeaders.isEmpty then QuizInit.loadDemoQuiz else QuizInit.loadQuiz(qgHeaders)
      _         <- printLine(Text.quizIntro(quiz)) *> QuizLoop.loop(quiz)
    yield ()

  def availableQgHeaders: IO[IOException, Seq[QuizGroupHeader]] =
    ZIO.attempt(dataStore.findAvailableQuizGroups.toSeq).refineToOrDie[IOException]
}
