package com.revotech.notification.business.firebase

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.revotech.notification.payload.NotificationPayload
import org.springframework.stereotype.Service

@Service
class FirebaseService {

    fun sendNotification(tenantId: String, userId: String, payload: List<NotificationPayload>) {
        payload.forEach { sendNotification(tenantId, userId, it) }
    }

    private fun sendNotification(tenantId: String, userId: String, payload: NotificationPayload) {
        val topic = "/topics/tenant_${tenantId}.user_${userId}"

        val builder = Message.builder()
        builder.putData("title", payload.title)
        builder.putData("content", payload.content)
        builder.putData("time", payload.time.toString())
        builder.putData("module", payload.module)
        builder.putData("function", payload.function)
        builder.putData("action", payload.action)
        builder.putData("fromUserId", payload.fromUserId)
        val message: Message = builder
            .setTopic(topic)
            .setNotification(
                com.google.firebase.messaging.Notification.builder()
                    .setTitle(payload.title)
                    .setBody(payload.content)
                    .build()
            )
            .build()

        FirebaseMessaging.getInstance().send(message)
    }

}
