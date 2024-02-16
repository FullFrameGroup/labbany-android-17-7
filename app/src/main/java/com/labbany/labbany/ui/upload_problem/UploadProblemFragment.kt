package com.labbany.labbany.ui.upload_problem

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
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
import com.labbany.labbany.databinding.FragmentUploadProblemBinding
import com.labbany.labbany.pojo.response.ComplaintTypesResponse
import com.labbany.labbany.pojo.response.GeneralResponse
import com.labbany.labbany.ui.complaints.ComplaintsViewModel
import com.labbany.labbany.ui.help.HelperDialog
import com.labbany.labbany.util.DialogsListener
import com.labbany.labbany.util.ShardHelper
import com.labbany.labbany.util.Utils
import org.koin.android.ext.android.inject

class UploadProblemFragment : DialogFragment() {

    private lateinit var binding: FragmentUploadProblemBinding
    private val viewModel: ComplaintsViewModel by inject()
    private val shardHelper: ShardHelper by inject()
    private lateinit var navController: NavController
    private lateinit var typesAdapter: TypesAdapter
    private lateinit var mContext: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_upload_problem, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUp()

        mContext = requireContext()
        navController = findNavController()
        typesAdapter = TypesAdapter()

        binding.spTypes.adapter = typesAdapter

        types()

        binding.tvSend.setOnClickListener {

            if (!verify()) return@setOnClickListener

            uploadComplaint()

        }

    }

    private fun setUp() {

        this.view?.setBackgroundColor(Color.TRANSPARENT)
    }

    private fun types() {

        viewModel.complaintTypes()

        lifecycleScope.launchWhenStarted {
            viewModel.complaintTypesStateFlow.collect {

                when (it) {
                    is NetworkState.Idle -> {
                        return@collect
                    }
                    is NetworkState.Loading -> {
                        visProgress(true)
                    }
                    is NetworkState.Error -> {
                        visProgress(false)
                        it.handleErrors(mContext, null)
                    }
                    is NetworkState.Result<*> -> {
                        val response = it.response as ComplaintTypesResponse

                        visProgress(false)

                        when {
                            response.success -> {

                                if (response.data != null && !response.data.complaint_type.isNullOrEmpty()) {
                                    typesAdapter.submitData(response.data.complaint_type as ArrayList<ComplaintTypesResponse.ComplaintType>)
                                }
                            }
                            else -> NetworkState.Error(response.code)
                                .handleErrors(mContext)

                        }

                    }
                }
            }
        }

    }

    private fun uploadComplaint() {

        viewModel.uploadComplaint(
            userId = shardHelper.id,
            name = binding.etName.text.toString(),
            phone = binding.etPhone.text.toString(),
            complaintTypeId = typesAdapter.getItem(binding.spTypes.selectedItemPosition).complaint_type_id,
            description = binding.etNotes.text.toString(),
        )

        lifecycleScope.launchWhenStarted {
            viewModel.uploadComplaintStateFlow.collect {

                when (it) {
                    is NetworkState.Idle -> {
                        return@collect
                    }
                    is NetworkState.Loading -> {
                        visProgress(true)
                    }
                    is NetworkState.Error -> {
                        visProgress(false)
                        it.handleErrors(mContext, null)
                    }
                    is NetworkState.Result<*> -> {
                        val response = it.response as GeneralResponse

                        visProgress(false)

                        when {
                            response.success -> {

                                val mDialogsListener = object : DialogsListener {
                                    override fun onDismiss() {

                                        navController.popBackStack()
                                    }
                                }

                                HelperDialog(
                                    getString(R.string.complaint_sent_succ),
                                    mDialogsListener
                                ).show(parentFragmentManager, "")

                            }
                            else -> NetworkState.Error(response.code)
                                .handleErrors(mContext)

                        }

                    }
                }
            }
        }
    }

    private fun visProgress(s: Boolean) {

        if (s) {
            binding.incProgress.spinProgress.visibility = View.VISIBLE
            binding.tvSend.visibility = View.INVISIBLE
        } else {
            binding.incProgress.spinProgress.visibility = View.GONE
            binding.tvSend.visibility = View.VISIBLE
        }

    }

    private fun verify(): Boolean {

        var isNull = false

        if (!Utils.validateET(binding.etPhone, getString(R.string.enter_phone))) isNull = true
        if (!Utils.validateET(binding.etName, getString(R.string.user_name))) isNull = true
        if (!Utils.validateET(binding.etNotes, getString(R.string.note_here))) isNull = true

        if (binding.spTypes.selectedItemPosition == -1)
            Utils.toast(requireContext(), getString(R.string.should_select_problem_type))

      //  Log.e(TAG, "verify: isNull $isNull")

        return !isNull
    }


    override fun onStart() {
        super.onStart()
        setWindowParams()
    }

    private fun setWindowParams() {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
    }

    companion object {
        private val TAG = this::class.java.name
    }


}