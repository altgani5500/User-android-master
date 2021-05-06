package com.gems.app.utilities


import android.content.Context
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import com.partime.user.helpers.Utilities.showToastLong
import com.partime.user.prefences.AppPrefence
import java.io.File
import java.util.regex.Matcher
import java.util.regex.Pattern


object AppValidator {

    val EMAIL_REGEX = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"
    val NAME_REGEX1 = "^[_A-Za-z0-9-\\+]"
    val NAME_REGEX = "^[A-Za-z0-9\\s]{1,}[\\.]{0,1}[A-Za-z0-9\\s]{0,}$"
    val CHAR_REGEX = ".*[a-zA-Z]+.*"
    val ONLY_CHAR_REGEX = "^[a-zA-Z ]*$"
    val MOBILE_REGEX = "\\d{10}"
    val MOBILE_REGEX_TEST = "\\d{10}|.{11}"
    val YEAR_REGEX = "\\d{4}"
    val ONLY_DIGIT_REGEX = "[0-9]+"

    val PINCODE_REGEX = "^([1-9])([0-9]){5}$"
    val VEHICLE_REGEX = "^[A-Z]{2} [0-9]{2} [A-Z]{2} [0-9]{4}$"
    val PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#\$%^&+=])(?=\\\\S+\$).{4,}\$"//.{6,}
    val IMAGE_EXTENSIONS = arrayOf("jpg", "jpeg", "png")

    fun isValidEmail(editText: String): Boolean {

        return EMAIL_REGEX.toRegex().matches(editText)

    }

    fun isValidPassword(password: String): Boolean {

        val pattern: Pattern
        val matcher: Matcher

        val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$"

        pattern = Pattern.compile(PASSWORD_PATTERN)
        matcher = pattern.matcher(password)

        return matcher.matches()

    }


    fun isValidFullName(context: Context, editText: EditText, msg: String): Boolean {
        if (editText.text.toString().trim { it <= ' ' } == "") {
            showToastLong(context, msg)
            editText.addTextChangedListener(RemoveErrorEditText(editText))
            editText.requestFocus()
            return false
        } else if (editText.text.toString().matches(ONLY_CHAR_REGEX.toRegex()))
            return true
        else {
            //showToastLong(context, context.resources.getString(R.string.error_invalid_fullname))
            editText.addTextChangedListener(RemoveErrorEditText(editText))
            editText.requestFocus()
            return false
        }
    }

    fun isValidMobile(context: Context, editText: EditText, msg: String): Boolean {

        if (editText.text.toString().trim { it <= ' ' } == "") {
            showToastLong(context, msg)
            editText.addTextChangedListener(RemoveErrorEditText(editText))
            editText.requestFocus()
            return false
        } else if (editText.text.toString().length >= 9)
            return true
        else {
            //showToastLong(context, context.resources.getString(R.string.error_invalid_mobile))
            editText.addTextChangedListener(RemoveErrorEditText(editText))
            editText.requestFocus()
            return false
        }

    }

    fun isOnlyDigit(editText: EditText): Boolean {
        return editText.text.toString().matches(ONLY_DIGIT_REGEX.toRegex())
    }

    fun isValidCardNumber(context: Context, editText: EditText, msg: String): Boolean {
        if (editText.text.toString().trim { it <= ' ' } == "") {
            showToastLong(context, msg)
            editText.addTextChangedListener(RemoveErrorEditText(editText))
            editText.requestFocus()
            return false
        } else if (editText.text.toString().trim { it <= ' ' }.length == 19)
            return true
        else {
            showToastLong(context, "Invalid card number.")
            editText.addTextChangedListener(RemoveErrorEditText(editText))
            editText.requestFocus()
            return false
        }
    }


    fun isValidPincode(editText: EditText, msg: String): Boolean {
        if (editText.text.toString().trim { it <= ' ' } == "") {

            editText.error = msg
            editText.addTextChangedListener(RemoveErrorEditText(editText))
            editText.requestFocus()
            return false
        } else if (editText.text.toString().matches(PINCODE_REGEX.toRegex()))
            return true
        else {
            editText.error = "invalid pincode"
            editText.addTextChangedListener(RemoveErrorEditText(editText))
            editText.requestFocus()
            return false
        }
    }


    fun isValidAddress(editText: EditText, msg: String): Boolean {
        if (editText.text.toString().trim { it <= ' ' } == "") {

            editText.error = msg
            editText.addTextChangedListener(RemoveErrorEditText(editText))
            editText.requestFocus()
            return false
        } else if (editText.text.toString().matches(NAME_REGEX.toRegex()))
            return true
        else {
            editText.error = "invalid address"
            editText.addTextChangedListener(RemoveErrorEditText(editText))
            editText.requestFocus()
            return false
        }
    }

    fun isOnlyChars(context: Context, editText: EditText, msg: String): Boolean {
        if (editText.text.toString().trim { it <= ' ' } == "") {
            showToastLong(context, msg)
            editText.addTextChangedListener(RemoveErrorEditText(editText))
            editText.requestFocus()
            return false
        } else if (editText.text.toString().matches(ONLY_CHAR_REGEX.toRegex()))
            return true
        else {
            showToastLong(context, "invalid name")
            editText.addTextChangedListener(RemoveErrorEditText(editText))
            editText.requestFocus()
            return false
        }

    }

    fun isValidVehicleNo(editText: EditText, msg: String): Boolean {
        if (editText.text.toString().trim { it <= ' ' } == "") {

            editText.error = msg
            editText.addTextChangedListener(RemoveErrorEditText(editText))
            editText.requestFocus()
            return false
        } else if (editText.text.toString().matches(VEHICLE_REGEX.toRegex()))
            return true
        else {
            editText.error = "invalid vehicle no."
            editText.addTextChangedListener(RemoveErrorEditText(editText))
            editText.requestFocus()
            return false
        }
    }


    fun isValidImage(file: File): Boolean {
        for (extensions in IMAGE_EXTENSIONS) {
            if (file.name.toLowerCase().endsWith(extensions))
                return true
        }
        return false
    }

    fun isValidYear(data: String): Boolean {
        return data.matches(YEAR_REGEX.toRegex())
    }


    fun isValid(context: Context, editText: EditText, msg: String): Boolean {
        if (editText.text.toString().trim { it <= ' ' } == "") {
            showToastLong(context, msg)
            editText.addTextChangedListener(RemoveErrorEditText(editText))
            editText.requestFocus()
            return false
        }
        return true
    }


    class RemoveErrorEditText(private val editText: EditText) : TextWatcher {

        override fun afterTextChanged(s: Editable) {

            editText.error = null
        }

        override fun beforeTextChanged(
            s: CharSequence, start: Int, count: Int,
            after: Int
        ) {
        }

        override fun onTextChanged(
            s: CharSequence, start: Int, before: Int,
            count: Int
        ) {

        }

    }

    fun isValidUserName(context: Context, editText: EditText): Boolean {

        if (editText.text.length == 0) {
            showToastLong(context, "Enter Your Email or Phone Number")
            return false
        } else if (editText.text.matches(ONLY_DIGIT_REGEX.toRegex())) {
            if (!editText.text.matches(MOBILE_REGEX.toRegex()) || editText.text.length != 10) {
                showToastLong(context, "Please enter valid mobile no")
                return false
            }
        } else if (!editText.text.matches(EMAIL_REGEX.toRegex()) || editText.text.length > 40) {
            showToastLong(context, "Please enter valid email")
            return false
        }

        return true

    }

    fun isValidPhoneNo(context: Context, editText: EditText): Boolean {

        if (editText.text.toString().matches(ONLY_DIGIT_REGEX.toRegex())) {
            if (!editText.text.toString().matches(MOBILE_REGEX.toRegex()) || editText.text.length != 10) {
                showToastLong(context, "Please enter valid mobile no")
                return false
            }

        }
        return true
    }


    /*
Method to check internet connectivity
*/
    fun isInternetAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork != null
    }


    fun rotateBackArrow(backButton: View, context: Context) {

        val appPrefence = AppPrefence.INSTANCE
        appPrefence.initAppPreferences(context)
        val defLan = appPrefence.getString(AppPrefence.SharedPreferncesKeys.LANGUAGE.toString())
        val lan: String
        if (defLan == "ar") {
            backButton.rotation = 180F

        } else {
            backButton.rotation = 0F
        }
    }

    fun rotateChat(backButton: View, context: Context, arBackground: Drawable, engBackground: Drawable) {

        val appPrefence = AppPrefence.INSTANCE
        appPrefence.initAppPreferences(context)
        val defLan = appPrefence.getString(AppPrefence.SharedPreferncesKeys.LANGUAGE.toString())
        val lan: String
        if (defLan == "ar") {
            backButton.background = arBackground
        } else {
            backButton.background = engBackground
        }
    }

}
