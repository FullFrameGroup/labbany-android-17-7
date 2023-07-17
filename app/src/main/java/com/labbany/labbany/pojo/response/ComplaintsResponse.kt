package com.labbany.labbany.pojo.response

data class ComplaintsResponse(
    val code: Int,
    val `data`: List<Complaint>?,
    val msg: String,
    val success: Boolean
) {

    data class Complaint(
        val complaint_date: String,
        val complaint_description: String,
        val complaint_number: Int,
        val complaint_title: String,
        val is_reviewed: Int
    )
}