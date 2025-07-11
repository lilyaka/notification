package com.revotech.notification.business.data

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository

interface NotificationRepository : MongoRepository<Notification, String> {
    fun countByUserIdAndRead(userId: String, read: Boolean): Int

    fun findByUserIdAndRead(userId: String, read: Boolean): List<Notification>

    fun findByUserIdAndNewIsTrue(userId: String): List<Notification>

    fun findByUserIdOrderByTimeDesc(userId: String, pageable: Pageable): Page<Notification>
}
