package eu.enhan.fptothemax

import arrow.Kind
import arrow.core.Option
import arrow.core.Try
import arrow.effects.IO
import arrow.effects.fix
import arrow.effects.monadDefer
import arrow.effects.typeclasses.MonadDefer
import arrow.typeclasses.Monad
import arrow.typeclasses.binding
import java.util.*

object ORandom : Random()

interface Console<F> {
  fun putStrLn(s: String): Kind<F, Unit>
  fun getStrLn(): Kind<F, String>
}

class ConsoleInstance<F>(val delay: MonadDefer<F>) : Console<F> {
  override fun putStrLn(s: String): Kind<F, Unit> = delay { println(s) }
  override fun getStrLn(): Kind<F, String> = delay { readLine().orEmpty() }
}

interface FRandom<F> {
  fun nextInt(upper: Int): Kind<F, Int>
}

class FRandomInstance<F>(val delay: MonadDefer<F>) : FRandom<F> {
  override fun nextInt(upper: Int): Kind<F, Int> = delay { ORandom.nextInt(upper) }
}

class MonadAndConsoleRandom<F>(M: Monad<F>, C: Console<F>, R: FRandom<F>) : Monad<F> by M, Console<F> by C, FRandom<F> by R

object Step1 {

  fun parseInt(s: String): Option<Int> = Try { s.toInt() }.toOption()

  fun <F> MonadAndConsoleRandom<F>.checkContinue(name: String): Kind<F, Boolean> = binding {
    putStrLn("Do you want to continue, $name?").bind()
    val input = getStrLn().map { it.toLowerCase() }.bind()
    when (input) {
      "y" -> just(true)
      "n" -> just(false)
      else -> checkContinue(name)
    }.bind()
  }

  fun <F> MonadAndConsoleRandom<F>.gameLoop(name: String): Kind<F, Unit> = binding {
    val num = nextInt(5).map { it + 1 }.bind()
    putStrLn("Dear $name, please guess a number from 1 to 5:").bind()
    val input = getStrLn().bind()
    parseInt(input).fold({ putStrLn("You did not enter a number") }) { guess ->
      if (guess == num) putStrLn("You guessed right, $name!")
      else putStrLn("You guessed wrong, $name! The number was: $num")
    }.bind()
    val cont = checkContinue(name).bind()
    (if (cont) gameLoop(name) else just(Unit)).bind()
  }

  fun <F> MonadAndConsoleRandom<F>.fMain(): Kind<F, Unit> = binding {
    putStrLn("What is your name?").bind()
    val name = getStrLn().bind()
    putStrLn("Hello $name, welcome to the game").bind()
    gameLoop(name).bind()
  }

  @JvmStatic
  fun main(args: Array<String>) {
    val module = IO.monadDefer().run {
      MonadAndConsoleRandom(this, ConsoleInstance(this), FRandomInstance(this))
    }
    val r = module.fMain()
    r.fix().unsafeRunSync()
  }

}
