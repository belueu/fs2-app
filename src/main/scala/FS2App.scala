package io.belueu.app

import cats.effect.{ IO, IOApp }
import fs2.{ Chunk, Pure, Stream }

object FS2App extends IOApp.Simple {


  case class Actor(id: Int, firstName: String, lastName: String)

  object Data {

    // Justice League
    val henryCavil: Actor = Actor(0, "Henry", "Cavill")
    val galGodot: Actor = Actor(1, "Gal", "Godot")
    val ezraMiller: Actor = Actor(2, "Ezra", "Miller")
    val benFisher: Actor = Actor(3, "Ben", "Fisher")
    val rayHardy: Actor = Actor(4, "Ray", "Hardy")
    val jasonMomoa: Actor = Actor(5, "Jason", "Momoa")

    // Avengers
    val scarlettJohansson: Actor = Actor(6, "Scarlett", "Johansson")
    val robertDowneyJr: Actor = Actor(7, "Robert", "Downey Jr.")
    val chrisEvans: Actor = Actor(8, "Chris", "Evans")
    val markRuffalo: Actor = Actor(9, "Mark", "Ruffalo")
    val chrisHemsworth: Actor = Actor(10, "Chris", "Hemsworth")
    val jeremyRenner: Actor = Actor(11, "Jeremy", "Renner")
    val tomHolland: Actor = Actor(13, "Tom", "Holland")
    val tobeyMaguire: Actor = Actor(14, "Tobey", "Maguire")
    val andrewGarfield: Actor = Actor(15, "Andrew", "Garfield")
  }

  // streams = abstraction to manage an unbounded amount of data
  // IO = any king of computation that might perform some side effects
  implicit class IODebugOps[A](io: IO[A]) {
    def debug: IO[A] = io.map { a =>
      println(s"${Thread.currentThread().getName} $a")
      a
    }
  }

  import Data._

  // streams
  // pure streams = store actual data
  val jlActors: Stream[Pure, Actor] = Stream(
    henryCavil,
    galGodot,
    ezraMiller,
    benFisher,
    rayHardy,
    jasonMomoa
  )

  val tomHollandStream: Stream[Pure, Actor] = Stream.emit(tomHolland)
  val spiderMen: Stream[Pure, Actor] = Stream.emits(List(tomHolland, andrewGarfield, tobeyMaguire))

  // convert a stream to a std data structure
  val jlActorList: Seq[Actor] = jlActors.toList // applicable for Stream[Pure, _]

  val infiniteJLActors: Stream[Pure, Actor] = jlActors.repeat
  val repeatedJLActorsList: Seq[Actor] = infiniteJLActors.take(10).toList

  // effectfull streams
  val savingTomHolland: Stream[IO, Actor] = Stream.eval {
    IO {
      println("Saving actor Tom Holland into the db")
      Thread.sleep(1000)
      tomHolland
    }
  }

  val compiledStream = savingTomHolland.compile


  // chunks
  val avengersActors: Stream[Pure, Actor] = Stream.chunk(Chunk.array(Array(
    scarlettJohansson,
    robertDowneyJr,
    chrisEvans,
    markRuffalo,
    chrisHemsworth,
    jeremyRenner,
    tomHolland,
    tobeyMaguire,
    andrewGarfield
  )))

  // transformations
  val allSuperHeroes = jlActors ++ avengersActors

  // flatMap
  val printedJLActors: Stream[IO, Unit] = jlActors.flatMap { actor =>
    // perform an IO[Unit] effect as a Stream
    Stream.eval(IO.println(actor))
  }
  // flatMap + eval = evalMap
  val printedJLActors_v2: Stream[IO, Unit] = jlActors.evalMap(IO.println)
  // flatMap + eval while keeping the original type = evalTap
  val printedJLActors_v3: Stream[IO, Actor] = jlActors.evalTap(IO.println)


  override def run: IO[Unit] = printedJLActors_v3.compile.toList.void

}

