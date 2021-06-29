package com.kontrakanprojects.appdelivery.view.courier.barang.adapter

import android.content.Context
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.kontrakanprojects.appdelivery.R
import com.kontrakanprojects.appdelivery.view.courier.barang.first.FirstProsesFragment
import com.kontrakanprojects.appdelivery.view.courier.barang.second.SecondDoneFragment

class ViewPagerAdapter(private val mContext: Context, fm: FragmentManager): FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return if (position == 0) {
            FirstProsesFragment.newInstance()
        } else {
            SecondDoneFragment.newInstance()
        }
    }

    override fun getPageTitle(position: Int): CharSequence {
        return mContext.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        return 2
    }

    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(R.string.tab_text_1,
            R.string.tab_text_2)
    }
}