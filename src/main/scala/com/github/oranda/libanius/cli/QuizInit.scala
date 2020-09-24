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

import zio.{ IO, UIO, URIO, ZIO }
import zio.console.{ Console, getStrLn, putStrLn }

import com.oranda.libanius.model.Quiz
import com.oranda.libanius.model.action.modelComponentsAsQuizItemSources.dataStore
import com.oranda.libanius.model.quizgroup.{ QuizGroup, QuizGroupHeader }

object QuizInit {
  val getUser: UIO[Quiz] = IO.succeed(Quiz.demoQuiz())

  val loadDemoQuiz: URIO[Console, Quiz] =
    putStrLn("No quiz groups found. Defaulting to dummy data.") *> IO.succeed(Quiz.demoQuiz())

  def loadQuiz(availableQgHeaders: Seq[QuizGroupHeader]): ZIO[Console, IOException, Quiz] =
    for {
      qgHeaders <- getQuizGroupsFromUser(availableQgHeaders)
      quiz      <- quizForQgHeaders(qgHeaders)
    } yield quiz

  def getQuizGroupsFromUser(
    qgHeaders: Seq[QuizGroupHeader]
  ): ZIO[Console, IOException, Seq[QuizGroupHeader]] = {
    val chooseQgsText =
      "\nChoose quiz group(s). For more than one, separate with commas, e.g. 1,2,3\n\n"
    for {
      _       <- putStrLn(chooseQgsText + Text.quizGroupChoices(qgHeaders) + "\n")
      choices <- getQgSelectionFromInput(qgHeaders)
    } yield choices
  }

  def getQgSelectionFromInput(
    availableQgHeaders: Seq[QuizGroupHeader]
  ): ZIO[Console, IOException, Seq[QuizGroupHeader]] =
    for {
      userInput          <- getStrLn
      validChoices        = (1 to availableQgHeaders.size).map(_.toString)
      userChoices         = userInput.split(",")
      userChoicesAreValid = userChoices.toSet.subsetOf(validChoices.toSet)
      chosenOptions <-
        if (userChoicesAreValid)
          IO.succeed(userChoices.map(_.toInt - 1).map(availableQgHeaders).toSeq)
        else
          putStrLn("\nUnrecognized option") *> getQuizGroupsFromUser(availableQgHeaders)
    } yield chosenOptions

  def quizForQgHeaders(qgHeaders: Seq[QuizGroupHeader]): IO[IOException, Quiz] = {
    val quizGroups: Map[QuizGroupHeader, QuizGroup] =
      qgHeaders.map(header => (header, dataStore.loadQuizGroupCore(header))).toMap
    IO.effect(Quiz(quizGroups)).refineToOrDie[IOException]
  }
}
