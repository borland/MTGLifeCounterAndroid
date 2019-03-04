package com.orionedwards.mtglifecounter


import android.content.Context
import android.os.Handler
import android.util.TypedValue

inline fun <T> T?.guardElse(orElse: T?.() -> Unit): T {
    this?.let { return it }
    orElse()
    throw FatalError("guard failed; ")
}

class FatalError : RuntimeException {
    @Suppress("unused")
    constructor(string: String, cause: Throwable) : super(string, cause)
    constructor(string: String) : super(string)
}


object Util {
    @JvmStatic
    fun delay(millis: Long, callback: Runnable): Runnable {
        val handler = Handler()
        var capture = false

        handler.postDelayed(Runnable {
            if (capture) {
                return@Runnable  // canceled
            }
            callback.run()
        }, millis)

        // return a Runnable which cancels the delayed action if it hasn't already run
        return Runnable { capture = true }
    }


    @JvmStatic
    fun pxToDp(context: Context, px: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, context.resources.displayMetrics)
    }

    //! The UInt is the number rolled on the dice face, the Bool is true if this is the "winning" value
    class DiceRollResult internal constructor(var number: Int) : Comparable<DiceRollResult> {
        var winner: Boolean = false

        init {
            winner = false
        }

        override fun equals(other: Any?): Boolean {
            return if (other is DiceRollResult) {
                other.number == number
            } else super.equals(other)
        }

        override fun compareTo(other: DiceRollResult): Int {
            return number.compareTo(other.number)
        }

        override fun hashCode(): Int {
            return number.hashCode()
        }
    }

    @JvmStatic
    fun randomUntiedDiceRolls(numDice: Int, diceFaceCount: Int): Array<DiceRollResult> {
        val values = Array(size = numDice, init = { DiceRollResult(RandomGen.next(diceFaceCount) + 1) })


        // find the indexes of values that have the highest value, and replace those values with randoms. Repeat until no ties
        while (true) {
            val maxVal = maxElement(values) // we only care if the highest dice rolls are tied (e.g. if there are 3 people and the dice go 7,2,2 that's fine)
            val tiedValueIndexes = findIndexes(values, maxVal)
            if (tiedValueIndexes.size < 2) {
                break
            }

            for (ix in tiedValueIndexes) {
                values[ix] = DiceRollResult(RandomGen.next(diceFaceCount) + 1)
            }
        }
        maxElement(values)?.let{ it.winner = true}

        return values
    }

    // returns null if the collection is empty
    @JvmStatic
    fun <T : Comparable<T>> maxElement(collection: Array<T>): T? {
        var result: T? = null
        for (value in collection) {
            if (result == null || value > result) {
                result = value
            }
        }
        return result
    }

    @JvmStatic
    fun <T> findIndexes(collection: Array<T>, value: T?): List<Int> {
        val results = MutableList(size = 0, init = { 0 })
        for (i in collection.indices) {
            if (collection[i] == value) {
                results.add(i)
            }
        }
        return results
    }


}
