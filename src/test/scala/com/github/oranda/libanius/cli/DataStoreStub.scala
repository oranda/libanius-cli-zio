package com.github.oranda.libanius.cli

import com.oranda.libanius.model.Quiz
import com.oranda.libanius.model.quizgroup.{QuizGroup, QuizGroupHeader}
import zio.*

class DataStoreStub extends DataStore.Service {
  def findAvailableQuizGroups: ZIO[DataStore.Service, Throwable, Seq[QuizGroupHeader]] =
    Task.attempt(TestData.qghs)

  def loadQuizForQgHeaders(
    qgHeaders: Seq[QuizGroupHeader]
  ): ZIO[DataStore.Service, Throwable, Quiz] =
    if qgHeaders == Seq(TestData.qgh0) then IO.attempt(TestData.quizOneGroup)
    else throw new RuntimeException(s"Unknown test input: $qgHeaders")

  def saveQuiz(quiz: Quiz): Task[Quiz] =
    Task.attempt(quiz)
}
