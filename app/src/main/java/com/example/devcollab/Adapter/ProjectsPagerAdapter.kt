package com.example.devcollab.Adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.devcollab.Fragments.AppliedProjectsFragment
import com.example.devcollab.Fragments.MyProjectsFragment
import com.example.devcollab.Fragments.RecentProjectsFragment

class ProjectsPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3  // Three tabs

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> RecentProjectsFragment()  // First Tab
            1 -> MyProjectsFragment()
            2 -> AppliedProjectsFragment()  // Second Tab
            else -> RecentProjectsFragment()
        }
    }
}