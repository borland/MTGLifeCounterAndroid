package com.orionedwards.mtglifecounter;

import android.os.Handler;
import android.support.annotation.NonNull;

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
}
