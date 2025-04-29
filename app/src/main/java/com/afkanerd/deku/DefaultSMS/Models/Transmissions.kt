package com.afkanerd.deku.DefaultSMS.Models

import android.app.PendingIntent
import android.content.Context
import android.os.Build
import android.telephony.SmsManager
import com.afkanerd.deku.DefaultSMS.BuildConfig

object Transmissions {
    private const val DATA_TRANSMISSION_PORT: Short = 8200
    @Throws(Exception::class)
    fun sendTextSMS(
        context: Context,
        destinationAddress: String,
        text: String,
        sentIntent: PendingIntent?,
        deliveryIntent: PendingIntent?,
        subscriptionId: Int
    ) {
        if (text.isEmpty()) return

        val smsManager = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            context.getSystemService(SmsManager::class.java)
                .createForSubscriptionId(subscriptionId)
        else SmsManager.getSmsManagerForSubscriptionId(subscriptionId)

        try {
            val dividedMessage = smsManager.divideMessage(text)
            if (dividedMessage.size < 2) smsManager.sendTextMessage(
                destinationAddress,
                null,
                text,
                sentIntent,
                deliveryIntent
            )
            else {
                val sentPendingIntents = ArrayList<PendingIntent?>()
                val deliveredPendingIntents = ArrayList<PendingIntent?>()

                for (i in 0 until dividedMessage.size - 1) {
                    sentPendingIntents.add(null)
                    deliveredPendingIntents.add(null)
                }

                sentPendingIntents.add(sentIntent)
                deliveredPendingIntents.add(deliveryIntent)

                smsManager.sendMultipartTextMessage(
                    destinationAddress,
                    null,
                    dividedMessage,
                    sentPendingIntents,
                    deliveredPendingIntents
                )
            }
        } catch (e: Exception) {
            throw Exception(e)
        }
    }

    @Throws(Exception::class)
    fun sendDataSMS(
        destinationAddress: String?, data: ByteArray?,
        sentIntent: PendingIntent?, deliveryIntent: PendingIntent?,
        subscriptionId: Int?
    ) {
        if (data == null) return

        val smsManager = SmsManager.getSmsManagerForSubscriptionId(
            subscriptionId!!
        )
        try {
            smsManager.sendDataMessage(
                destinationAddress,
                null,
                DATA_TRANSMISSION_PORT,
                data,
                sentIntent,
                deliveryIntent
            )
        } catch (e: Exception) {
            throw Exception(e)
        }
    }
}
