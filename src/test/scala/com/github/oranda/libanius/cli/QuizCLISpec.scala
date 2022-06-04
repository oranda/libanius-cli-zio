package com.github.oranda.libanius.cli

import com.github.oranda.libanius.cli.QuizInitSpec.{suite, test}
import zio.ZEnvironment
import zio.test.Assertion.{containsString, equalTo, startsWithString}
import zio.test.{TestConsole, ZIOSpecDefault, assert, assertM}

object QuizCLISpec extends ZIOSpecDefault {
  override def spec = suite("Quiz CLI")(
    test("The quiz CLI can be run") {
      val expectedQghsChoices = Text.quizGroupChoicesWithPrompt(TestData.qghs).trim
      val expectedQuestion     = "what is the German word for this English word?"
      val expectedFullQuestion = "(0): solve: " + expectedQuestion
      val expectedQuizIntro: String = Text.quizIntro(TestData.quizOneGroup).trim
      for
        _             <- TestConsole.feedLines("1", "q")
        _             <- QuizCLI.runCLI.provideEnvironment(ZEnvironment(DataStoreStub()))
        outputVector  <- TestConsole.output
        qghsChoices   = outputVector(0).trim
        quizIntro     = outputVector(1).trim
        status        = outputVector(2).trim
        question      = outputVector(3).trim
        exitMessage   = outputVector(4).trim
      yield assert(qghsChoices)(equalTo(expectedQghsChoices)) &&
        assert(quizIntro)(equalTo(expectedQuizIntro)) &&
        assert(status)(equalTo("Score: 0.0%")) &&
        assert(question)(startsWithString(expectedFullQuestion)) &&
        assert(exitMessage)(containsString("Saving quiz state..."))
    })
}
