package com.orionedwards.mtglifecounter

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout

import org.json.JSONException
import org.json.JSONObject

class ThreePlayerActivity : AppCompatActivity() {
    private val initialLifeTotal: Int
        get() = 20
    private val configKey: String
        get() = "3player"

    private lateinit var mPlayer1: PlayerFragment
    private lateinit var mPlayer2: PlayerFragment
    private lateinit var mPlayer3: PlayerFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_three_player)

        mPlayer1 = supportFragmentManager.findFragmentById(R.id.p1fragment) as PlayerFragment
        mPlayer1.lifeTotal = initialLifeTotal

        mPlayer2 = supportFragmentManager.findFragmentById(R.id.p2fragment) as PlayerFragment
        mPlayer2.lifeTotal = initialLifeTotal

        mPlayer3 = supportFragmentManager.findFragmentById(R.id.p3fragment) as PlayerFragment
        mPlayer3.lifeTotal = initialLifeTotal

        try {
            val config = DataStore.getWithKey(this, configKey)

            mPlayer1.resetLifeTotal(config.getInt("player1"))
            mPlayer1.color = MtgColor.values()[config.getInt("player1color")]

            mPlayer2.resetLifeTotal(config.getInt("player2"))
            mPlayer2.color = MtgColor.values()[config.getInt("player2color")]

            mPlayer3.resetLifeTotal(config.getInt("player3"))
            mPlayer3.color = MtgColor.values()[config.getInt("player3color")]

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
        val result = Util.randomUntiedDiceRolls(3, 20)

        val fontSize = 60
        val wh = Util.pxToDp(this, 105f).toInt()
        val params = RelativeLayout.LayoutParams(wh, wh)
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)

        val rootView1 = mPlayer1.rootView.guardElse { return }
        val rootView2 = mPlayer2.rootView.guardElse { return }
        val rootView3 = mPlayer3.rootView.guardElse { return }

        val diceView1 = DiceRollView.create(this, fontSize, result[0].number, result[0].winner)
        diceView1.showInView(rootView1, params)

        val diceView2 = DiceRollView.create(this, fontSize, result[1].number, result[1].winner)
        diceView2.showInView(rootView2, params)

        val diceView3 = DiceRollView.create(this, fontSize, result[2].number, result[2].winner)
        diceView3.showInView(rootView3, params)
    }

    fun onResetButtonClicked(@Suppress("UNUSED_PARAMETER") v: View) {
        mPlayer1.resetLifeTotal(initialLifeTotal)
        mPlayer2.resetLifeTotal(initialLifeTotal)
        mPlayer3.resetLifeTotal(initialLifeTotal)
    }

    private fun saveConfig() {
        try {
            val config = JSONObject()

            config.put("player1", mPlayer1.lifeTotal)
            config.put("player1color", mPlayer1.color.ordinal)

            config.put("player2", mPlayer2.lifeTotal)
            config.put("player2color", mPlayer2.color.ordinal)

            config.put("player3", mPlayer3.lifeTotal)
            config.put("player3color", mPlayer3.color.ordinal)

            DataStore.setWithKey(this, configKey, config)

        } catch (unused: DataStoreException) {
        } catch (unused: JSONException) {
        }

    }
}
