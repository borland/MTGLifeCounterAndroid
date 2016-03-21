package com.orionedwards.mtglifecounter;

import android.graphics.Color;
import android.support.annotation.ColorInt;

public enum MtgColor {
    White, Blue, Black, Red, Green, // basic
    WhiteBlue, BlueBlack, BlackRed, RedGreen, GreenWhite, // allied
    WhiteBlack, BlueRed, BlackGreen, RedWhite, GreenBlue; // enemy

    public String getDisplayName() {
        switch(this) {
            case White: return "White";
            case Blue: return "Blue";
            case Black: return "Black";
            case Red: return "Red";
            case Green: return "Green";
            case WhiteBlue: return "Azorius";
            case BlueBlack: return "Dimir";
            case BlackRed: return "Rakdos";
            case RedGreen: return "Gruul";
            case GreenWhite: return "Selesnya";
            case WhiteBlack: return "Orzhov";
            case BlueRed: return "Izzet";
            case BlackGreen: return "Golgari";
            case RedWhite: return "Boros";
            case GreenBlue: return "Simic";
            default: return "";
        }
    }

    public static MtgColor first() {
        return White;
    }

    public static MtgColor last() {
        return GreenBlue;
    }

    @ColorInt
    public int lookupColor(boolean primary) {
        if(primary) {
            switch(this) {
                case White:
                case WhiteBlue:
                case WhiteBlack:
                    return Color.argb(1, 1, 1, 1);
                case Blue:
                case BlueBlack:
                case BlueRed:
                    return Color.argb(1, 0, 56, 184);
                case Black:
                case BlackRed:
                case BlackGreen:
                    return Color.argb(1, 31, 48, 64);
                case Red:
                case RedGreen:
                case RedWhite:
                    return Color.argb(1, 214, 10, 18);
                case Green:
                case GreenWhite:
                case GreenBlue:
                    return Color.argb(1, 64, 173, 69);
            }
        } else {
            switch(this) {
                case White:
                case GreenWhite:
                case RedWhite:
                    return Color.argb(1, 247, 235, 230);
                case Blue:
                case WhiteBlue:
                case GreenBlue:
                    return Color.argb(1, 51, 77, 255);
                case Black:
                case BlueBlack:
                case WhiteBlack:
                    return Color.argb(1, 0, 0, 0);
                case Red:
                case BlackRed:
                case BlueRed:
                    return Color.argb(1, 214, 10, 18);
                case Green:
                case RedGreen:
                case BlackGreen:
                    return Color.argb(1, 199, 36, 10);
            }
        }
        return Color.argb(1,0,0,0);
    }
}