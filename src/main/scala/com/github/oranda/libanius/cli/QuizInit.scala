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

import zio.{ IO, UIO, URIO, ZIO }

import com.oranda.libanius.model.Quiz
import com.oranda.libanius.model.action.modelComponentsAsQuizItemSources.dataStore
import com.oranda.libanius.model.quizgroup.{ QuizGroup, QuizGroupHeader }
import zio.Console
import zio.Console.{ printLine, readLine }

object QuizInit {
  val getUser: UIO[Quiz] = ZIO.succeed(Quiz.demoQuiz())

  val loadDemoQuiz: IO[IOException, Quiz] =
    printLine("No quiz groups found. Defaulting to dummy data.") *> ZIO.succeed(Quiz.demoQuiz())

  def loadQuiz(availableQgHeaders: Seq[QuizGroupHeader]): IO[IOException, Quiz] =
    for
      qgHeaders <- getQuizGroupsFromUser(availableQgHeaders)
      quiz <- quizForQgHeaders(qgHeaders)
    yield quiz

  def getQuizGroupsFromUser(
    qgHeaders: Seq[QuizGroupHeader]
  ): IO[IOException, Seq[QuizGroupHeader]] = {
    val chooseQgsText =
      "\nChoose quiz group(s). For more than one, separate with commas, e.g. 1,2,3\n\n"
    for
      _ <- printLine(chooseQgsText + Text.quizGroupChoices(qgHeaders) + "\n")
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
        if userChoicesAreValid then
          ZIO.succeed(userChoices.map(_.toInt - 1).map(availableQgHeaders).toSeq)
        else
          printLine("\nUnrecognized option") *> getQuizGroupsFromUser(availableQgHeaders)
    yield chosenOptions

  def quizForQgHeaders(qgHeaders: Seq[QuizGroupHeader]): IO[IOException, Quiz] = {
    val quizGroups: Map[QuizGroupHeader, QuizGroup] =
      qgHeaders.map(header => (header, dataStore.loadQuizGroupCore(header))).toMap
    ZIO.attempt(Quiz(quizGroups)).refineToOrDie[IOException]
  }
}
