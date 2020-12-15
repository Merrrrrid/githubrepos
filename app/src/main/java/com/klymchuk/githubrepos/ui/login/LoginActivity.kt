package com.klymchuk.githubrepos.ui.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.klymchuk.githubrepos.App
import com.klymchuk.githubrepos.BuildConfig
import com.klymchuk.githubrepos.databinding.ActivityLoginBinding
import com.klymchuk.githubrepos.ui.MainActivity
import com.klymchuk.githubrepos.ui.base.interfaces.GlobalFragmentContext
import com.klymchuk.githubrepos.utils.*
import javax.inject.Inject


class LoginActivity : AppCompatActivity(), GlobalFragmentContext {

    private val logTag = logTag()

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as App).component!!.inject(this)

        super.onCreate(savedInstanceState)

        mBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        initUI()

        mViewModel.observeCommands(this, this)
    }

    override fun onResume() {
        super.onResume()
        Reporter.appAction(logTag, "onResume")
        mViewModel.checkCodeOnResume()
    }

    //==============================================================================================
    // *** Navigation ***
    //==============================================================================================
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
    lateinit var mViewModel: LoginActivityViewModel
    private lateinit var mBinding: ActivityLoginBinding

    private fun initUI() {
        mBinding.loginLabel.setOnClickListener {
            mViewModel.onLoginButtonClicked()
        }

        observeState()
    }

    //==============================================================================================
    // *** State ***
    //==============================================================================================
    private var mLastConsumedState: LoginActivityViewModel.State? = null

    private fun observeState() {
        mViewModel.state().observe(this) { state ->
            if (shouldUpdateProgress(state)) {
                if (state.isProgress) {
                    mBinding.progressBar.show()
                    window.setFlags(
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    )
                } else {
                    mBinding.progressBar.gone()
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                }

            }

            if (state.token != "" && mLastConsumedState?.token != state.token) mViewModel.saveUserToken()
            mLastConsumedState = state

        }
    }

    private fun shouldUpdateProgress(state: LoginActivityViewModel.State): Boolean {
        return state.isProgress != mLastConsumedState?.isProgress
    }

    //==============================================================================================
    // *** Commands ***
    //==============================================================================================
    fun checkIsCodeExist() {
        Reporter.appAction(logTag, "checkIsCodeExist")

        val uri = intent!!.data
        if (uri != null && uri.toString().startsWith(BuildConfig.AUTHORIZATION_CALLBACK_URL)) {
            val code = uri.getQueryParameter("code")
            if (code != null) {
                mViewModel.onCodeRetrieve(code)
            } else if (uri.getQueryParameter("error") != null) {
                val error = uri.getQueryParameter("error")
                mViewModel.onCodeErrorRetrieve(error.orEmpty())
            }
        }
    }

    fun navigateToMainMenu() {
        Reporter.appAction(logTag, "navigateToMainMenu")

        startActivity(Intent(this, MainActivity::class.java))
    }

    fun navigateToGitHubLogin() {
        Reporter.appAction(logTag, "navigateToGitHubLogin")

        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                //todo refactor
                Uri.parse("https://github.com/login/oauth/authorize?client_id=${BuildConfig.CLIENT_ID}&redirect_uri=${BuildConfig.AUTHORIZATION_CALLBACK_URL}")
            )
        )
    }
}