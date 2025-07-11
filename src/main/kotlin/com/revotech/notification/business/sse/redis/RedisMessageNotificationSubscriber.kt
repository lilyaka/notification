package com.revotech.notification.business.sse.redis

import com.fasterxml.jackson.databind.ObjectMapper
import com.revotech.notification.business.event.NotificationEventPayload
import com.revotech.notification.business.sse.ServerSendEventsService
import org.slf4j.LoggerFactory
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.stereotype.Service

@Service
class RedisMessageNotificationSubscriber(
    private val serverSendEventsService: ServerSendEventsService,
    private val objectMapper: ObjectMapper,
) : MessageListener {
    private val log = LoggerFactory.getLogger(this::class.java)
    override fun onMessage(message: Message, pattern: ByteArray?) {
        try {
            val payload = objectMapper.readValue(objectMapper.readValue(message.body, String::class.java), NotificationEventPayload::class.java)
            println(payload)
            serverSendEventsService.pushNotification(payload.tenantId, payload.userId)
        } catch (e: Exception) {
            log.warn("Error during processing message: ${String(message.body)}", e)
        }
    }
}
