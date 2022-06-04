package com.github.oranda.libanius.cli

import com.github.oranda.libanius.cli.PersistentData._
import com.oranda.libanius.dependencies.AppDependencyAccess
import com.oranda.libanius.dependencies.Logger
import com.oranda.libanius.model.Quiz
import com.oranda.libanius.model.quizgroup.{QuizGroup, QuizGroupHeader}
import zio.{IO, Task, UIO, ZIO, ZLayer}

import java.io.IOException

object PersistentData {
  def findAvailableQuizGroups: ZIO[DataStore.Service, Throwable, Seq[QuizGroupHeader]] =
    for
      dataStore <- ZIO.service[DataStore.Service]
      qghs <- dataStore.findAvailableQuizGroups
    yield qghs

  def loadQuizForQgHeaders(
    qgHeaders: Seq[QuizGroupHeader]
  ): ZIO[DataStore.Service, Throwable, Quiz] =
    for
      dataStore <- ZIO.service[DataStore.Service]
      quiz      <- dataStore.loadQuizForQgHeaders(qgHeaders)
    yield quiz

  def saveQuiz(quiz: Quiz): ZIO[DataStore.Service, Throwable, Quiz] =
    for
      dataStore <- ZIO.service[DataStore.Service]
      quiz      <- dataStore.saveQuiz(quiz)
    yield quiz
}