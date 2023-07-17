package com.labbany.labbany.pojo.request

data class RateOrderRequest(
    val comment: String,
    val order_id: Int,
    val questions: List<Answer>,
    val user_id: Int
) {
    data class Answer(
        val answer: String,
        val question_id: Int
    )
}