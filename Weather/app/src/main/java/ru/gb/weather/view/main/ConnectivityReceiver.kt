package ru.gb.weather.view.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class ConnectivityReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        Log.d("!!!!!!!!", "onReceive() called with: context = $context, intent = $intent")
        TODO("connectivityReceiver.onReceive() is not implemented")
    }
}