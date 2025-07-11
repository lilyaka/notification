package com.revotech.notification.business.event

import com.fasterxml.jackson.databind.ObjectMapper
import com.revotech.notification._config.NOTIFICATION_CHANNEL
import com.revotech.notification.business.data.NotificationService
import com.revotech.util.WebUtil
import org.springframework.context.event.EventListener
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class EventListener(
    private val notificationService: NotificationService,
    private val webUtil: WebUtil,
    private val redisTemplate: RedisTemplate<String, Any>,
    private val objectMapper: ObjectMapper
) {

    @Async
    @EventListener
    fun changeToOldNotifications(event: NotificationChangeToOldEvent) {
        val payload = event.source as NotificationEventPayload
        webUtil.changeTenant(payload.tenantId) {
            notificationService.changeToOldNotifications(payload.ids)
        }
    }

    @Async
    @EventListener
    fun notificationToUser(event: NotificationToUser) {
        val payload = event.source as NotificationEventPayload
        redisTemplate.convertAndSend(
            NOTIFICATION_CHANNEL,
            objectMapper.writeValueAsString(payload)
        )
    }
}
