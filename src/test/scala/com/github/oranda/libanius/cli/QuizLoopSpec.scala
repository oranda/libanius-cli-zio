package com.github.oranda.libanius.cli

import zio.test.Assertion.{equalTo, not, containsString, startsWithString}
import zio.test.environment.TestConsole
import zio.test.{DefaultRunnableSpec, assert, suite, testM}

object QuizLoopSpec extends DefaultRunnableSpec {
  def spec = suite("Quiz Loop")(
    testM("Showing the quiz status displays the score of the quiz") {
      for {
        _      <- QuizLoop.showQuizStatus(TestData.quiz)
        output <- TestConsole.output
      } yield assert(output.map(_.trim))(equalTo(Vector("Score: 0.0%")))
    },

    testM("Processing a correct answer displays feedback and updates the quiz") {
      val qi = TestData.quizItem
      val qiv = TestData.quizItemView(useMultipleChoice = false)
      val isCorrect = true
      val expectedQuiz = TestData.quiz.updateWithUserResponse(isCorrect, TestData.qgh0, qi)
      for {
        updatedQuiz <- QuizLoop.processUserAnswer(TestData.quiz, qiv.correctResponse, qiv)
        output      <- TestConsole.output
      } yield assert(output.map(_.trim))(equalTo(Vector("Correct!"))) &&
                assert(updatedQuiz)(equalTo(expectedQuiz))
    },

    testM("Processing a wrong answer displays feedback and updates the quiz") {
      val qiv = TestData.quizItemView(useMultipleChoice = false)
      val qi = TestData.quizItem
      val answer = "Wrong answer"
      val expectedOutput = s"Wrong! It's ${qi.correctResponse} not $answer"
      val isCorrect = false
      val expectedQuiz = TestData.quiz.updateWithUserResponse(isCorrect, TestData.qgh0, qi)
      for {
        updatedQuiz <- QuizLoop.processUserAnswer(TestData.quiz, answer, qiv)
        output      <- TestConsole.output
      } yield assert(output.map(_.trim))(equalTo(Vector(expectedOutput))) &&
                assert(updatedQuiz)(equalTo(expectedQuiz))
    },

    testM("Processing a multiple-choice question displays feedback and updates the quiz") {
      val qiv = TestData.quizItemView(useMultipleChoice = true)
      val answer = "1"    // could be right or wrong, because choices are presented randomly
      for {
        updatedQuiz <- QuizLoop.processMultipleChoiceItem(TestData.quiz, answer, qiv)
        output      <- TestConsole.output
      } yield assert(output.length)(equalTo(1)) &&
                assert(updatedQuiz)(not(equalTo(TestData.quiz)))
    },

    testM("For a multiple-choice question, bad user input causes an error to be displayed") {
      val qiv = TestData.quizItemView(useMultipleChoice = true)
      val answer = "0"
      val expectedErrMsg = s"'$answer' is not a valid choice. Please enter a number in the list."
      val expectedQuiz = TestData.quiz // no change
      for {
        updatedQuiz <- QuizLoop.processMultipleChoiceItem(TestData.quiz, answer, qiv)
        output      <- TestConsole.output
      } yield assert(output.map(_.trim))(equalTo(Vector(expectedErrMsg))) &&
                assert(updatedQuiz)(equalTo(expectedQuiz))
    },

    testM("If a quiz item is multiple-choice, processQuizItem accepts a number") {
      val qiv = TestData.quizItemView(useMultipleChoice = true)
      val answer = "1"    // could be right or wrong, because choices are presented randomly
      for {
        updatedQuiz <- QuizLoop.processMultipleChoiceItem(TestData.quiz, answer, qiv)
        output      <- TestConsole.output
      } yield assert(output.length)(equalTo(1)) &&
        assert(updatedQuiz)(not(equalTo(TestData.quiz)))
    },

    testM("If a quiz item is not multiple-choice, the user's answer will be processed directly") {
      val qi = TestData.quizItem
      val qiv = TestData.quizItemView(useMultipleChoice = false)
      val isCorrect = true
      val expectedQuiz = TestData.quiz.updateWithUserResponse(isCorrect, TestData.qgh0, qi)
      for {
        updatedQuiz <- QuizLoop.processQuizItem(TestData.quiz, qiv.correctResponse, qiv)
        output      <- TestConsole.output
      } yield assert(output.map(_.trim))(equalTo(Vector("Correct!"))) &&
                assert(updatedQuiz)(equalTo(expectedQuiz))
    },

    testM("Exiting should showing a message that we are exiting...") {
      for {
        _      <- QuizLoop.exit(TestData.quiz)
        output <- TestConsole.output
      } yield assert(output.map(_.trim))(equalTo(Vector("Exiting...")))
    },

    testM("Asking a question outputs the right text to the user") {
      val qiv = TestData.quizItemView(useMultipleChoice = false)
      val questionText = Text.question(qiv)
      val responseToGive = "Paris"
      for {
        _      <- TestConsole.feedLines(responseToGive)
        _      <- QuizLoop.askQuestionAndGetResponse(qiv)
        output <- TestConsole.output
      } yield assert(output.map(_.trim))(equalTo(Vector(questionText)))
    },

    testM("On asking a question, an input of 'q' should lead to a Quit result") {
      val qiv = TestData.quizItemView(useMultipleChoice = false)
      for {
        _        <- TestConsole.feedLines("q")
        response <- QuizLoop.askQuestionAndGetResponse(qiv)
      } yield assert(response)(equalTo(Quit))
    },

    testM("If the user does not respond to a question, the question should be asked again") {
      val qiv = TestData.quizItemView(useMultipleChoice = false)
      val expectedQuestionText = Text.question(qiv)
      for {
        _             <- TestConsole.feedLines("", "q")
        _             <- QuizLoop.askQuestionAndGetResponse(qiv)
        outputVector  <- TestConsole.output
        question      = outputVector(0)
        questionAgain = outputVector(1)
      } yield assert(question)(containsString(expectedQuestionText)) &&
                assert(questionAgain)(containsString(expectedQuestionText))
    },

    testM("The main quiz loop should work") {
      val expectedQuestion = "what is the German word for this English word?"
      val expectedFullQuestion = "(0): solve: " + expectedQuestion
      for {
        _            <- TestConsole.feedLines("1", "q")
        _            <- QuizLoop.loop(TestData.quiz)
        outputVector <- TestConsole.output
        status       = outputVector(0).trim
        question     = outputVector(1).trim
        feedback     = outputVector(2).trim
        statusAgain  = outputVector(3).trim
        nextQuestion = outputVector(4).trim
        exitMessage  = outputVector(5).trim
      } yield assert(status.trim)(equalTo("Score: 0.0%")) &&
                assert(question)(startsWithString(expectedFullQuestion)) &&
                (assert(feedback)(startsWithString("Wrong!")) ||
                  assert(feedback)(startsWithString("Correct"))) &&
                assert(statusAgain)(startsWithString("Score: ")) &&
                assert(nextQuestion)(startsWithString("(1)")) &&
                assert(nextQuestion)(containsString(expectedQuestion)) &&
                assert(exitMessage)(containsString("Exiting..."))
    })
}
