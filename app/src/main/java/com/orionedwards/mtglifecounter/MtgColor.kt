package com.orionedwards.mtglifecounter

import android.graphics.Color
import android.support.annotation.ColorInt

enum class MtgColor {
    White, Blue, Black, Red, Green, // basic
    WhiteBlue, BlueBlack, BlackRed, RedGreen, GreenWhite, // allied
    WhiteBlack, BlueRed, BlackGreen, RedWhite, GreenBlue;

    @Suppress("unused")
    val displayName: String
        get() {
            when (this) {
                White -> return "White"
                Blue -> return "Blue"
                Black -> return "Black"
                Red -> return "Red"
                Green -> return "Green"
                WhiteBlue -> return "Azorius"
                BlueBlack -> return "Dimir"
                BlackRed -> return "Rakdos"
                RedGreen -> return "Gruul"
                GreenWhite -> return "Selesnya"
                WhiteBlack -> return "Orzhov"
                BlueRed -> return "Izzet"
                BlackGreen -> return "Golgari"
                RedWhite -> return "Boros"
                GreenBlue -> return "Simic"
                else -> return ""
            }
        }

    @ColorInt
    fun lookupColor(primary: Boolean): Int {
        return if (primary) {
            when (this) {
                White, WhiteBlue, WhiteBlack -> Color.argb(255, 255, 255, 255)
                Blue, BlueBlack, BlueRed -> Color.argb(255, 0, 56, 184)
                Black, BlackRed, BlackGreen -> Color.argb(255, 31, 48, 64)
                Red, RedGreen, RedWhite -> Color.argb(255, 214, 10, 18)
                Green, GreenWhite, GreenBlue -> Color.argb(255, 64, 173, 69)
            }
        } else {
            when (this) {
                White, GreenWhite, RedWhite -> Color.argb(255, 247, 235, 230)
                Blue, WhiteBlue, GreenBlue -> Color.argb(255, 51, 77, 255)
                Black, BlueBlack, WhiteBlack -> Color.argb(255, 0, 0, 0)
                Red, BlackRed, BlueRed -> Color.argb(255, 214, 10, 18)
                Green, RedGreen, BlackGreen -> Color.argb(255, 199, 36, 10)
            }
        }
    }

    companion object {
        const val PRIMARY = true
        const val SECONDARY = false
    }
}