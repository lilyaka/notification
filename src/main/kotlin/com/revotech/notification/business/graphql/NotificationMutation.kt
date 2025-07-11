package com.revotech.notification.business.graphql

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.revotech.notification.business.data.NotificationService

@DgsComponent
class NotificationMutation(
    private val notificationService: NotificationService,
) {
    @DgsMutation
    fun markAllAsRead(): Boolean = notificationService.markAsRead()

    @DgsMutation
    fun markAsRead(id: String): Boolean = notificationService.readNotification(id).read

    @DgsMutation
    fun deleteNotification(id: String): Boolean = notificationService.deleteNotification(id)
}
