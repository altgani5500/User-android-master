package com.partime.user.helpers

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.partime.user.R
import java.io.UnsupportedEncodingException
import java.net.MalformedURLException
import java.net.URL
import java.net.URLDecoder

    fun buildDeepUri(parse: Uri): Uri {
        val domain = "https://parttimeuser.page.link"

        val builder =
            FirebaseDynamicLinks.getInstance()
                .createDynamicLink()
                .setDomainUriPrefix(domain)
                .setAndroidParameters(DynamicLink.AndroidParameters.Builder().build())
                .setIosParameters(DynamicLink.IosParameters.Builder("com.partime.user").build())
                .setLink(parse)
                .buildDynamicLink()

        return builder.uri

    }

    fun buildDeepLink(jobId: String): String {
        val deepLinkstr = "http://www.partime.org/"
        lateinit var url: URL
        val deepLink = deepLinkstr + "?" + "&" + "job_id=" + jobId
        val deepLinkUri = buildDeepUri(Uri.parse(deepLink))
        try {
            url = URL(deepLinkUri.toString())
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        try {
            val afterDecode: String = URLDecoder.decode(url.toString(), "UTF-8")
            return deepLinkUri.toString()
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            return e.toString()
        }
    }

    fun Context.shortLinkTask(data: String, longlink: String) {
        lateinit var shortLink: Uri
        FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLongLink(
                Uri.parse(longlink)
            )
            .buildShortDynamicLink()
            .addOnSuccessListener { result ->
                shortLink = result.shortLink!!
                val linkFinal = data + shortLink.toString()
                val share = Intent(Intent.ACTION_SEND)
                share.type = "text/plain"
                share.putExtra(Intent.EXTRA_TEXT, linkFinal)
                share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                share.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
                startActivity(Intent.createChooser(share, getString(R.string.share)))
            }
            .addOnFailureListener {
                Log.d("errorlink", it.toString())
            }

    }

    fun Context.shareDataAll(data: String) {
        val share = Intent(Intent.ACTION_SEND)
        share.type = "text/plain"
        share.putExtra(Intent.EXTRA_TEXT, data)
        startActivity(Intent.createChooser(share, getString(R.string.share)))
    }
