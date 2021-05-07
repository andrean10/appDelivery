package com.kontrakanprojects.appdelivery.view.splash

import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.kontrakanprojects.appdelivery.R
import com.kontrakanprojects.appdelivery.sessions.UserPreference
import com.kontrakanprojects.appdelivery.view.auth.AuthActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    private val activityScope = CoroutineScope(Dispatchers.Main)

    companion object {
        private const val DELAY = 3000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        //hiding title bar of this activity
        window.requestFeature(Window.FEATURE_NO_TITLE)
        //making this activity full screen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        activityScope.launch {
            delay(DELAY)

            // check session login
            val session = UserPreference(this@SplashActivity)
            if (session.getLogin().isLoginValid) {
                when (session.getUser().idRole) {
//                    ChooseLoginFragment.ROLE_GURU -> {
//                        val intent = Intent(this@SplashScreenActivity, GuruActivity::class.java)
//                        startActivity(intent)
//                        finish()
//                    }
//                    ChooseLoginFragment.ROLE_SISWA -> {
//                        val intent = Intent(this@SplashScreenActivity, SiswaActivity::class.java)
//                        startActivity(intent)
//                        finish()
//                    }
                }
            } else {
                val intent = Intent(this@SplashActivity, AuthActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}