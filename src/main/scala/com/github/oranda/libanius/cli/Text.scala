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

import com.oranda.libanius.consoleui.ChoiceGroupStrings
import com.oranda.libanius.model.Quiz
import com.oranda.libanius.model.quizgroup.QuizGroupHeader
import com.oranda.libanius.model.quizitem.QuizItemViewWithChoices

/**
 * Functions to compose text.
 */
object Text {
  def quizIntro(quiz: Quiz) = s"""
                                 |Selected quiz groups:
                                 |${quiz.activeQuizGroupHeaders.mkString("\n")}
                                 |
                                 |Number of quiz items: ${quiz.numQuizItems}
                                 |
                                 |OK, the quiz begins! To quit, type q at any time.|""".stripMargin

  def quizGroupChoices(choices: Seq[QuizGroupHeader]): String =
    choices.zipWithIndex.map { case (header, index) => s"${index + 1}.$header\n"}.mkString("\n")

  def question(quizItem: QuizItemViewWithChoices): String = {
    val wordText           = s": what is the ${quizItem.responseType} for this ${quizItem.promptType}?"
    val wordTextToShow     = if quizItem.isWordMapping then wordText else ""
    val answeredText       = s" (correctly answered ${quizItem.numCorrectResponsesInARow} times)"
    val answeredTextToShow = if quizItem.numCorrectResponsesInARow > 0 then answeredText else ""
    val questionText       = "(" + quizItem.qgCurrentPromptNumber + "): " + quizItem.prompt

    lazy val choicesText = "\n\n" + {
      val choices = ChoiceGroupStrings(quizItem.allChoices)
      choices.choicesWithIndex.map { case (header, index) => s"${index + 1}. $header\n"}.mkString
    }
    lazy val notMultipleChoiceText = "\n(Not multiple choice now. Type it in.)"
    val extraText                  = if quizItem.useMultipleChoice then choicesText else notMultipleChoiceText
    val fullQuestionText           = questionText + wordTextToShow + answeredTextToShow + extraText
    fullQuestionText
  }

  def quizCompleted: String = s"""
                                 |No more questions found! Congratulations on completing the quiz!
                                 |
                                 |If you felt Libanius helped you learn this subject, and want to learn
                                 |something else, consider making your own quiz file. It's easy! See the
                                 |README for details.
                                 |""".stripMargin
}
