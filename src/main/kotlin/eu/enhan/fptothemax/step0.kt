package eu.enhan.fptothemax

import arrow.core.Option
import arrow.core.Try
import arrow.effects.IO
import arrow.effects.fix
import arrow.effects.monad
import arrow.typeclasses.binding
import java.util.Random

object Step0 {
    fun putStrLn(s: String): IO<Unit> = IO { println(s) }
    fun getStrLn(): IO<String> = IO { readLine().orEmpty() }

    fun parseInt(s: String): Option<Int> = Try { s.toInt() }.toOption()

    object LRandom : Random()

    fun nextInt(upper: Int): IO<Int> = IO {
        LRandom.nextInt(upper)
    }

    fun checkContinue(name: String): IO<Boolean> = IO.monad().binding {
        putStrLn("Do you want to continue, $name?").bind()
        val input = getStrLn().map { it.toLowerCase() }.bind()
        when (input) {
            "y" -> IO.just(true)
            "n" -> IO.just(false)
            else -> checkContinue(name)
        }.bind()
    }.fix()

    fun gameLoop(name: String): IO<Unit> = IO.monad().binding {
        val num = nextInt(5).map { it + 1 }.bind()
        putStrLn("Dear $name, please guess a number from 1 to 5:").bind()
        val input = getStrLn().bind()
        parseInt(input).fold({ putStrLn("You did not enter a number")}){ guess ->
            if (guess == num) putStrLn("You guessed right, $name!")
            else putStrLn("You guessed wrong, $name! The number was: $num")
        }.bind()
        val cont = checkContinue(name).bind()
        (if (cont) gameLoop(name) else IO.unit).bind()
    }.fix()

    fun ioMain(): IO<Unit> = IO.monad().binding {
        putStrLn("What is your name?").bind()
        val name = getStrLn().bind()
        putStrLn("Hello $name, welcome to the game").bind()
        gameLoop(name).bind()
    }.fix()


    @JvmStatic
    fun main(args: Array<String>) {
        ioMain().unsafeRunSync()
    }

}
