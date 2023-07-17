package com.labbany.labbany.util

import com.labbany.labbany.pojo.response.TimeSlotResponse

interface DialogsListener {

    fun onDismiss() {}

    fun onResult() {}

    fun onCancel() {}

    fun onResult(time: TimeSlotResponse.TimeSlot) {}

    fun onResult(success: Boolean) {}

    fun <T> onDismiss(data: T) {}

    interface SubListener {

        fun <T> onDismiss(data: T) {}

    }
}