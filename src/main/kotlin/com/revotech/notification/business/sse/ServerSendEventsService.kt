package com.revotech.notification.business.sse

import com.revotech.notification.business.data.NotificationService
import com.revotech.notification.business.firebase.FirebaseService
import com.revotech.notification.payload.NotificationPayload
import com.revotech.notification.payload.NotificationResponse
import com.revotech.util.WebUtil
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.event
import java.io.IOException
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Service
class ServerSendEventsService(
    private val notificationService: NotificationService,
    private val firebaseService: FirebaseService,
    private val webUtil: WebUtil,
) {
    private val mapSse = ConcurrentHashMap<String, MutableMap<String, SseEmitter>>()
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun registerEmitter(): SseEmitter {
        val tenantId = webUtil.getTenantId()
        val userId = webUtil.getUserId()
        val clientId = "$userId-${UUID.randomUUID()}"
        val emitter = SseEmitter(60000 * 5).apply {
            onCompletion {
                logger.debug("Emitter completed for client $clientId")
                removeAndCleanupEmitter(tenantId, clientId)
            }
            onError { e ->
                logger.error("Emitter error for client $clientId", e)
                removeAndCleanupEmitter(tenantId, clientId)
            }
            onTimeout {
                logger.debug("Emitter timeout for client $clientId")
                removeAndCleanupEmitter(tenantId, clientId)
            }
        }
        synchronized(mapSse) {
            mapSse.getOrPut(tenantId) { mutableMapOf() }[clientId] = emitter
        }
        try {
            sendMessage(
                emitter = emitter,
                message = NotificationResponse(notificationService.countUnreadNotifications(userId))
            )
        } catch (e: Exception) {
            logger.error("Failed to send initial message to client $clientId", e)
            removeAndCleanupEmitter(tenantId, clientId)
            throw e
        }
        return emitter
    }

    fun pushNotification(tenantId: String, userId: String) {
        mapSse[tenantId]?.run {
            webUtil.changeTenant(tenantId) {
                val notificationResponse = NotificationResponse(
                    notificationService.countUnreadNotifications(userId),
                    notificationService.getNewNotifications(tenantId, userId)
                        .map { NotificationPayload(it) }
                )
                filterKeys { it.startsWith("$userId-") }
                    .forEach { (k, emitter) ->
                        try {
                            sendMessage(emitter, message = notificationResponse)
                        } catch (e: IOException) {
                            logger.error("Failed to send update to client $k", e)
                            removeAndCleanupEmitter(tenantId, k)
                        }
                    }
                firebaseService.sendNotification(
                    tenantId = tenantId,
                    userId = userId,
                    payload = notificationResponse.notifications
                )
            }
        }
    }

    private fun removeAndCleanupEmitter(tenantId: String, clientId: String) {
        synchronized(mapSse) {
            mapSse[tenantId]?.remove(clientId)?.also { emitter ->
                try {
                    emitter.complete()
                } catch (e: Exception) {
                    logger.debug("Error completing emitter", e)
                }
            }
            if (mapSse[tenantId]?.isEmpty() == true) {
                mapSse.remove(tenantId)
            }
        }
    }

    private fun sendMessage(emitter: SseEmitter, eventName: String = "push-notification", message: Any) {
        try {
            emitter.send(
                event()
                    .name(eventName)
                    .data(message, MediaType.APPLICATION_JSON)
            )
        } catch (e: Exception) {
            logger.error("Failed to send SSE message", e)
            throw e
        }
    }
}
