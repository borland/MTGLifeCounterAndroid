package com.orionedwards.mtglifecounter;

import java.util.Random;

public class RandomGen {
    static final Random sRNG = new Random(12);

    public static int next(int exclusiveUpperBound) {
        return sRNG.nextInt(exclusiveUpperBound);
    }
}
