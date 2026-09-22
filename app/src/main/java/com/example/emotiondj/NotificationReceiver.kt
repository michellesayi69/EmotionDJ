package com.example.emotiondj

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.emotiondj.utils.NotificationHelper

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "STOP_NOTIFICATION") {
            NotificationHelper.cancelNotification(context)
            // Also stop any background services if needed (none yet)
        }
    }
}