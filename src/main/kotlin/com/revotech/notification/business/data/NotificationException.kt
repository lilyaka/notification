package com.revotech.notification.business.data

import com.revotech.exception.AppException

class NotificationException(code: String, message: String) : AppException(code, message)
