package eu.enhan.fptothemax

import java.util.Random

/**
 *
 */

fun main(args: Array<String>) {

    val random: Random = Random()

    println("What is your name?")
    val name = readLine()
    println("Hello $name, welcome to the game")
    var exec = true

    while (exec) {
        val num = random.nextInt(5) + 1
        println("Dear $name, please guess a number from 1 to 5:")

        val guess = readLine()?.toInt()?: 0

        if (guess == num) println("You guessed right, $name!")
        else println("You guessed wrong, $name! The number was: $num")

        println("Do you want to continue, $name?")

        when(readLine()?.toLowerCase()){
            "y" -> exec = true
            "n" -> exec = false
        }
    }

}