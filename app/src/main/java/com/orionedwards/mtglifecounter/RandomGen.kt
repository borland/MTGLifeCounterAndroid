package com.orionedwards.mtglifecounter

import java.util.Random

object RandomGen {
    private val sRNG = Random(12)

    @JvmStatic
    fun next(exclusiveUpperBound: Int): Int {
        return sRNG.nextInt(exclusiveUpperBound)
    }
}
