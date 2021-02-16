package com.klymchuk.githubrepos.navigation

import android.os.Bundle
import androidx.navigation.NavDirections
import com.klymchuk.githubrepos.R

class GlobalReposDetailsFragmentDirections {

    private data class ActionGlobalReposDetailsFragment(
        val url: String,
        val title: String
    ) : NavDirections {
        override fun getActionId(): Int = R.id.action_global_repoDetailsFragment

        override fun getArguments(): Bundle {
            val result = Bundle()
            result.putString("url", this.url)
            result.putString("title", this.title)
            return result
        }
    }

    companion object {
        fun actionGlobalReposDetailsFragment(url: String, title: String): NavDirections =
            ActionGlobalReposDetailsFragment(url, title)
    }

}