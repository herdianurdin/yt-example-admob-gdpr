package com.samiode.example

import android.app.Activity
import android.content.Context
import com.google.android.ump.ConsentForm
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.FormError
import com.google.android.ump.UserMessagingPlatform

class GoogleMobileAdsConsentManager private constructor(context: Context){
    private val consentInformation: ConsentInformation =
        UserMessagingPlatform.getConsentInformation(context)
    val canRequestAds: Boolean get() = consentInformation.canRequestAds()

    fun gatherConsent(
        activity: Activity, onConsentGatheringCompleteListener: OnConsentGatheringCompleteListener
    ) {
        val params = ConsentRequestParameters.Builder().build()
        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) {
                        formError -> onConsentGatheringCompleteListener
                    .consentGatheringComplete(formError)
                }
            }, {}
        )
    }

    fun showPrivacyOptionsForm(
        activity: Activity,
        onConsentFormDismissedListener: ConsentForm.OnConsentFormDismissedListener
    ) {
        UserMessagingPlatform
            .showPrivacyOptionsForm(
                activity, onConsentFormDismissedListener
            )
    }

    fun interface OnConsentGatheringCompleteListener {
        fun consentGatheringComplete(error: FormError?)
    }

    companion object {
        @Volatile private var instance: GoogleMobileAdsConsentManager? = null

        fun getInstance(context: Context) = instance ?: synchronized(this) {
            instance ?: GoogleMobileAdsConsentManager(context).also {
                instance = it
            }
        }
    }

}