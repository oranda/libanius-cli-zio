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

import com.oranda.libanius.dependencies.AppDependencyAccess
import com.oranda.libanius.model.Quiz
import com.oranda.libanius.model.quizgroup.QuizGroupHeader
import zio.*
import zio.Console.*

import java.io.IOException

object QuizCLI {
  def runCLI: ZIO[PersistentData, Throwable, Unit] =
    for
      data      <- ZIO.service[PersistentData]
      qgHeaders <- data.findAvailableQuizGroups
      quiz      <- if qgHeaders.isEmpty then QuizInit.loadDemoQuiz else QuizInit.loadQuiz(qgHeaders)
      _         <- printLine(Text.quizIntro(quiz)) *> QuizLoop.loop(quiz)
    yield ()
}
