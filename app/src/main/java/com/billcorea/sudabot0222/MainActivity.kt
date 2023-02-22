package com.billcorea.sudabot0222

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.afollestad.materialdialogs.MaterialDialog
import com.billcorea.sudabot0222.composable.BottomScreen
import com.billcorea.sudabot0222.composable.HomeScreen
import com.billcorea.sudabot0222.composable.NavGraphs
import com.billcorea.sudabot0222.composable.TopScreen
import com.billcorea.sudabot0222.composable.destinations.HomeScreenDestination
import com.billcorea.sudabot0222.retrofit.DataViewModel
import com.billcorea.sudabot0222.ui.theme.SudaBot0222Theme
import com.billcorea.sudabot0222.ui.theme.typography
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.android.gms.tasks.Task
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.common.IntentSenderForResultStarter
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import com.ramcosta.composedestinations.manualcomposablecalls.composable

class MainActivity : ComponentActivity() {

    private val dataViewModel : DataViewModel by viewModels()
    private var appUpdateManager: AppUpdateManager? = null
    private var appUpdateInfoTask: Task<AppUpdateInfo>? = null
    private var REQUSET_UPDATE_APP = 100

    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            doCloseApps()
        }
    }

    private fun doCloseApps() {
        MaterialDialog(this@MainActivity).show {
            icon(R.drawable.ic_sudabot_icon)
            title(R.string.app_name)
            message(R.string.finishApps)
            positiveButton (R.string.OK){
                finish()
            }
            negativeButton(R.string.CANCEL) {
                this.dismiss()
            }
        }
    }

    @OptIn(ExperimentalMaterialNavigationApi::class, ExperimentalAnimationApi::class,
        ExperimentalMaterial3Api::class
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.onBackPressedDispatcher.addCallback(this, callback)
        appUpdateManager = AppUpdateManagerFactory.create(applicationContext)
        appUpdateInfoTask = appUpdateManager!!.appUpdateInfo

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask!!.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                // This example applies an immediate 즉각적인 update. To apply a flexible update
                // instead, pass in AppUpdateType.FLEXIBLE 유연한
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                doUpdateInApps(appUpdateInfo)
            }
        }

        setContent {

            ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)){ view, insets ->
                val bottom = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
                view.updatePadding(bottom = bottom)
                insets
            }

            val engine = rememberAnimatedNavHostEngine()
            val navController = engine.rememberNavController()
            val startRoute = NavGraphs.root.startRoute

            SudaBot0222Theme (darkTheme = isSystemInDarkTheme()){
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    Scaffold(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(3.dp),
                        topBar = {
                            TopScreen(dataViewModel,
                                doClearChatData = {
                                    MaterialDialog(this@MainActivity).show {
                                        icon(R.drawable.ic_sudabot_icon)
                                        title(R.string.app_name)
                                        message(R.string.clearData)
                                        positiveButton (R.string.OK){
                                            dataViewModel.doClearData()
                                        }
                                        negativeButton(R.string.CANCEL) {
                                            this.dismiss()
                                        }
                                    }
                                },
                                doInformation = {
                                var baseUrl = "https://billcorea.tistory.com/319"
                                val myIntent = Intent(Intent.ACTION_VIEW, Uri.parse(baseUrl))
                                startActivity(myIntent)
                            })
                        },
                        bottomBar = {
                            BottomScreen( doAppendChat = {
                                Log.e("", "append $it")
                                dataViewModel.doAppend(sender = "me", sendText = it)
                            })
                        }
                    ) { it ->
                        DestinationsNavHost(
                            engine = engine,
                            navController = navController,
                            navGraph = NavGraphs.root,
                            modifier = Modifier.padding(it),
                            startRoute = startRoute
                        ) {
                            composable(HomeScreenDestination) {
                                HomeScreen( destinationsNavigator, dataViewModel, modifier = Modifier.padding(3.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    private val updateFlowResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult(),
        ) { result ->
            if (result.resultCode == ComponentActivity.RESULT_OK) {
                doToastMake(R.string.msgAppUpdateOK)
            }
        }

    private fun doUpdateInApps(appUpdateInfo: AppUpdateInfo) {

        val starter =
            IntentSenderForResultStarter { intent, _, fillInIntent, flagsMask, flagsValues, _, _ ->
                val request = IntentSenderRequest.Builder(intent)
                    .setFillInIntent(fillInIntent)
                    .setFlags(flagsValues, flagsMask)
                    .build()

                updateFlowResultLauncher.launch(request)
            }

        appUpdateManager!!.startUpdateFlowForResult(
            appUpdateInfo,
            AppUpdateType.IMMEDIATE,
            starter,
            REQUSET_UPDATE_APP,
        )

    }

    private fun doToastMake(msgResource: Int) {
        Toast.makeText( this@MainActivity, getString(msgResource), Toast.LENGTH_SHORT).show()
    }
}

