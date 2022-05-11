@file:Suppress("unused")

package com.elkins.watchlist.utility

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

// Fragment extension for setting the title of the AppCompatActivity title bar
fun Fragment.setSupportBarTitle(activity: Activity, title: String) {
    (activity as AppCompatActivity).supportActionBar?.title = title
}

fun Fragment.hideSupportBar(activity: Activity) {
    (activity as AppCompatActivity).supportActionBar?.hide()
}