package com.kontrakanprojects.appdelivery.view.splash

import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.kontrakanprojects.appdelivery.R
import com.kontrakanprojects.appdelivery.sessions.UserPreference
import com.kontrakanprojects.appdelivery.view.admin.AdminActivity
import com.kontrakanprojects.appdelivery.view.auth.ChooseLoginFragment
import com.kontrakanprojects.appdelivery.view.courier.CourierActivity
import com.kontrakanprojects.appdelivery.view.home.HomeActivity
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

        //hiding title bar of this activity
        window.requestFeature(Window.FEATURE_NO_TITLE)
        //making this activity full screen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        setContentView(R.layout.activity_splash)

        activityScope.launch {
            delay(DELAY)

            // check session login
            val session = UserPreference(this@SplashActivity)
            if (session.getLogin().isLoginValid) {
                when (session.getUser().idRole) {
                    ChooseLoginFragment.ROLE_ADMIN -> {
                        val intent = Intent(this@SplashActivity, AdminActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    ChooseLoginFragment.ROLE_COURIER -> {
                        val intent = Intent(this@SplashActivity, CourierActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            } else {
                val intent = Intent(this@SplashActivity, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}