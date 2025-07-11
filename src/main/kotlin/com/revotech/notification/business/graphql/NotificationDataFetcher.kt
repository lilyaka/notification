package com.revotech.notification.business.graphql

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsQuery
import com.revotech.graphql.GraphqlUtil
import com.revotech.graphql.type.CustomPageable
import com.revotech.notification.business.data.Notification
import com.revotech.notification.business.data.NotificationService
import graphql.relay.Connection

@DgsComponent
class NotificationDataFetcher(private val notificationService: NotificationService) {
    @DgsQuery
    fun notifications(pageable: CustomPageable): Connection<Notification> {
        val page = GraphqlUtil.toPageable(pageable)
        val notifications = notificationService.getNotifications(page)
        return GraphqlUtil.createConnection(notifications)
    }
}
