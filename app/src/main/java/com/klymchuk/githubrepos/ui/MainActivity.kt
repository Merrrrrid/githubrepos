package com.klymchuk.githubrepos.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ActionOnlyNavDirections
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.klymchuk.githubrepos.BuildConfig
import com.klymchuk.githubrepos.R
import com.klymchuk.githubrepos.databinding.ActivityMainBinding
import com.klymchuk.githubrepos.navigation.GlobalReposDetailsFragmentDirections
import com.klymchuk.githubrepos.utils.Reporter
import com.klymchuk.githubrepos.utils.hideKeyboard
import com.klymchuk.githubrepos.utils.logTag
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val logTag = logTag()

    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        val isProcessRestored = savedInstanceState != null
        Reporter.appAction(logTag, "onCreate isProcessRestored: $isProcessRestored")
//        (application as App).component!!.inject(this)

        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.reposFragment, R.id.loginFragment)
        )

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        setSupportActionBar(mBinding.toolbar)
        setupActionBarWithNavController(navController, appBarConfiguration)

        initUI()

        mViewModel.observeCommands(this, this)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onResume() {
        super.onResume()
        Reporter.appAction(logTag, "onResume")
        mViewModel.checkCodeOnResume()
    }

    @Inject
    lateinit var mViewModel: MainViewModel
    private lateinit var mBinding: ActivityMainBinding

    private fun initUI() {
        observeState()
    }

    fun hideKeyboard() {
        Reporter.appAction(logTag, "hideKeyboard")

        hideKeyboard(this, mBinding.root.windowToken)
    }

    private var mLastConsumedState: MainViewModel.State? = null

    private fun observeState() {
        mViewModel.state().observe(this) { state ->

            if (state.token != "" && mLastConsumedState?.token != state.token) mViewModel.saveUserToken()

            if (state.userFromDB?.token != null && state.userFromDB.token.isNotEmpty()) {
                navigateToRepos()
            }

            mLastConsumedState = state
        }
    }


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

    fun navigateToRepos() {
        Reporter.appAction(logTag, "navigateToRepos")

        val nav: NavDirections = ActionOnlyNavDirections(R.id.action_loginFragment_to_reposFragment)
        navController.navigate(nav)
    }

    fun navigateToRepoDetails(url: String, title: String) {
        Reporter.appAction(logTag, "navigateToRepoDetails")

        val action = GlobalReposDetailsFragmentDirections.actionGlobalReposDetailsFragment(url = url, title = title)
        navController.navigate(action)
    }

    fun navigateToGitHubLogin() {
        Reporter.appAction(logTag, "navigateToGitHubLogin")

        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://github.com/login/oauth/authorize?client_id=${BuildConfig.CLIENT_ID}&redirect_uri=${BuildConfig.AUTHORIZATION_CALLBACK_URL}")
            )
        )
    }

}