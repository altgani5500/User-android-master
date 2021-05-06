package com.partime.user.activities

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import com.gems.app.utilities.AppValidator
import com.partime.user.R
import com.partime.user.helpers.Utilities
import com.partime.user.prefences.AppPrefence
import com.partime.user.uicomman.BaseActivity
import kotlinx.android.synthetic.main.activity_change_language.*
import java.util.*

class ChangeLanguageActivity : BaseActivity(), View.OnClickListener {

    lateinit var language: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_language)
        AppValidator.rotateBackArrow(imgBackChangeLang, this@ChangeLanguageActivity)
        btnChangeLangSubmit.setOnClickListener(this)
        imgBackChangeLang.setOnClickListener(this)
    }

    override fun onClick(v: View?) {

        when (v) {

            btnChangeLangSubmit -> changeLang()

            imgBackChangeLang -> onBackPressed()
        }

    }

    /*
    Method to chnage the language
     */
    private fun changeLang() {

        //get selected language
        var lang = languageSelected()
        if (lang == "English") {

            language = "en"
            setEnglish()
        } else {

            language = "ar"
            setArabic()
        }

        //clear all activities after language is chnaged
        Utilities.clearAllgoToActivity(this@ChangeLanguageActivity, HomeActivity::class.java)
        appPrefence.setString(AppPrefence.SharedPreferncesKeys.LANGUAGE.toString(), language)
        appPrefence.setBoolean(AppPrefence.SharedPreferncesKeys.IS_FIRST_TIME_LAUNCH.toString(), false)
        finish()
    }

    /*
    Method to get the selected language
     */
    private fun languageSelected(): String {

        val selectedId = langRadioGroup.checkedRadioButtonId
        val radioButton = findViewById<View>(selectedId) as RadioButton
        return radioButton.text.toString()

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

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
