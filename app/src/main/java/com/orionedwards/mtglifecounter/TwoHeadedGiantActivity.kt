package com.orionedwards.mtglifecounter

class TwoHeadedGiantActivity : DuelActivity() {
    override val initialLifeTotal: Int
        get() = 30

    override val configKey: String
        get() = "2hg"
}
