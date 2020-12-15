package com.klymchuk.githubrepos.ui.base.interfaces

interface GlobalFragmentContext  {
    fun onBack(): Boolean
    fun onKeyBoardOpened(isOpen: Boolean)
}