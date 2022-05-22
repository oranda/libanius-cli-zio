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

import com.oranda.libanius.model.Quiz
import com.oranda.libanius.model.quizgroup.{QuizGroup, QuizGroupHeader}
import zio.Console.{printLine, readLine}
import zio.{Console, IO, UIO, URIO, ZIO}

import java.io.IOException

object QuizInit {
  val getUser: UIO[Quiz] = ZIO.succeed(Quiz.demoQuiz())

  val loadDemoQuiz: IO[IOException, Quiz] =
    printLine("No quiz groups found. Defaulting to dummy data.") *> ZIO.succeed(Quiz.demoQuiz())

  def loadQuiz(availableQgHeaders: Seq[QuizGroupHeader]): ZIO[PersistentData, Throwable, Quiz] =
    for
      qgHeaders <- getQuizGroupsFromUser(availableQgHeaders)
      data      <- ZIO.service[PersistentData]
      quiz      <- data.loadQuizForQgHeaders(qgHeaders)
    yield quiz

  def getQuizGroupsFromUser(
    qgHeaders: Seq[QuizGroupHeader]
  ): IO[IOException, Seq[QuizGroupHeader]] = {
    val chooseQgsText = "\nChoose quiz group\n\n"
    for
      _       <- printLine(chooseQgsText + Text.quizGroupChoices(qgHeaders) + "\n")
      choices <- getQgSelectionFromInput(qgHeaders)
    yield choices
  }

  def getQgSelectionFromInput(
    availableQgHeaders: Seq[QuizGroupHeader]
  ): IO[IOException, Seq[QuizGroupHeader]] =
    for
      userInput          <- readLine
      validChoices        = (1 to availableQgHeaders.size).map(_.toString)
      userChoices         = userInput.split(",")
      userChoicesAreValid = userChoices.toSet.subsetOf(validChoices.toSet)
      chosenOptions <-
        if userChoicesAreValid then ZIO.succeed(userChoices.map(_.toInt - 1).map(availableQgHeaders).toSeq)
        else printLine("\nUnrecognized option") *> getQuizGroupsFromUser(availableQgHeaders)
    yield chosenOptions
}
