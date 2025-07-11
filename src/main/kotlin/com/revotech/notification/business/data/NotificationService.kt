package com.revotech.notification.business.data

import com.revotech.notification.business.event.NotificationChangeToOldEvent
import com.revotech.notification.business.event.NotificationEventPayload
import com.revotech.notification.business.event.NotificationToUser
import com.revotech.notification.payload.NotificationRequest
import com.revotech.util.WebUtil
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.MongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Service

@Service
class NotificationService(
    private val notificationRepository: NotificationRepository,
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val webUtil: WebUtil,
    private val mongoOperations: MongoOperations,
) {

    fun addNotification(payload: NotificationRequest): Notification {
        val notification = notificationRepository.save(
            Notification.Builder()
                .userId(payload.userId)
                .title(payload.title)
                .content(payload.content)
                .function(payload.function)
                .module(payload.module)
                .action(payload.action)
                .fromUserId(payload.fromUserId)
                .build()
        )
        applicationEventPublisher.publishEvent(
            NotificationToUser(NotificationEventPayload(webUtil.getTenantId(), payload.userId))
        )
        return notification
    }

    fun countUnreadNotifications(userId: String) = notificationRepository.countByUserIdAndRead(userId, false)

    fun readNotification(id: String): Notification {
        return notificationRepository.findById(id)
            .map {
                if (it.userId != webUtil.getUserId()) throw NotificationException("wrongUser", "Not your Notification")
                it.read = true
                it.new = false
                val save = notificationRepository.save(it)

                applicationEventPublisher.publishEvent(
                    NotificationToUser(NotificationEventPayload(webUtil.getTenantId(), webUtil.getUserId()))
                )
                save
            }
            .orElseThrow { notExist(id) }
    }

    fun deleteNotification(id: String): Boolean {
        return notificationRepository.findById(id)
            .map {
                if (it.userId != webUtil.getUserId()) throw NotificationException("wrongUser", "Not your Notification")
                notificationRepository.delete(it)
                applicationEventPublisher.publishEvent(
                    NotificationToUser(NotificationEventPayload(webUtil.getTenantId(), webUtil.getUserId()))
                )
                true
            }
            .orElseThrow { notExist(id) }
    }

    fun changeToOldNotification(id: String): Notification {
        return notificationRepository.findById(id)
            .map {
                it.new = false
                notificationRepository.save(it)
            }
            .orElseThrow { notExist(id) }
    }

    fun changeToOldNotifications(ids: List<String>): MutableList<Notification> {
        val notifications = notificationRepository.findAllById(ids)
            .map {
                it.new = false
                it
            }
        return notificationRepository.saveAll(notifications)
    }

    private fun notExist(id: String) = NotificationException("notExist", "Notification with id $id is not exits")

    fun getNewNotifications(tenantId: String, userId: String): List<Notification> {
        val notifications = notificationRepository.findByUserIdAndNewIsTrue(userId)
        applicationEventPublisher.publishEvent(NotificationChangeToOldEvent(
            NotificationEventPayload(tenantId, userId, notifications.map { it.id!! }
            )))
        return notifications
    }

    fun getNotifications(pageable: Pageable): Page<Notification> =
        notificationRepository.findByUserIdOrderByTimeDesc(webUtil.getUserId(), pageable)

    fun markAsRead(): Boolean {
        val userId = webUtil.getUserId()
        val query = Query()
        query.addCriteria(
            Criteria
                .where("userId").`is`(userId)
                .andOperator(
                    Criteria.where("read").`is`(false)
                )
        )

        val update = Update()
        update["read"] = true
        update["new"] = false

        mongoOperations.updateMulti(query, update, Notification::class.java)
        applicationEventPublisher.publishEvent(
            NotificationToUser(NotificationEventPayload(webUtil.getTenantId(), webUtil.getUserId()))
        )
        return true
    }
}
