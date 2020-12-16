package com.klymchuk.githubrepos.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.klymchuk.githubrepos.App
import com.klymchuk.githubrepos.R
import com.klymchuk.githubrepos.databinding.ActivityMainBinding
import com.klymchuk.githubrepos.navigation.Destinations
import com.klymchuk.githubrepos.navigation.Navigator
import com.klymchuk.githubrepos.navigation.Router
import com.klymchuk.githubrepos.navigation.modifierFade
import com.klymchuk.githubrepos.ui.base.interfaces.GlobalFragmentContext
import com.klymchuk.githubrepos.utils.Reporter
import com.klymchuk.githubrepos.utils.hideKeyboard
import com.klymchuk.githubrepos.utils.logTag
import javax.inject.Inject

class MainActivity : AppCompatActivity(), GlobalFragmentContext {

    private val logTag = logTag()

    @Inject
    lateinit var mNavigator: Navigator

    @Inject
    lateinit var mRouter: Router

    override fun onCreate(savedInstanceState: Bundle?) {
        val isProcessRestored = savedInstanceState != null
        Reporter.appAction(logTag, "onCreate isProcessRestored: $isProcessRestored")
        (application as App).component!!.inject(this)

        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mNavigator.observe(this, this, supportFragmentManager)

        if (!isProcessRestored) {
            initNavigation()
        }

        initUI()

        mViewModel.observeCommands(this, this)
    }

    override fun onResume() {
        super.onResume()
        Reporter.appAction(logTag, "onResume")
    }

    //==============================================================================================
    // *** Navigation ***
    //==============================================================================================
    private fun initNavigation() {
        val startDestination = Destinations.repos()

        mRouter.newRoot(startDestination, modifierFade())
    }

    override fun onBackPressed() {
        val fragments = supportFragmentManager.fragments
            // walk from end
            .reversed()

        for (fragment in fragments) {
            if (fragment !is GlobalFragmentContext) {
                continue
            }

            val consumed = fragment.onBack()
            if (consumed) {
                return
            }
        }

        mRouter.back()
    }


    //==============================================================================================
    // *** GlobalFragmentContext ***
    //==============================================================================================
    override fun onBack(): Boolean {
        Reporter.appAction(logTag, "onBack")
        return false
    }

    override fun onKeyBoardOpened(isOpen: Boolean) {
        Reporter.appAction(logTag, "onKeyBoardOpened($isOpen)")
    }

    //==============================================================================================
    // *** UI ***
    //==============================================================================================
    @Inject
    lateinit var mViewModel: MainViewModel
    private lateinit var mBinding: ActivityMainBinding

    private fun initUI() {
        mNavigator.finishAppListener = object : Navigator.FinishAppListener {
            override fun onFinish(): Boolean {
                return true
            }
        }
    }

    //==============================================================================================
    // *** Commands ***
    //==============================================================================================

    fun openLinkInBrowser(url : Uri){
        Reporter.appAction(logTag, "openLinkInBrowser")

        startActivity(Intent(Intent.ACTION_VIEW, url))
    }

    fun showBackButton(){
        Reporter.appAction(logTag, "showBackButton")

       supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.history)
    }

    fun hideBackButton(){
        Reporter.appAction(logTag, "hideBackButton")

        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.title = getString(R.string.app_name)
    }

    //==============================================================================================
    // *** Utils ***
    //==============================================================================================

    fun hideKeyboard() {
        Reporter.appAction(logTag, "hideKeyboard")

        hideKeyboard(this, mBinding.root.windowToken)
    }

}