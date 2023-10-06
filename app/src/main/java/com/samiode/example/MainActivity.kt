package com.samiode.example

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.samiode.example.databinding.ActivityMainBinding
import java.util.concurrent.atomic.AtomicBoolean

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val isMobileAdsInitializeCalled = AtomicBoolean(false)

    private val adRequest = AdRequest.Builder().build()
    private var interstitialAd: InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getConsentForm()

        binding.btnShowAds.setOnClickListener { showInterstitialAd() }
    }

    private fun getConsentForm() {
        GoogleMobileAdsConsentManager.getInstance(this).gatherConsent(this) { consentError ->
            if (consentError != null) GoogleMobileAdsConsentManager.getInstance(this).showPrivacyOptionsForm(this) {
                if (GoogleMobileAdsConsentManager.getInstance(this).canRequestAds) initializeMobileAdsSdk()
            }
            if (GoogleMobileAdsConsentManager.getInstance(this).canRequestAds && consentError == null) initializeMobileAdsSdk()
        }
    }

    private fun initializeMobileAdsSdk() {
        isMobileAdsInitializeCalled.set(true)

        (application as MyApplication).initializeMobileAds()

        if (interstitialAd == null) getInterstitialAds()
    }

    private fun showInterstitialAd() {
        if (interstitialAd != null) {
            interstitialAd?.apply {
                show(this@MainActivity)
                closeCallback {
                    showMessage("Ads Loaded")
                    getInterstitialAds()
                }
            }
            return
        }

        showMessage("Ads Unloaded")
        getInterstitialAds()
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun getInterstitialAds() {
        interstitialAd = null
        InterstitialAd.load(
            this,
            getString(R.string.interstitial_id), adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(mInterstitialAd: InterstitialAd) {
                    super.onAdLoaded(mInterstitialAd)
                    interstitialAd = mInterstitialAd
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    super.onAdFailedToLoad(error)
                    interstitialAd = null
                }
            })
    }

    private inline fun InterstitialAd.closeCallback(crossinline action: () -> Unit) {
        val interstitialAd = this
        interstitialAd.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent()
                interstitialAd.fullScreenContentCallback = null
                action()
            }
        }
    }
}