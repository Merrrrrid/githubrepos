package com.klymchuk.githubrepos.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.klymchuk.githubrepos.App
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
        (application as App).component!!.inject(this)

        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mNavigator.observe(this, this, supportFragmentManager)

        initNavigation()

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

        observeState()
    }

    //==============================================================================================
    // *** State ***
    //==============================================================================================
    private var mLastConsumedState: MainViewModel.State? = null

    private fun observeState() {
        mViewModel.state().observe(this) { state ->
            // Progress
//            if (shouldUpdateProgress(state)) {
//                if (state.isProgress) {
//                    mBinding.progress.show()
//                    window.setFlags(
//                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
//                    )
//                } else {
//                    mBinding.progress.gone()
//                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
//                }
//            }
        }
    }

    private fun shouldUpdateProgress(state: MainViewModel.State): Boolean {
        return state.isProgress != mLastConsumedState?.isProgress
    }

    //==============================================================================================
    // *** Commands ***
    //==============================================================================================

    fun openLinkInBrowser(url : Uri){
        startActivity(Intent(Intent.ACTION_VIEW, url))
    }

    fun showBackButton(){
       supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun hideBackButton(){
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    //==============================================================================================
    // *** Utils ***
    //==============================================================================================

    private fun hideKeyboard() {
        hideKeyboard(this, mBinding.root.windowToken)
    }

}