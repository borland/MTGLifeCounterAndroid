package com.orionedwards.mtglifecounter;

public class TwoHeadedGiantActivity extends DuelActivity {
    @Override
    protected int getInitialLifeTotal() {
        return 30;
    }

    @Override
    protected String getConfigKey() {
        return "2hg";
    }
}
