package ru.gb.weather.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class MainBroadcastReceiver : BroadcastReceiver() {

    companion object {
        const val SYSTEM_MESSAGE_TITLE = "SYSTEM MESSAGE"
        const val LINEBREAK = "\n"
        const val ACTION = "Action"
        const val COLON = ":"
        const val WHITESPACE = " "
    }

    override fun onReceive(context: Context, intent: Intent) {
        StringBuilder().apply {
            append(SYSTEM_MESSAGE_TITLE)
            append(LINEBREAK)
            append(ACTION)
            append(COLON)
            append(WHITESPACE)
            append(intent.action)
            toString().also {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            }
        }
    }
}