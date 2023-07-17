package com.labbany.labbany.ui.rate_order

import androidx.lifecycle.ViewModel
import com.labbany.labbany.data.NetworkState
import com.labbany.labbany.data.OrdersServices
import com.labbany.labbany.pojo.request.RateOrderRequest
import com.labbany.labbany.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class RateOrderViewModel(private val ordersServices: OrdersServices) : ViewModel() {

    private val _feedbackQuestionsStateFlow = MutableStateFlow<NetworkState>(NetworkState.Idle)
    val feedbackQuestionsStateFlow: MutableStateFlow<NetworkState> get() = _feedbackQuestionsStateFlow

    private val _saveFeedbackQuestionsStateFlow = MutableStateFlow<NetworkState>(NetworkState.Idle)
    val saveFeedbackQuestionsStateFlow: MutableStateFlow<NetworkState> get() = _saveFeedbackQuestionsStateFlow

    fun feedbackQuestions(
    ) {
        _feedbackQuestionsStateFlow.value = NetworkState.Loading

        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                ordersServices.feedbackQuestions()
            }.onFailure {
                _feedbackQuestionsStateFlow.value =
                    NetworkState.Error(Constants.Codes.EXCEPTIONS_CODE)
            }.onSuccess {

                if (it.body() != null)
                    _feedbackQuestionsStateFlow.value = NetworkState.Result(it.body())
                else
                    _feedbackQuestionsStateFlow.value =
                        NetworkState.Error(Constants.Codes.UNKNOWN_CODE)

            }
        }

    }

    fun saveFeedbackQuestions(
        rateOrderRequest: RateOrderRequest
    ) {
        _saveFeedbackQuestionsStateFlow.value = NetworkState.Loading

        CoroutineScope(Dispatchers.IO).launch {
            kotlin.runCatching {
                ordersServices.saveFeedbackQuestions(rateOrderRequest)
            }.onFailure {
                _saveFeedbackQuestionsStateFlow.value =
                    NetworkState.Error(Constants.Codes.EXCEPTIONS_CODE)
            }.onSuccess {

                if (it.body() != null)
                    _saveFeedbackQuestionsStateFlow.value = NetworkState.Result(it.body())
                else
                    _saveFeedbackQuestionsStateFlow.value =
                        NetworkState.Error(Constants.Codes.UNKNOWN_CODE)

            }
        }

    }

    companion object {
//        private val TAG = this::class.java.name
    }

}