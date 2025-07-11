package com.revotech.notification.business.event

import org.springframework.context.ApplicationEvent

class NotificationChangeToOldEvent(payload: NotificationEventPayload) : ApplicationEvent(payload)

class NotificationToUser(payload: NotificationEventPayload) : ApplicationEvent(payload)

data class NotificationEventPayload(
    val tenantId: String,
    val userId: String,
    val ids: List<String> = listOf(),
)
