package com.labbany.labbany.ui.rate_order

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.labbany.labbany.R
import com.labbany.labbany.data.NetworkState
import com.labbany.labbany.databinding.FragmentRateOrderBinding
import com.labbany.labbany.pojo.request.RateOrderRequest
import com.labbany.labbany.pojo.response.GeneralResponse
import com.labbany.labbany.pojo.response.QuestionsResponse
import com.labbany.labbany.ui.help.HelperDialog
import com.labbany.labbany.util.*
import org.koin.android.ext.android.inject

class RateOrderFragment : DialogFragment() {

    private lateinit var binding: FragmentRateOrderBinding
    private lateinit var navController: NavController
    private lateinit var adapter: QuestionsAdapter
    private val viewModel: RateOrderViewModel by inject()
    private val shardHelper: ShardHelper by inject()
    private lateinit var mContext: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_rate_order, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUp()
        mContext=requireContext()
        navController = findNavController()
        adapter = QuestionsAdapter()

        binding.rvQuestions.adapter = adapter

        data()

        binding.tvSend.setOnClickListener {

            if (!verify()) return@setOnClickListener

            rate()
        }

    }

    private fun rate() {

        val rateOrderRequest =
            RateOrderRequest(
                comment = binding.etNotes.text.toString(),
                order_id = requireArguments().getInt(Constants.ORDER_ID),
                questions = adapter.answers(),
                user_id = shardHelper.id
            )

        viewModel.saveFeedbackQuestions( rateOrderRequest)

        lifecycleScope.launchWhenStarted {
            viewModel.saveFeedbackQuestionsStateFlow.collect {

                when (it) {
                    is NetworkState.Idle -> {
                        return@collect
                    }
                    is NetworkState.Loading -> {
                        visProgress( true)
                    }
                    is NetworkState.Error -> {
                        visProgress( false)
                        it.handleErrors(mContext, null)
                    }
                    is NetworkState.Result<*> -> {
                        val response = it.response as GeneralResponse

                        visProgress( false)

                        when {
                            response.success -> {

                                val dialogsListener = object : DialogsListener {
                                    override fun onDismiss() {
                                        dismiss()
                                    }
                                }

                                showHelperDialog(getString(R.string.rate_msg), dialogsListener)

                            }
                            else -> NetworkState.Error(response.code)
                                .handleErrors(mContext)

                        }

                    }
                }
            }
        }
    }

    private fun verify(): Boolean {

        var isNull = false

        if (adapter.isEmpty()) {
            showHelperDialog(getString(R.string.can_not_rating), null)
            isNull = true
        }

        if (!Utils.validateET(binding.etNotes, null)) isNull = true

        return !isNull
    }

    private fun showHelperDialog(msg: String, mDialogsListener: DialogsListener?) =
        HelperDialog(msg, mDialogsListener).show(parentFragmentManager, "")

    private fun data() {

        viewModel.feedbackQuestions()

        lifecycleScope.launchWhenStarted {
            viewModel.feedbackQuestionsStateFlow.collect {

                when (it) {
                    is NetworkState.Idle -> {
                        return@collect
                    }
                    is NetworkState.Loading -> {
                        visProgress( true)
                    }
                    is NetworkState.Error -> {
                        visProgress( false)
                        it.handleErrors(mContext, null)
                    }
                    is NetworkState.Result<*> -> {
                        val response = it.response as QuestionsResponse

                        visProgress( false)

                        when {
                            response.success -> {

                               if (!response.data.feedback_questions.isNullOrEmpty())
                                    adapter.submitData(response.data.feedback_questions as ArrayList<QuestionsResponse.FeedbackQuestion>)

                            }
                            else -> NetworkState.Error(response.code)
                                .handleErrors(mContext)

                        }

                    }
                }
            }
        }
    }

    private fun visProgress(progressState: Boolean) {

        if (progressState) {
            binding.progressBar.visibility = View.VISIBLE
            binding.tvSend.visibility = View.INVISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
            binding.tvSend.visibility = View.VISIBLE
        }

    }

    companion object {
//        private val TAG = this::class.java.name
    }

    private fun setUp() {

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
    }

}