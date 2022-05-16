package com.github.oranda.libanius.cli

import com.oranda.libanius.dependencies.*
import com.oranda.libanius.model.Quiz
import com.oranda.libanius.model.quizgroup.{QuizGroup, QuizGroupHeader}
import zio.{IO, Task, UIO, ZIO, ZLayer}

import java.io.IOException

trait PersistentData {
  def findAvailableQuizGroups: ZIO[PersistentData, Throwable, Seq[QuizGroupHeader]]
  def loadQuizForQgHeaders(qgHeaders: Seq[QuizGroupHeader]): ZIO[PersistentData, Throwable, Quiz]
  def saveQuiz(quiz: Quiz): Task[Quiz]
}

object PersistentData extends AppDependencyAccess {
  case class PersistentDataImpl(dataStore: DataStore, l: Logger) extends PersistentData {
    def findAvailableQuizGroups: ZIO[PersistentData, Throwable, Seq[QuizGroupHeader]] =
      Task.attempt(dataStore.findAvailableQuizGroups).either.flatMap {
        case Left(e) =>
          l.logError("Could not find available quiz groups", e)
          UIO.succeed(Seq())
        case Right(quizGroups) =>
          UIO.succeed(quizGroups.toSeq.sortBy(_.quizGroupType.str))
      }

    def loadQuizForQgHeaders(
      qgHeaders: Seq[QuizGroupHeader]
    ): ZIO[PersistentData, Throwable, Quiz] = {
      val quizGroups: Map[QuizGroupHeader, QuizGroup] =
        qgHeaders.map(header => (header, dataStore.loadQuizGroupCore(header))).toMap
      IO.attempt(Quiz(quizGroups)).refineToOrDie[IOException]
    }

    def saveQuiz(quiz: Quiz): Task[Quiz] =
      Task.attempt(dataStore.saveQuiz(quiz))
  }

  val live: ZLayer[Any, Throwable, PersistentData] = ZLayer {
    ZIO.succeed(PersistentDataImpl(dataStore, l))
  }
}
