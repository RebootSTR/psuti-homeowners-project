package ru.psuti.apache1337.homeowners.presentation.requests.adapter

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.psuti.apache1337.homeowners.presentation.requests.fragment.RequestsCreateFragment
import ru.psuti.apache1337.homeowners.presentation.requests.fragment.RequestsHistoryFragment

const val TO_CREATE_FRAGMENT_ARG_NAME = "request"

class RequestFragmentStateAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    private val bundleToCreateFragment = Bundle()

    constructor(argToCreateFragment: Parcelable?, fragmentActivity: FragmentActivity) : this(fragmentActivity) {
        if (argToCreateFragment != null) {
            bundleToCreateFragment.putParcelable(TO_CREATE_FRAGMENT_ARG_NAME, argToCreateFragment)
        }
    }

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment = when (position) {
            0 -> {
                RequestsCreateFragment().apply {
                    arguments = bundleToCreateFragment
                }
            }
            1 -> RequestsHistoryFragment()
            else -> RequestsCreateFragment()
    }

}