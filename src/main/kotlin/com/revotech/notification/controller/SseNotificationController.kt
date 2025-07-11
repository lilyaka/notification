package com.revotech.notification.controller

import com.revotech.notification.business.sse.ServerSendEventsService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SseNotificationController(
    private val serverSendEventsService: ServerSendEventsService,
) {

    @GetMapping("/")
    fun register() = serverSendEventsService.registerEmitter()

}
