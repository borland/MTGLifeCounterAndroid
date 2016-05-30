package com.orionedwards.mtglifecounter;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.TypedValue;

import java.util.ArrayList;
import java.util.List;

public class Util {
    public static Runnable delay(long millis, @NonNull final Runnable callback) {
        final Handler handler = new Handler();
        final boolean[] capture = { false };

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(capture[0]) {
                    return; // canceled
                }
                callback.run();
            }
        }, (long) millis);

        // return a Runnable which cancels the delayed action if it hasn't already run
        return new Runnable() {
            @Override
            public void run() {
                capture[0] = true;
            }
        };
    }


    public static float pxToDp(@NonNull Context context, float px) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, context.getResources().getDisplayMetrics());
    }

    //! The UInt is the number rolled on the dice face, the Bool is true if this is the "winning" value
    public static class DiceRollResult implements Comparable<DiceRollResult> {
        public int number;
        public boolean winner;

        DiceRollResult(int number) {
            this.number = number;
            winner = false;
        }

        @Override
        public boolean equals(Object o) {
            if(o instanceof DiceRollResult) {
                return ((DiceRollResult)o).number == number;
            }
            return super.equals(o);
        }

        @Override
        public int compareTo(@NonNull DiceRollResult diceRollResult) {
            return ((Integer)number).compareTo(diceRollResult.number);
        }
    }

    public static DiceRollResult[] randomUntiedDiceRolls(int numDice, int diceFaceCount) {
        DiceRollResult[] values = new DiceRollResult[numDice];

        // find the indexes of values that have the highest value, and replace those values with randoms. Repeat until no ties
        while(true) {
            DiceRollResult maxVal = maxElement(values); // we only care if the highest dice rolls are tied (e.g. if there are 3 people and the dice go 7,2,2 that's fine)
            List<Integer> tiedValueIndexes = findIndexes(values, maxVal);
            if (tiedValueIndexes.size() < 2) {
                break;
            }

            for(int ix : tiedValueIndexes) {
                values[ix] = new DiceRollResult(RandomGen.next(diceFaceCount) + 1);
            }
        }
        DiceRollResult maxVal = maxElement(values);
        maxVal.winner = true; // it's a ref type so just go assign it. In swift it's a value type so need to map the array
        return values;
    }

    // returns null if the collection is empty
    public static <T extends Comparable<T>> T maxElement(@NonNull T[] collection) {
        T result = null;
        for (T value: collection) {
            if(result == null || value.compareTo(result) > 0) {
                result = value;
            }
        }
        return result;
    }

    public static <T> List<Integer> findIndexes(T[] collection, T value) {
        ArrayList<Integer> results = new ArrayList<>();
        for(int i = 0; i < collection.length; i++) {
            if(collection[i].equals(value)) {
                results.add(i);
            }
        }
        return results;
    }



}
