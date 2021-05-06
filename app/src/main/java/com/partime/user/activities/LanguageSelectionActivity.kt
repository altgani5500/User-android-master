package com.partime.user.activities

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import com.partime.user.R
import com.partime.user.prefences.AppPrefence
import com.partime.user.uicomman.BaseActivity
import com.partime.user.uicomman.walkthroughs.WalkThroughActivity
import kotlinx.android.synthetic.main.activity_language_selection.*
import java.util.*


class LanguageSelectionActivity : BaseActivity(), View.OnClickListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_language_selection)

        btnEnglishLang.setOnClickListener(this)
        btnArabicLang.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v == btnEnglishLang) {

            setEnglish()
        }
        if (v == btnArabicLang) {

            setArabic()
        }

        val intent = Intent(this@LanguageSelectionActivity, WalkThroughActivity::class.java)
        startActivity(intent)
        onBackPressed()
    }

    /*
   Method to set arabic language
    */

    private fun setArabic() {
        val locale = Locale("ar", "SA")
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLayoutDirection(config.locale)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            config.setLocale(locale)
            this.applicationContext.createConfigurationContext(config)
        }
        baseContext.resources.updateConfiguration(
            config,
            baseContext.resources.displayMetrics
        )
        appPrefence.setString(AppPrefence.SharedPreferncesKeys.languageSreen.toString(), "1")
        appPrefence.setString(AppPrefence.SharedPreferncesKeys.LANGUAGE.toString(), "ar")

    }

    /*
       Method to set english language
        */
    fun setEnglish() {
        val languageToLoad = "en_US"
        val locale = Locale(languageToLoad)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLayoutDirection(config.locale)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            config.setLocale(locale)
            this.applicationContext.createConfigurationContext(config)
        }
        this.resources.updateConfiguration(config, this.resources.displayMetrics)
        appPrefence.setString(AppPrefence.SharedPreferncesKeys.languageSreen.toString(), "1")
        appPrefence.setString(AppPrefence.SharedPreferncesKeys.LANGUAGE.toString(), "en")

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()

    }
}
