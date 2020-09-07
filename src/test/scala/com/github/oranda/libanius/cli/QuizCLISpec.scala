package com.github.oranda.libanius.cli

import zio.test.Assertion.{contains, containsString, startsWithString}
import zio.test.environment.TestConsole
import zio.test.{DefaultRunnableSpec, assert, suite, testM}

object QuizCLISpec extends DefaultRunnableSpec {

  def spec = suite("Quiz CLI")(
    testM("Available quiz group headers can be found for a quiz") {
      val sampleQGKey = TestData.qgk0
      for {
        qgHeaders <- QuizCLI.availableQgHeaders
      } yield assert(qgHeaders.map(_.quizGroupKey))(contains(sampleQGKey))
    },

    testM("The quiz CLI can be run") {
      for {
        _            <- TestConsole.feedLines("1", "1", "q")
        _            <- QuizCLI.quizCLI
        outputVector <- TestConsole.output
        qgSelectMsg  = outputVector(0).trim
        selectedQgs  = outputVector(1).trim
        status       = outputVector(2).trim
        question     = outputVector(3).trim
        feedback     = outputVector(4).trim
        statusAgain  = outputVector(5).trim
        nextQuestion = outputVector(6).trim
        exitMessage  = outputVector(7).trim
      } yield assert(qgSelectMsg.trim)(startsWithString("Choose quiz group")) &&
                assert(selectedQgs.trim)(startsWithString("Selected quiz groups")) &&
                assert(status.trim)(startsWithString("Score:")) &&
                assert(question)(containsString(("?"))) &&
                (assert(feedback)(startsWithString("Wrong!")) ||
                   assert(feedback)(startsWithString("Correct"))) &&
                assert(statusAgain)(startsWithString("Score: ")) &&
                assert(nextQuestion)(containsString("?")) &&
                assert(exitMessage)(containsString("Exiting..."))
    }
  )
}
