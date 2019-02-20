package com.orionedwards.mtglifecounter

import java.util.Random

object RandomGen {
    internal val sRNG = Random(12)

    @JvmStatic
    fun next(exclusiveUpperBound: Int): Int {
        return sRNG.nextInt(exclusiveUpperBound)
    }
}
