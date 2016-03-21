package com.orionedwards.mtglifecounter;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class DuelActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duel);

        PlayerFragment f = (PlayerFragment)getSupportFragmentManager().findFragmentById(R.id.duelP1fragment);
        f.setIsUpsideDown(true);
    }

    public void onBackButtonClicked(View v) {
        finish();
    }

    public void onD20ButtonClicked(View v) {

    }

    public void onResetButtonClicked(View v) {

    }
}
