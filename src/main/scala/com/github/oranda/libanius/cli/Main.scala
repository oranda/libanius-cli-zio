package com.github.oranda.libanius.cli

import zio.*

object Main extends ZIOAppDefault {
  override val layer: ZLayer[Any, Throwable, PersistentData] =
    ZLayer.make[PersistentData](PersistentData.live)

  override val run: ZIO[Any, Throwable, Unit] = QuizCLI.runCLI.provide(layer)
}
