package com.github.oranda.libanius.cli

import com.oranda.libanius.model.action.serialize._
import com.oranda.libanius.model.{ Quiz, UserResponsesAll }
import com.oranda.libanius.model.quizgroup.QuizGroupType.WordMapping
import com.oranda.libanius.model.quizgroup._
import com.oranda.libanius.model.quizitem.{ QuizItem, QuizItemViewWithChoices }

object TestData {
  val userResponsesAll = UserResponsesAll()
  val prompt           = "solve"
  val response         = "nachlösen"
  val quizItem         = QuizItem(prompt, response, userResponsesAll)

  val qgk0 = QuizGroupKey("English word", "German word", WordMapping)
  val qgh0 = QuizGroupHeader(qgk0, ParamsSeparator("|"), 6, 4)
  val qgk1 = QuizGroupKey("German word", "English word", WordMapping)
  val qgh1 = QuizGroupHeader(qgk1, ParamsSeparator("|"), 6, 4)
  val qgk2 = QuizGroupKey("English word", "Spanish word", WordMapping)
  val qgh2 = QuizGroupHeader(qgk2, ParamsSeparator("|"), 6, 4)
  val qghs = Seq(qgh0, qgh1, qgh2)

  val qgml = QuizGroupMemoryLevel(0, 0, LazyList(quizItem))

  val memLevelMap    = Map(0 -> qgml)
  val quizGroup      = QuizGroup(memLevelMap)
  val quizGroupEmpty = QuizGroup()

  val falseAnswers = List("Vertrag", "unterhalten")
  def quizItemView(useMultipleChoice: Boolean) =
    QuizItemViewWithChoices(quizItem, 0, qgh0, falseAnswers, 0, useMultipleChoice)
  val quizOneGroup = Quiz().addQuizGroups(Map(qgh0 -> quizGroup))
  val quizFull = Quiz().addQuizGroups(
    Map(
      qgh0 -> quizGroup,
      qgh1 -> quizGroupEmpty,
      qgh2 -> quizGroupEmpty
    )
  )
}
