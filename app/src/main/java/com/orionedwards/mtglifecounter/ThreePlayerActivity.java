package com.orionedwards.mtglifecounter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import org.json.JSONException;
import org.json.JSONObject;

public class ThreePlayerActivity extends AppCompatActivity {
    protected int getInitialLifeTotal() { return 20; }
    protected String getConfigKey() { return "3player"; }

    protected PlayerFragment mPlayer1;
    protected PlayerFragment mPlayer2;
    protected PlayerFragment mPlayer3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_three_player);

        mPlayer1 = (PlayerFragment)getSupportFragmentManager().findFragmentById(R.id.p1fragment);
        mPlayer1.setLifeTotal(getInitialLifeTotal());

        mPlayer2 = (PlayerFragment)getSupportFragmentManager().findFragmentById(R.id.p2fragment);
        mPlayer2.setLifeTotal(getInitialLifeTotal());

        mPlayer3 = (PlayerFragment)getSupportFragmentManager().findFragmentById(R.id.p3fragment);
        mPlayer3.setLifeTotal(getInitialLifeTotal());

        try {
            JSONObject config = DataStore.INSTANCE.getWithKey(this, getConfigKey());

            mPlayer1.resetLifeTotal(config.getInt("player1"));
            mPlayer1.setColor(MtgColor.values()[config.getInt("player1color")]);

            mPlayer2.resetLifeTotal(config.getInt("player2"));
            mPlayer2.setColor(MtgColor.values()[config.getInt("player2color")]);

            mPlayer3.resetLifeTotal(config.getInt("player3"));
            mPlayer3.setColor(MtgColor.values()[config.getInt("player3color")]);

        } catch(DataStoreException | JSONException unused){ }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveConfig();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveConfig();
    }

    public void onBackButtonClicked(View v) {
        finish();
    }

    public void onD20ButtonClicked(View v) {
        Util.DiceRollResult[] result = Util.INSTANCE.randomUntiedDiceRolls(3, 20);

        int fontSize = 60;
        int wh = (int) Util.INSTANCE.pxToDp(this, 105);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(wh, wh);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        FloatingView diceView1 = DiceRollView.INSTANCE.create(this, fontSize, result[0].getNumber(), result[0].getWinner());
        diceView1.showInView(mPlayer1.getRootView(), params);

        FloatingView diceView2 = DiceRollView.INSTANCE.create(this, fontSize, result[1].getNumber(), result[1].getWinner());
        diceView2.showInView(mPlayer2.getRootView(), params);

        FloatingView diceView3 = DiceRollView.INSTANCE.create(this, fontSize, result[2].getNumber(), result[2].getWinner());
        diceView3.showInView(mPlayer3.getRootView(), params);
    }

    public void onResetButtonClicked(View v) {
        mPlayer1.resetLifeTotal(getInitialLifeTotal());
        mPlayer2.resetLifeTotal(getInitialLifeTotal());
        mPlayer3.resetLifeTotal(getInitialLifeTotal());
    }

    private void saveConfig() {
        try {
            JSONObject config = new JSONObject();

            config.put("player1", mPlayer1.getLifeTotal());
            config.put("player1color", mPlayer1.getColor().ordinal());

            config.put("player2", mPlayer2.getLifeTotal());
            config.put("player2color", mPlayer2.getColor().ordinal());

            config.put("player3", mPlayer3.getLifeTotal());
            config.put("player3color", mPlayer3.getColor().ordinal());

            DataStore.INSTANCE.setWithKey(this, getConfigKey(), config);

        } catch(DataStoreException | JSONException unused){ }
    }
}
