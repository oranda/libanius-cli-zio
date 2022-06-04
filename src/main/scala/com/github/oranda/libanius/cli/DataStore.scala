package com.github.oranda.libanius.cli

import com.github.oranda.libanius.cli.DataStore._
import com.oranda.libanius.dependencies._
import com.oranda.libanius.model.Quiz
import com.oranda.libanius.model.quizgroup.{QuizGroup, QuizGroupHeader}
import zio.{IO, Task, UIO, ZIO, ZLayer}

import java.io.IOException

object DataStore {
  trait Service {
    def findAvailableQuizGroups: ZIO[DataStore.Service, Throwable, Seq[QuizGroupHeader]]
    def loadQuizForQgHeaders(qgHeaders: Seq[QuizGroupHeader]): ZIO[DataStore.Service, Throwable, Quiz]
    def saveQuiz(quiz: Quiz): Task[Quiz]
  }

  case class DataStoreImpl(dataStore: DataStore, l: Logger) extends DataStore.Service {
    def findAvailableQuizGroups: ZIO[DataStore.Service, Throwable, Seq[QuizGroupHeader]] =
      Task.attempt(dataStore.findAvailableQuizGroups).either.flatMap {
        case Left(e) =>
          l.logError("Could not find available quiz groups", e)
          UIO.succeed(Seq())
        case Right(quizGroups) =>
          UIO.succeed(quizGroups.toSeq.sortBy(_.quizGroupType.str))
      }

    def loadQuizForQgHeaders(
      qgHeaders: Seq[QuizGroupHeader]
    ): ZIO[DataStore.Service, Throwable, Quiz] = {
      val quizGroups: Map[QuizGroupHeader, QuizGroup] =
        qgHeaders.map(header => (header, dataStore.loadQuizGroupCore(header))).toMap
      IO.attempt(Quiz(quizGroups)).refineToOrDie[IOException]
    }

    def saveQuiz(quiz: Quiz): Task[Quiz] =
      Task.attempt(dataStore.saveQuiz(quiz))
  }
}

object DataStoreLive extends AppDependencyAccess {
  val layer: ZLayer[Any, Throwable, DataStore.Service] = ZLayer {
    ZIO.succeed(DataStoreImpl(dataStore, l))
  }
}

