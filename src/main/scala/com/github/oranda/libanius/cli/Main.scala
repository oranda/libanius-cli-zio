package com.github.oranda.libanius.cli

import zio.*


object Main extends ZIOAppDefault {
  override val layer: ZLayer[Any, Throwable, DataStore.Service] =
    ZLayer.make[DataStore.Service](DataStoreLive.layer)

  override val run: ZIO[Any, Throwable, Unit] = QuizCLI.runCLI.provide(layer)
}
