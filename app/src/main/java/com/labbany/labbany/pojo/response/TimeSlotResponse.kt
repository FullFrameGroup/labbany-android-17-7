package com.labbany.labbany.pojo.response

data class TimeSlotResponse(
    val code: Int,
    val `data`: Data,
    val msg: String,
    val success: Boolean
) {
    data class Data(
        val time_slot: List<TimeSlot>
    )

    data class TimeSlot(
        val from_time: String,
        val id: Int,
        val to_time: String
    )
}