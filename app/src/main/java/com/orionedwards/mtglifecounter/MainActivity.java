package com.orionedwards.mtglifecounter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.util.Pair;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Type;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    final static int LAUNCH_DUEL = 1;
    final static int LAUNCH_2HG = 2;
    final static int LAUNCH_3PLAYER = 3;
    final static int ROLL_D20 = 10;

    @Nullable RelativeLayout mRootView;

    // hack to create generic arrays in java because it has rubbish generics
    @SafeVarargs
    static <T> T[] createArray(T... items) {
        return Arrays.copyOf(items, items.length);
    }

    final static Pair<String, Integer>[] sItems = createArray(
            Pair.create("- Games", 0),
            Pair.create("Duel", LAUNCH_DUEL),
            Pair.create("Two-headed Giant", LAUNCH_2HG),
            Pair.create("3 Player", LAUNCH_3PLAYER),
            Pair.create("- Utilities", 0),
            Pair.create("Roll D20", ROLL_D20));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRootView = (RelativeLayout)findViewById(R.id.main_layout);
        assert(mRootView != null);

        ListView menuList = (ListView)findViewById(R.id.main_menu_list);
        if(menuList != null) {
            menuList.setOnItemClickListener(this);
            menuList.setAdapter(new MenuListAdapter());
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent;
        switch((int)id) {
            case LAUNCH_DUEL:
                intent = new Intent(this, DuelActivity.class);
                startActivity(intent);
                break;
            case LAUNCH_2HG:
                intent = new Intent(this, TwoHeadedGiantActivity.class);
                startActivity(intent);
                break;
            case LAUNCH_3PLAYER:
                intent = new Intent(this, ThreePlayerActivity.class);
                startActivity(intent);
                break;
            case ROLL_D20:
                int wh = (int) Util.INSTANCE.pxToDp(this, 110); // equal to fontSize
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(wh, wh);
                params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

                FloatingView diceRollView = DiceRollView.INSTANCE.create(this, 60, RandomGen.INSTANCE.next(20) + 1, false);
                diceRollView.showInView(mRootView, params, 1000, 1300, FloatingView.Companion.getDEFAULT_FADE_MILLIS());
                break;
            default:
                break;
        }
    }

    class MenuListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return sItems.length;
        }

        @Override
        public Object getItem(int position) {
            return sItems[position];
        }

        @Override
        public long getItemId(int position) {
            return sItems[position].second;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater)MainActivity.this.getApplicationContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            String item = sItems[position].first;

            View rowView;

            if(item.startsWith("- ")) { // header view
                item = item.substring(2);
                rowView = inflater.inflate(R.layout.menu_header, parent, false);
            } else {
                rowView = inflater.inflate(R.layout.menu_row, parent, false);
            }

            rowView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT));

            TextView textView = (TextView) rowView.findViewById(R.id.text_view);
            textView.setText(item);
            return rowView;
        }

        @Override
        public boolean isEnabled(int position) {
            String item = sItems[position].first;
            return !item.startsWith("- "); // disabled for headers
        }
    }
}
