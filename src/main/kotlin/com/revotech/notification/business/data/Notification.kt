package com.revotech.notification.business.data

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
const val DB_NOTIFICATION = "notification"
@Document(DB_NOTIFICATION)
data class Notification(
    @Id
    val id: String?,
    val userId: String,
    val fromUserId: String = "system",
    val title: String,
    val content: String,
    val time: LocalDateTime = LocalDateTime.now(),
    val module: String,
    val function: String,
    var action: String,
    var read: Boolean = false,
    var new: Boolean = true,
) {
    class Builder private constructor(
        private var userId: String = "",
        private var title: String = "",
        private var content: String = "",
        private var module: String = "",
        private var function: String = "",
        private var action: String = "",
        private var fromUserId: String = "system",
    ) {
        constructor() : this("", "", "", "", "", "")

        fun userId(userId: String) = apply { this.userId = userId }
        fun title(title: String) = apply { this.title = title }
        fun content(content: String) = apply { this.content = content }
        fun module(module: String) = apply { this.module = module }
        fun function(function: String) = apply { this.function = function }
        fun action(action: String) = apply { this.action = action }
        fun fromUserId(fromUserId: String) = apply { this.fromUserId = fromUserId }

        fun build() = Notification(
            id = null,
            userId = userId,
            title = title,
            content = content,
            time = LocalDateTime.now(),
            module = module,
            function = function,
            action = action,
            fromUserId = fromUserId
        )
    }

}
