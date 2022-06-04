package com.github.oranda.libanius.cli

import com.oranda.libanius.dependencies.DataStore
import zio._
import zio.test._
import zio.test.Assertion.equalTo

object PersistentDataSpec extends ZIOSpecDefault {
  override def spec = suite("Persistent Data")(
    test("Find available quiz groups") {
      assertM(
        for
          quizGroups <- PersistentData.findAvailableQuizGroups
        yield quizGroups
      )(equalTo(TestData.qghs)).provideEnvironment(ZEnvironment(DataStoreStub()))
    },
    test("A quiz can be loaded from supplied quiz group headers") {
      assertM(
        for
          quizGroups <- PersistentData.loadQuizForQgHeaders(Seq(TestData.qgh0))
        yield quizGroups
      )(equalTo(TestData.quizOneGroup)).provideEnvironment(ZEnvironment(DataStoreStub()))
    },
    test("A quiz can be saved") {
      assertM(
        for
          quizGroups <- PersistentData.saveQuiz(TestData.quizOneGroup)
        yield quizGroups
      )(equalTo(TestData.quizOneGroup)).provideEnvironment(ZEnvironment(DataStoreStub()))
    }
  )
}