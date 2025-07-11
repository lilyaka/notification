package com.revotech.notification.controller

import com.revotech.notification.business.data.Notification
import com.revotech.notification.business.data.NotificationService
import com.revotech.notification.payload.NotificationRequest
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*

@RestController
class NotificationController(
    private val notificationService: NotificationService,
) {

    @PostMapping("/add-notification")
    fun addNotification(@RequestBody payload: NotificationRequest): Notification {
        return notificationService.addNotification(payload)
    }

    @GetMapping("/notifications")
    fun getNotifications(pageable: Pageable): Page<Notification> = notificationService.getNotifications(pageable)

    @PutMapping("/mark-as-read")
    fun markAsRead(): Boolean = notificationService.markAsRead()
}
