/*
 * Copyright 2019 James McCabe
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

import zio.{IO, URIO, ZIO}
import zio.console.{Console, getStrLn, putStrLn}

import com.oranda.libanius.consoleui.{Quit, TextAnswer, UserConsoleResponse}
import com.oranda.libanius.model.{Correct, Quiz}
import com.oranda.libanius.model.quizitem.QuizItemViewWithChoices
import com.oranda.libanius.util.StringUtil

object QuizLoop {
  def loop(quiz: Quiz): ZIO[Console, IOException, Quiz] = for {
    _ <- showQuizStatus(quiz)
    quiz <- LibaniusUtil.findQuizItem(quiz) match {
      case None => putStrLn(s"No more questions found! Done!") *> IO.succeed(quiz)
      case Some(quizItem) => runQuizItemAndLoop(quiz, quizItem)
    }
  } yield quiz

  def runQuizItemAndLoop(
    quiz: Quiz,
    quizItem: QuizItemViewWithChoices
  ): ZIO[Console, IOException, Quiz] = for {
    response <- askQuestionAndGetResponse(quizItem)
    newState <- response match {
      case Quit => exit(quiz)
      case TextAnswer(answer) => processQuizItemAndLoop(quiz, answer, quizItem)
      case _ => putStrLn("Invalid response") *> runQuizItemAndLoop(quiz, quizItem)
    }
  } yield newState

  def exit(quiz: Quiz): ZIO[Console, IOException, Quiz] =
    putStrLn("Exiting...") *> IO.effect(LibaniusUtil.saveQuiz(quiz)).refineToOrDie[IOException]

  def askQuestionAndGetResponse(
    quizItem: QuizItemViewWithChoices
  ): ZIO[Console, IOException, UserConsoleResponse] = for {
    input <- putStrLn(s"${LibaniusUtil.makeQuestionText(quizItem)}") *> getStrLn
    userResponse <- input.trim match {
      case "" => askQuestionAndGetResponse(quizItem)
      case "q" | "quit" => IO.succeed(Quit)
      case answer => IO.succeed(TextAnswer(answer))
    }
  } yield userResponse

  def processQuizItemAndLoop(
    quiz: Quiz,
    answer: String,
    quizItem: QuizItemViewWithChoices
  ): ZIO[Console, IOException, Quiz] = for {
    updatedQuiz <- processQuizItem(quiz, answer, quizItem)
    quiz <- loop(updatedQuiz)
  } yield quiz

  def processQuizItem(
    quiz: Quiz,
    answer: String,
    quizItem: QuizItemViewWithChoices
  ): URIO[Console, Quiz] =
    if (quizItem.useMultipleChoice)
      processMultipleChoiceItem(quiz, answer, quizItem)
    else
      processUserAnswer(quiz, answer, quizItem)

  def processMultipleChoiceItem(
    quiz: Quiz,
    answer: String,
    quizItem: QuizItemViewWithChoices
  ): URIO[Console, Quiz] = {
    val validChoices = (1 to quizItem.allChoices.size).map(_.toString)
    lazy val invalidMsg = s"'$answer' is not a valid choice. Please enter a number in the list."
    if (!validChoices.contains(answer)) {
      putStrLn("\n" + invalidMsg + "\n") *> IO.succeed(quiz)
    } else {
      val selectedAnswer = quizItem.allChoices(answer.toInt - 1)
      processUserAnswer(quiz, selectedAnswer, quizItem)
    }
  }

  def processUserAnswer(
    quiz: Quiz,
    answer: String,
    quizItem: QuizItemViewWithChoices
  ): URIO[Console, Quiz] = {
    val isCorrect = quiz.isCorrect(quizItem.quizGroupKey, quizItem.prompt.value, answer) == Correct
    val message = if (isCorrect) "Correct!\n"
                  else s"Wrong! It's ${quizItem.correctResponse} not $answer"
    putStrLn(s"\n$message\n") *> IO.succeed(quiz.updateWithUserResponse(
      isCorrect,
      quizItem.quizGroupHeader,
      quizItem.quizItem
    ))
  }

  def showQuizStatus(quiz: Quiz): URIO[Console, Unit] = {
    val formattedScore = StringUtil.formatScore(quiz.scoreSoFar)
    putStrLn(s"Score: $formattedScore\n")
  }
}