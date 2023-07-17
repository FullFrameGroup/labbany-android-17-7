package com.labbany.labbany.pojo.response

data class NotificationsResponse(
    val code: Int,
    val `data`: Data?,
    val msg: String,
    val success: Boolean
) {
    data class Data(
        val notification: List<Notification>
    )

    data class Notification(
        val notification: String,
        val notificationDate: String,
        val notification_id: Int
    )
}