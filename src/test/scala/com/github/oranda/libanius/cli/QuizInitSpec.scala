package com.github.oranda.libanius.cli

import zio.ZEnvironment
import zio.test._
import zio.test.Assertion.{ equalTo, startsWithString }

object QuizInitSpec extends ZIOSpecDefault {
  override def spec = suite("Quiz Init")(
    test("Quiz groups can be selected from a list") {
      val expectedQgHeaders = TestData.qghs.slice(1, 3)
      for
        _    <- TestConsole.feedLines("2,3")
        qghs <- QuizInit.getQgSelectionFromInput(TestData.qghs)
      yield assert(qghs)(equalTo(expectedQgHeaders))
    },
    test("If the user inputs an invalid selection for a quiz group, an error message is output") {
      for
        _       <- TestConsole.feedLines("0", "1")
        _       <- QuizInit.getQgSelectionFromInput(TestData.qghs)
        output  <- TestConsole.output
        errorMsg = output(0).trim
      yield assert(errorMsg)(startsWithString("Unrecognized option"))
    },
    test("The user can be prompted to select quiz groups from a list") {
      val expectedQgHeaders = TestData.qghs.slice(1, 3)
      for
        _    <- TestConsole.feedLines("2,3")
        qghs <- QuizInit.getQuizGroupsFromUser(TestData.qghs)
      yield assert(qghs)(equalTo(expectedQgHeaders))
    },
    test("Load quiz") {
      assertM(
        for
          _    <- TestConsole.feedLines("1")
          quiz <- QuizInit.loadQuiz(availableQgHeaders = Seq(TestData.qgh0))
        yield quiz
      )(equalTo(TestData.quizOneGroup)).provideEnvironment(ZEnvironment(DataStoreStub()))
    })
}


/*

    test("Load quiz") {
      assertM(
        for
          quiz <- QuizInit.loadQuiz(availableQgHeaders = Seq(TestData.qgh0))
        yield quiz
      )(equalTo(TestData.quizOneGroup)).provideEnvironment(ZEnvironment(PersistentDataStub()))
    }
  )
*/