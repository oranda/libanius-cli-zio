package com.github.oranda.libanius.cli

import com.oranda.libanius.model.action.serialize.Separator
import com.oranda.libanius.model.{Quiz, UserResponsesAll}
import com.oranda.libanius.model.quizgroup.QuizGroupType.WordMapping
import com.oranda.libanius.model.quizgroup.{QuizGroup, QuizGroupHeader, QuizGroupKey, QuizGroupMemoryLevel}
import com.oranda.libanius.model.quizitem.{QuizItem, QuizItemViewWithChoices}

import scala.collection.immutable.Map

object TestData {
  val userResponsesAll = UserResponsesAll()
  val prompt = "solve"
  val response = "nachlösen"
  val quizItem = QuizItem(prompt, response, userResponsesAll)

  val qgk0 = QuizGroupKey("English word", "German word", WordMapping)
  val qgh0 = QuizGroupHeader(qgk0, Separator("|"), 6, 4)
  val qgk1 = QuizGroupKey("German word", "English word", WordMapping)
  val qgh1 = QuizGroupHeader(qgk1, Separator("|"), 6, 4)
  val qgk2 = QuizGroupKey("English word", "Spanish word", WordMapping)
  val qgh2 = QuizGroupHeader(qgk2, Separator("|"), 6, 4)
  val qghs = Seq(qgh0, qgh1, qgh2)

  val qgml = QuizGroupMemoryLevel(0, 0, Stream(quizItem))

  val memLevelMap = Map(0 -> qgml)
  val quizGroup = QuizGroup(memLevelMap)

  val falseAnswers = List("Vertrag", "unterhalten")
  def quizItemView(useMultipleChoice: Boolean) =
    QuizItemViewWithChoices(quizItem, 0, qgh0, falseAnswers, 0, useMultipleChoice)
  val quiz = Quiz().addQuizGroups(Map(qgh0 -> quizGroup))
}