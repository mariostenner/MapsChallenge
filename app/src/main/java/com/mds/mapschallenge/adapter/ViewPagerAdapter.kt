package com.mds.mapschallenge.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mds.mapschallenge.ui.fragment.MapFragment
import com.mds.mapschallenge.ui.fragment.RecordListFragment

class ViewPagerAdapter(fragment: FragmentActivity) : FragmentStateAdapter(fragment) {

    companion object{
        private const val ARG_OBJECT = "object"
    }

    //Agrego el numero de tabs que tendrá
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {

         var fragment = Fragment()
            when (position){
                0 -> fragment = MapFragment()
                1 -> fragment = RecordListFragment()
            }


        fragment.arguments = Bundle().apply {
            putInt(ARG_OBJECT, position+1)
        }
        return fragment
    }

}