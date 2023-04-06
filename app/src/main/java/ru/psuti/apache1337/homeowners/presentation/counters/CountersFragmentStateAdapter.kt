package ru.psuti.apache1337.homeowners.presentation.counters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.psuti.apache1337.homeowners.presentation.counters.pages.apply.CountersApplyFragment
import ru.psuti.apache1337.homeowners.presentation.counters.pages.history.CountersHistoryFragment


class CountersFragmentStateAdapter(fragmentActivity: FragmentActivity, private val demo: Boolean) :
    FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> CountersApplyFragment(demo)
        1 -> CountersHistoryFragment(demo)
        else -> CountersApplyFragment(demo)
    }

}