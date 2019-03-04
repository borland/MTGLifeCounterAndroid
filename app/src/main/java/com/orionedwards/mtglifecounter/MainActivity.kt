package com.orionedwards.mtglifecounter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.menu_row.view.*

class MainActivity : AppCompatActivity(), AdapterView.OnItemClickListener {

    private lateinit var mRootView: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mRootView = main_layout

        val menuList: ListView = main_menu_list
        menuList.onItemClickListener = this
        menuList.adapter = MenuListAdapter()
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val intent: Intent
        when (id) {
            LAUNCH_DUEL -> {
                intent = Intent(this, DuelActivity::class.java)
                startActivity(intent)
            }
            LAUNCH_2HG -> {
                intent = Intent(this, TwoHeadedGiantActivity::class.java)
                startActivity(intent)
            }
            LAUNCH_3PLAYER -> {
                intent = Intent(this, ThreePlayerActivity::class.java)
                startActivity(intent)
            }
            ROLL_D20 -> {
                val wh = Util.pxToDp(this, 110f).toInt() // equal to fontSize
                val params = RelativeLayout.LayoutParams(wh, wh)
                params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)

                val diceRollView = DiceRollView.create(this, 60, RandomGen.next(20) + 1, false)
                diceRollView.showInView(mRootView, params, 1000, 1300)
            }
            else -> {
            }
        }
    }

    internal inner class MenuListAdapter : BaseAdapter() {
        override fun getCount(): Int {
            return sItems.size
        }

        override fun getItem(position: Int): Any {
            return sItems[position]
        }

        override fun getItemId(position: Int): Long {
            return sItems[position].second
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val inflater = this@MainActivity.applicationContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            var item = sItems[position].first

            val rowView: View

            if (item.startsWith("- ")) { // header view
                item = item.substring(2)
                rowView = inflater.inflate(R.layout.menu_header, parent, false)
            } else {
                rowView = inflater.inflate(R.layout.menu_row, parent, false)
            }

            rowView.layoutParams = AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT)

            rowView.text_view.text = item
            return rowView
        }

        override fun isEnabled(position: Int): Boolean {
            val item = sItems[position].first
            return !item.startsWith("- ") // disabled for headers
        }
    }

    companion object {
        internal const val LAUNCH_DUEL = 1L
        internal const val LAUNCH_2HG = 2L
        internal const val LAUNCH_3PLAYER = 3L
        internal const val ROLL_D20 = 10L

        internal val sItems : Array<Pair<String, Long>> = arrayOf(
                Pair("- Games", 0L),
                Pair("Duel", LAUNCH_DUEL),
                Pair("Two-headed Giant", LAUNCH_2HG),
                Pair("3 Player", LAUNCH_3PLAYER),
                Pair("- Utilities", 0L),
                Pair("Roll D20", ROLL_D20))
    }
}
