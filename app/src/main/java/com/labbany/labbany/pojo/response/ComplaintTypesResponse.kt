package com.labbany.labbany.pojo.response

data class ComplaintTypesResponse(
    val code: Int,
    val `data`: Data?,
    val msg: String,
    val success: Boolean
) {
    data class Data(
        val complaint_type: List<ComplaintType>
    )

    data class ComplaintType(
        val complaint_type_id: Int,
        val title: String
    )
}