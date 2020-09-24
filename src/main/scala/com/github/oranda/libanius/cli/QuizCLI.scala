/*
 * Copyright 2019-2020 James McCabe
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

import zio.{ App, IO, ZIO }
import zio.console.{ Console, putStrLn }

import com.oranda.libanius.model.quizgroup.QuizGroupHeader
import com.oranda.libanius.dependencies.AppDependencyAccess
import com.oranda.libanius.model.Quiz

object QuizCLI extends App with AppDependencyAccess {

  def run(args: List[String]) =
    quizCLI.exitCode

  val quizCLI: ZIO[Console, IOException, Quiz] = {
    for {
      qgHeaders   <- availableQgHeaders
      quiz        <- if (qgHeaders.isEmpty) QuizInit.loadDemoQuiz else QuizInit.loadQuiz(qgHeaders)
      quizUpdated <- putStrLn(Text.quizIntro(quiz)) *> QuizLoop.loop(quiz)
    } yield quizUpdated
  }

  def availableQgHeaders: IO[IOException, Seq[QuizGroupHeader]] =
    IO.effect(dataStore.findAvailableQuizGroups.toSeq).refineToOrDie[IOException]
}
