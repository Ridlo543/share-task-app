package com.l0122138.ridlo.sharetaskapp.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.l0122138.ridlo.sharetaskapp.ui.playground.CalculatorFragment
import com.l0122138.ridlo.sharetaskapp.ui.playground.TimerFragment

class PlaygroundPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> TimerFragment()
            1 -> CalculatorFragment()
            else -> TimerFragment() // Default
        }
    }
}