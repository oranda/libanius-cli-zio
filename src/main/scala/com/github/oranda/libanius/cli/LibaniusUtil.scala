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

import com.oranda.libanius.consoleui.ChoiceGroupStrings
import com.oranda.libanius.model.Quiz
import com.oranda.libanius.model.quizgroup.{QuizGroupHeader, WordMapping}
import com.oranda.libanius.model.action.modelComponentsAsQuizItemSources._
import com.oranda.libanius.model.action.NoParams
import com.oranda.libanius.model.action.QuizItemSource.{findAnyUnfinishedQuizItem, produceQuizItem}
import com.oranda.libanius.model.quizitem.QuizItemViewWithChoices

/**
 * Non-ZIO functions which supplement Libanius core.
 *
 * Some of these functions may eventually be migrated to Libanius core.
 */
object LibaniusUtil {

  def isCorrect(userResponse: String, quizItem: QuizItemViewWithChoices, quiz: Quiz) =
    quiz.isCorrect(quizItem.quizGroupHeader, quizItem.prompt.value, userResponse)

  def findQuizItem(quiz: Quiz): Option[QuizItemViewWithChoices] =
    produceQuizItem(quiz, NoParams()).orElse(findAnyUnfinishedQuizItem(quiz, NoParams()))

  def isWordMapping(quizItem: QuizItemViewWithChoices) =
    quizItem.quizGroupHeader.quizGroupType == WordMapping

  def makeQgChoicesText(choices: Seq[QuizGroupHeader]): String =
    choices.zipWithIndex.map { case (header, index) =>
      (index + 1) + ". " + header
    }.mkString("\n")

  def quizIntroText(quiz: Quiz) = s"""
      |Selected quiz groups:
      |${quiz.activeQuizGroupHeaders.mkString("\n")}
      |
      |Number of quiz items: ${quiz.numQuizItems}
      |
      |OK, the quiz begins! To quit, type q at any time.|""".stripMargin

  def makeQuestionText(quizItem: QuizItemViewWithChoices): String = {
    val wordText = s": what is the ${quizItem.responseType} for this ${quizItem.promptType}?"
    val wordTextToShow = if (LibaniusUtil.isWordMapping(quizItem)) wordText else ""
    val answeredText = s" (correctly answered ${quizItem.numCorrectResponsesInARow} times)"
    val answeredTextToShow = if (quizItem.numCorrectResponsesInARow > 0) answeredText else ""
    val questionText = "(" + quizItem.qgCurrentPromptNumber + "): " + quizItem.prompt

    lazy val choicesText = "\n\n" + {
      val choices = ChoiceGroupStrings(quizItem.allChoices)
      choices.choicesWithIndex.map {
        case (header, index) => (index + 1).toString + ". " + header + "\n"
      }.mkString
    }
    lazy val notMultipleChoiceText = "\n(Not multiple choice now. Type it in.)"
    val extraText = if (quizItem.useMultipleChoice) choicesText else notMultipleChoiceText
    val fullQuestionText = questionText + wordTextToShow + answeredTextToShow + extraText
    fullQuestionText
  }

  def saveQuiz(quiz: Quiz): Quiz = {
    dataStore.saveQuiz(quiz, path = conf.filesDir)
    quiz
  }
}
