package com.mds.mapschallenge.ui.activity

import android.content.Context
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.animation.DecelerateInterpolator
import android.widget.Scroller

import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.tabs.TabLayoutMediator
import com.mds.mapschallenge.R
import com.mds.mapschallenge.adapter.ViewPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import java.lang.Exception


class MainActivity : AppCompatActivity() {

    private val adapter by lazy {ViewPagerAdapter(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        loadPager()

    }

    fun loadPager(){
        pager.adapter = adapter
        //Esta linea de codigo permite bloquear el swipe del pagerview, pero permite al mapa hacer moverse
        pager.isUserInputEnabled = false

        val tabLayoutMediator = TabLayoutMediator(tab_layout,pager,
            TabLayoutMediator.TabConfigurationStrategy{tab, position ->
                when(position){
                    0 -> tab.setIcon(R.drawable.ic_map_black_24dp)
                    1 -> tab.setIcon(R.drawable.ic_list_black_24dp)
                }
            })
        tabLayoutMediator.attach()
    }


}
