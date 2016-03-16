package com.orionedwards.mtglifecounter;

import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class DuelActivity extends FragmentActivity implements PlayerFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_duel);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void onBackButtonClicked(View v) {
        finish();
    }

    public void onD20ButtonClicked(View v) {

    }

    public void onResetButtonClicked(View v) {

    }
}
