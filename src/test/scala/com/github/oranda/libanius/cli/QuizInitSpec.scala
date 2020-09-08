package com.github.oranda.libanius.cli

import zio.test.Assertion.{equalTo, startsWithString}
import zio.test.environment.TestConsole
import zio.test.{DefaultRunnableSpec, assert, suite, testM}

object QuizInitSpec extends DefaultRunnableSpec {
  def spec = suite("Quiz Init")(
    testM("A quiz can be loaded from supplied quiz group headers") {
      for {
        quiz      <- QuizInit.quizForQgHeaders(TestData.qghs)
      } yield assert(quiz.activeQuizGroupHeaders)(equalTo(TestData.qghs.toSet))
    },
    testM("Quiz groups can be selected from a list") {
      val expectedQgHeaders = TestData.qghs.slice(1,3)
      for {
        _    <- TestConsole.feedLines("2,3")
        qghs <- QuizInit.getQgSelectionFromInput(TestData.qghs)
      } yield assert(qghs)(equalTo(expectedQgHeaders))
    },
    testM("If the user inputs an invalid selection for a quiz group, an error message is output") {
      for {
        _        <- TestConsole.feedLines("0", "1")
        _        <- QuizInit.getQgSelectionFromInput(TestData.qghs)
        output   <- TestConsole.output
        errorMsg = output(0).trim
      } yield assert(errorMsg)(startsWithString("Unrecognized option"))
    },
    testM("The user can be prompted to select quiz groups from a list") {
      val expectedQgHeaders = TestData.qghs.slice(1,3)
      for {
        _    <- TestConsole.feedLines("2,3")
        qghs <- QuizInit.getQuizGroupsFromUser(TestData.qghs)
      } yield assert(qghs)(equalTo(expectedQgHeaders))
    },
    testM("The user can be prompted for quiz groups, and a quiz can be loaded from them") {
      val expectedQgHeaders = TestData.qghs.slice(1,3)
      for {
        _    <- TestConsole.feedLines("2,3")
        quiz <- QuizInit.loadQuiz(TestData.qghs)
      } yield assert(quiz.activeQuizGroupHeaders)(equalTo(expectedQgHeaders.toSet))
    }
  )
}