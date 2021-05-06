package com.parttime.user.uicomman

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.partime.user.Constants.Constants
import com.partime.user.R
import com.partime.user.activities.HomeActivity
import com.partime.user.activities.JobDescriptionActivity
import com.partime.user.activities.LanguageSelectionActivity
import com.partime.user.prefences.AppPrefence
import com.partime.user.uicomman.BaseActivity
import java.util.*


class SplashActivity : BaseActivity() {

    private var mDelayHandler: Handler? = null

    private val SPLASH_TIMER: Long = 1000

    internal val mRunnable: Runnable = Runnable {
        if (!isFinishing) {

            //val l:String=appPrefence.getString(AppPrefence.SharedPreferncesKeys.LANGUAGE.toString())

            if (!appPrefence.getString(AppPrefence.SharedPreferncesKeys.LANGUAGE.toString()).isNullOrBlank() ||
                !appPrefence.getString(AppPrefence.SharedPreferncesKeys.LANGUAGE.toString()).isNullOrEmpty()
            ) {

                setLanguage()
            }

            nextScreenOpenWithLangage()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        //Initialize the Handler
        mDelayHandler = Handler()

        //Navigate with delay
        mDelayHandler!!.postDelayed(mRunnable, SPLASH_TIMER)

        //clear prefrences if any.........
        clearMapPrefrences()

    }

    private fun clearMapPrefrences() {
        appPrefence.setString(AppPrefence.SharedPreferncesKeys.industryFilter.toString(), "")
        appPrefence.setString(AppPrefence.SharedPreferncesKeys.jobTitleFilter.toString(), "")
        appPrefence.setString(AppPrefence.SharedPreferncesKeys.cityFilter.toString(), "")
        appPrefence.setString(AppPrefence.SharedPreferncesKeys.companyFilter.toString(), "")
        appPrefence.setString(AppPrefence.SharedPreferncesKeys.stateFilter.toString(), "")
        appPrefence.setString(AppPrefence.SharedPreferncesKeys.countryFilter.toString(), "")
        appPrefence.setString(AppPrefence.SharedPreferncesKeys.currentLocation.toString(), "")
        appPrefence.setString(AppPrefence.SharedPreferncesKeys.hourweekFilter.toString(), "")
        appPrefence.setString(AppPrefence.SharedPreferncesKeys.hourPaidRateFilter.toString(), "")
        appPrefence.setString(AppPrefence.SharedPreferncesKeys.jobtypeFilter.toString(), "")
        appPrefence.setString(AppPrefence.SharedPreferncesKeys.currentAddOne.toString(), "")
        appPrefence.setString(AppPrefence.SharedPreferncesKeys.currentAddTwo.toString(), "")

    }

    private fun nextScreenOpenWithLangage() {
        Handler().postDelayed(Runnable {
            if (!appPrefence.isFirstTimeLaunch) {
                dynamicLink()
            } else {
                Intent(this, LanguageSelectionActivity::class.java).also {
                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(it)
                    finish()
                }
            }

        }, SPLASH_TIMER)
    }

    override fun onStart() {
        super.onStart()
        overridePendingTransition(R.anim.right_to_left_animation, R.anim.left_to_right_animation)
    }

    override fun onDestroy() {
        /*super.onDestroy()
        overridePendingTransition(R.anim.right_to_left_animation, R.anim.left_to_right_animation)*/


        if (mDelayHandler != null) {
            mDelayHandler!!.removeCallbacks(mRunnable)
        }

        super.onDestroy()

    }

    fun setLanguage() {
        val language = appPrefence.getString(AppPrefence.SharedPreferncesKeys.LANGUAGE.toString())
        if (language == "ar") {
            val myLocale = Locale("ar")//"ar" if you want arabic
            Locale.setDefault(myLocale)
            val config = android.content.res.Configuration()
            config.locale = myLocale
            baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
        }
        if (language == "en") {
            val myLocale = Locale("en")//"en" if you want english
            Locale.setDefault(myLocale)
            val config = android.content.res.Configuration()
            config.locale = myLocale
            baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
        }
    }

    private fun dynamicLink() {
        FirebaseDynamicLinks.getInstance().getDynamicLink(intent).addOnSuccessListener(this) { p0 ->
            if (p0 != null) {
                val data = p0.link.toString()
                val values = data.substring(data.indexOf("?") + 1, data.length)
                val valueArray = values.split("&").toTypedArray()
                val type = valueArray[0]
                val postId =  valueArray[1]
                val typeArray = type.split("=").toTypedArray()
                val postIdArray = postId.split("=").toTypedArray()


                val jobId =  postIdArray[1]


                var intent = Intent(this@SplashActivity, JobDescriptionActivity::class.java)
                intent.putExtra(Constants.JOB_ID, jobId.toInt())
                intent.putExtra(Constants.FROM, "splash")
                startActivity(intent)

            } else {
                launchActivity(HomeActivity::class.java)
                finish()
            }
        }
    }
}
