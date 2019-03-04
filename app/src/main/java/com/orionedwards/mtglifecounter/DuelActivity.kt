package com.orionedwards.mtglifecounter

import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.view.View
import android.widget.RelativeLayout
import org.json.JSONException
import org.json.JSONObject

open class DuelActivity : FragmentActivity() {

    protected open val initialLifeTotal: Int
        get() = 20

    protected open val configKey: String
        get() = "duel"

    private lateinit var mPlayer1: PlayerFragment
    private lateinit var mPlayer2: PlayerFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_duel)

        mPlayer1 = supportFragmentManager.findFragmentById(R.id.p1fragment) as PlayerFragment
        mPlayer1.lifeTotal = initialLifeTotal

        mPlayer2 = supportFragmentManager.findFragmentById(R.id.p2fragment) as PlayerFragment
        mPlayer2.lifeTotal = initialLifeTotal

        val isPortrait = resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        mPlayer1.isUpsideDown = isPortrait

        try {
            val config = DataStore.getWithKey(this, configKey)

            mPlayer1.resetLifeTotal(config.getInt("player1"))
            mPlayer1.color = MtgColor.values()[config.getInt("player1color")]

            mPlayer2.resetLifeTotal(config.getInt("player2"))
            mPlayer2.color = MtgColor.values()[config.getInt("player2color")]

        } catch (unused: DataStoreException) {
        } catch (unused: JSONException) {
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        saveConfig()
    }

    override fun onDestroy() {
        super.onDestroy()
        saveConfig()
    }

    @Suppress("unused", "UNUSED_PARAMETER")
    fun onBackButtonClicked(v: View) {
        finish()
    }

    fun onD20ButtonClicked(@Suppress("UNUSED_PARAMETER") v: View) {
        val result = Util.randomUntiedDiceRolls(2, 20)

        val fontSize = 70
        val wh = Util.pxToDp(this, 120f).toInt()
        val params = RelativeLayout.LayoutParams(wh, wh)
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)

        val rootView1 = mPlayer1.rootView.guardElse { return }
        val rootView2 = mPlayer2.rootView.guardElse { return }

        val diceView1 = DiceRollView.create(this, fontSize, result[0].number, result[0].winner)
        diceView1.showInView(rootView1, params)

        val diceView2 = DiceRollView.create(this, fontSize, result[1].number, result[1].winner)
        diceView2.showInView(rootView2, params)
    }

    @Suppress("UNUSED_PARAMETER")
    fun onResetButtonClicked(v: View) {
        mPlayer1.resetLifeTotal(initialLifeTotal)
        mPlayer2.resetLifeTotal(initialLifeTotal)
    }

    private fun saveConfig() {
        try {
            val config = JSONObject()

            config.put("player1", mPlayer1.lifeTotal)
            config.put("player1color", mPlayer1.color.ordinal)

            config.put("player2", mPlayer2.lifeTotal)
            config.put("player2color", mPlayer2.color.ordinal)

            DataStore.setWithKey(this, configKey, config)

        } catch (unused: DataStoreException) {
        } catch (unused: JSONException) {
        }

    }
}
