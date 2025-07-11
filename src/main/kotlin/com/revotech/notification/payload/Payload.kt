package com.revotech.notification.payload

import com.revotech.notification.business.data.Notification
import java.time.LocalDateTime

data class NotificationResponse(
    val unreadNotifications: Int,
    val notifications: List<NotificationPayload> = listOf()
)

data class NotificationRequest(
    val userId: String,
    val title: String,
    val content: String,
    val module: String,
    val function: String,
    val action: String,
    val fromUserId: String = "system",
)

data class NotificationPayload(
    val title: String,
    val content: String,
    val time: LocalDateTime,
    val module: String,
    val function: String,
    val action: String,
    val fromUserId: String = "system",
) {
    constructor(notification: Notification) : this(
        notification.title,
        notification.content,
        notification.time,
        notification.module,
        notification.function,
        notification.action,
        notification.fromUserId,
    )
}
