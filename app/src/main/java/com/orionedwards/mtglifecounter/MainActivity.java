package com.orionedwards.mtglifecounter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView menuList = (ListView)findViewById(R.id.main_menu_list);
        menuList.setAdapter(new MenuListAdapter());
    }

    class MenuListAdapter extends BaseAdapter {
        final String[] mItems = new String[]{
                "- Games",
                "Duel",
                "Two-headed Giant",
                "3 Player",
                "- Utilities",
                "Roll D20"
        };

        @Override
        public int getCount() {
            return mItems.length;
        }

        @Override
        public Object getItem(int position) {
            return mItems[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater)MainActivity.this.getApplicationContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            String item = mItems[position];

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
            String item = mItems[position];
            return !item.startsWith("- "); // disabled for headers
        }
    }
}
