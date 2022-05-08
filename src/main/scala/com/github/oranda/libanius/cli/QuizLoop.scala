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

import zio.{ IO, URIO, ZIO }
import com.oranda.libanius.dependencies.AppDependencyAccess
import com.oranda.libanius.model.{ Correct, Quiz }
import com.oranda.libanius.model.quizitem.QuizItemViewWithChoices
import com.oranda.libanius.util.StringUtil
import com.oranda.libanius.model.action.FindQuizItem
import zio.Console
import zio.Console.{ printLine, readLine }

sealed trait UserConsoleResponse
case class UserTextAnswer(text: String) extends UserConsoleResponse
case object Quit                        extends UserConsoleResponse

object QuizLoop extends AppDependencyAccess {
  def loop(quiz: Quiz): IO[IOException, Quiz] =
    for
      _ <- showQuizStatus(quiz)
      quiz <- FindQuizItem.run(quiz) match {
                case None           => printLine(Text.quizCompleted) *> ZIO.succeed(quiz)
                case Some(quizItem) => runQuizItemAndLoop(quiz, quizItem)
              }
    yield quiz

  def runQuizItemAndLoop(
    quiz: Quiz,
    quizItem: QuizItemViewWithChoices
  ): IO[IOException, Quiz] =
    for
      response <- askQuestionAndGetResponse(quizItem)
      newState <- response match {
                    case Quit                   => exit(quiz)
                    case UserTextAnswer(answer) => processQuizItemAndLoop(quiz, answer, quizItem)
                  }
    yield newState

  def exit(quiz: Quiz): IO[IOException, Quiz] =
    printLine("Exiting...") *> ZIO.attempt(dataStore.saveQuiz(quiz)).refineToOrDie[IOException]

  def askQuestionAndGetResponse(
    quizItem: QuizItemViewWithChoices
  ): IO[IOException, UserConsoleResponse] =
    for
      input <- printLine(s"${Text.question(quizItem)}") *> readLine
      userResponse <- input.trim match {
                        case ""           => askQuestionAndGetResponse(quizItem)
                        case "q" | "quit" => ZIO.succeed(Quit)
                        case answer       => ZIO.succeed(UserTextAnswer(answer))
                      }
    yield userResponse

  def processQuizItemAndLoop(
    quiz: Quiz,
    answer: String,
    quizItem: QuizItemViewWithChoices
  ): IO[IOException, Quiz] =
    for
      updatedQuiz <- processQuizItem(quiz, answer, quizItem)
      quiz        <- loop(updatedQuiz)
    yield quiz

  def processQuizItem(
    quiz: Quiz,
    answer: String,
    quizItem: QuizItemViewWithChoices
  ): IO[IOException, Quiz] =
    if quizItem.useMultipleChoice then
      processMultipleChoiceItem(quiz, answer, quizItem)
    else
      processUserAnswer(quiz, answer, quizItem)

  def processMultipleChoiceItem(
    quiz: Quiz,
    answer: String,
    quizItem: QuizItemViewWithChoices
  ): IO[IOException, Quiz] = {
    val validChoices    = (1 to quizItem.allChoices.size).map(_.toString)
    lazy val invalidMsg = s"'$answer' is not a valid choice. Please enter a number in the list."
    if !validChoices.contains(answer) then {
      printLine("\n" + invalidMsg + "\n") *> ZIO.succeed(quiz)
    } else {
      val selectedAnswer = quizItem.allChoices(answer.toInt - 1)
      processUserAnswer(quiz, selectedAnswer, quizItem)
    }
  }

  def processUserAnswer(
    quiz: Quiz,
    answer: String,
    quizItem: QuizItemViewWithChoices
  ): IO[IOException, Quiz] = {
    val isCorrect     = quiz.isCorrect(quizItem.quizGroupKey, quizItem.prompt.value, answer) == Correct
    lazy val wrongMsg = s"Wrong! It's ${quizItem.correctResponse} not $answer"
    val message       = if isCorrect then "Correct!\n" else wrongMsg
    printLine(s"\n$message\n") *> ZIO.succeed(
      quiz.updateWithUserResponse(
        isCorrect,
        quizItem.quizGroupHeader,
        quizItem.quizItem
      )
    )
  }

  def showQuizStatus(quiz: Quiz): IO[IOException, Unit] = {
    val formattedScore = StringUtil.formatScore(quiz.scoreSoFar)
    printLine(s"Score: $formattedScore\n")
  }
}
