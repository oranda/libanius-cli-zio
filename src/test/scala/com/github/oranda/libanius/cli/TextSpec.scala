package com.github.oranda.libanius.cli

import zio.test.Assertion.{equalTo, containsString, startsWithString}
import zio.test.{DefaultRunnableSpec, assert, suite, test}

object TextSpec extends DefaultRunnableSpec {
  def spec = suite("Text")(
    test("Text can be formed for the quiz intro") {
      val expectedText = s"""
        |Selected quiz groups:
        |WordMapping: English word-German word
        |
        |Number of quiz items: 1
        |
        |OK, the quiz begins! To quit, type q at any time.|""".stripMargin

      val text = Text.quizIntro(TestData.quiz)
      assert(text)(equalTo(expectedText))
    },

    test("Text can be formed for the quiz group choices") {
      val expectedText = s"""1. WordMapping: English word-German word
        |2. WordMapping: German word-English word
        |3. WordMapping: English word-Spanish word""".stripMargin

      val text = Text.quizGroupChoices(choices = TestData.qghs)
      assert(text)(equalTo(expectedText))
    },

    test("Text can be formed for a question for a quiz item") {
      // The choices presented are in random order, so only test the start for now
      val expectedStartText = s"""(0): solve: what is the German word for this English word?
        |
        |1.""".stripMargin

      val text = Text.question(TestData.quizItemView(true))
      assert(text)(startsWithString(expectedStartText))
      assert(text)(containsString("2."))
      assert(text)(containsString("3."))
    },

    test("Text can be formed for a message when the quiz is completed") {
      val expectedText = s"""
        |No more questions found! Congratulations on completing the quiz!
        |
        |If you felt Libanius helped you learn this subject, and want to learn
        |something else, consider making your own quiz file. It's easy! See the
        |README for details.
        |""".stripMargin

      val text = Text.quizCompleted
      assert(text)(equalTo(expectedText))
    })
}