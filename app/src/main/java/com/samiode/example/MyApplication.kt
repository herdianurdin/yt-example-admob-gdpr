package com.samiode.example

import android.app.Application
import com.google.android.gms.ads.MobileAds

class MyApplication: Application() {
    fun initializeMobileAds() { MobileAds.initialize(this) }
}