package com.orionedwards.mtglifecounter;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

public class DuelActivity extends FragmentActivity {

    protected int getInitialLifeTotal() { return 20; }
    protected String getConfigKey() { return "duel"; }

    protected PlayerFragment mPlayer1;
    protected PlayerFragment mPlayer2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duel);

        mPlayer1 = (PlayerFragment)getSupportFragmentManager().findFragmentById(R.id.duelP1fragment);
        mPlayer1.setIsUpsideDown(true);
        mPlayer1.setLifeTotal(getInitialLifeTotal());

        mPlayer2 = (PlayerFragment)getSupportFragmentManager().findFragmentById(R.id.duelP2fragment);
        mPlayer2.setLifeTotal(getInitialLifeTotal());

        try {
            JSONObject config = DataStore.getWithKey(this, getConfigKey());

            config.getInt("player1");

        } catch(DataStoreException | JSONException _){ }
    }

    public void onBackButtonClicked(View v) {
        finish();
    }

    public void onD20ButtonClicked(View v) {

    }

    public void onResetButtonClicked(View v) {

    }
}
