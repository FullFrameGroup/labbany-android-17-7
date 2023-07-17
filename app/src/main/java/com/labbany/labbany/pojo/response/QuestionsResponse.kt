package com.labbany.labbany.pojo.response

data class QuestionsResponse(
    val code: Int,
    val `data`: Data,
    val success: Boolean
) {
    data class Data(
        val feedback_questions: List<FeedbackQuestion>
    )

    data class FeedbackQuestion(
        val id: Int,
        val title: String,
        var answer:Boolean=true
    )
}