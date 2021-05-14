package com.byjus.thelearningapp.inappreview

import android.app.Application
import com.byjus.thelearningapp.inapprating.InAppRatingManager

class MainApplication : Application() {

    lateinit var ratingManager: InAppRatingManager

    override fun onCreate() {
        super.onCreate()
        ratingManager = InAppRatingManager(this, true)
    }

}