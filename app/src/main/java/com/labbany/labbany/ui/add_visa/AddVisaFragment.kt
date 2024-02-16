package com.labbany.labbany.ui.add_visa

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.library.BuildConfig
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.labbany.labbany.R
import com.labbany.labbany.data.NetworkState
import com.labbany.labbany.databinding.FragmentAddVisaBinding
import com.labbany.labbany.pojo.response.GeneralResponse
import com.labbany.labbany.ui.views.SharedViewModel
import com.labbany.labbany.util.*
import com.squareup.picasso.Picasso
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*


class AddVisaFragment : DialogFragment() {

    private lateinit var binding: FragmentAddVisaBinding
    private lateinit var navController: NavController
    private val shardHelper: ShardHelper by inject()
    private val viewModel: VisasViewModel by inject()
    private val sharedViewModel by sharedViewModel<SharedViewModel>()
    private lateinit var mContext: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_visa, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUp()
        navController = findNavController()
        mContext = requireContext()

        if (BuildConfig.DEBUG) {
            binding.etName.setText("test test")
            binding.etPassword.setText("123")
            binding.etDate.setText("2021-12")
            binding.etNumber.setText("2021-1342-4445-0000")
        }

        binding.etNumber.addTextChangedListener(visaTextWatcher)

        binding.etDate.addTextChangedListener(dateTextWatcher)

        binding.tvAdd.setOnClickListener {

            if (!verify()) return@setOnClickListener

            callApi()

        }

    }

    private fun callApi() {

        viewModel.addVisa(
            shardHelper.id,
            requireArguments().getInt(Constants.VISA_TYPE_ID),
            binding.etName.text.toString(),
            realVisaNum(binding.etNumber.text.toString()),
            binding.etDate.text.toString(),
            binding.etPassword.text.toString()
        )

        lifecycleScope.launchWhenStarted {
            viewModel.addVisaStateFlow.collect {

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

//                      //  Log.e(TAG, "result: response $response")

                        visProgress(progressState = false)

                        when {
                            response.success -> {
                                /*val bundle = Bundle()
                                 bundle.putInt(
                                     Constants.VISA_TYPE_ID,
                                     requireArguments().getInt(Constants.VISA_TYPE_ID)
                                 )

                                 navController.navigate(R.id.visaDetailsFragment, bundle)*/
//                                navController.popBackStack()
                                sharedViewModel.visaAddedStateFlow.value = NetworkState.Result(true)
                                dismiss()

                            }
                            response.code == Constants.Visas.VISA_16_NUM_CODE -> {
                                binding.etNumber.error = getString(R.string.visa_nu_error)
                            }
                            response.code == Constants.Visas.VISA_TYPE_CODE -> {
                                binding.etNumber.error = getString(R.string.visa_type_error)
                            }
                            response.code == Constants.Visas.VISA_FOUNDED_CODE -> {
                                binding.etNumber.error = getString(R.string.visa_founded_error)
                            }
                            else -> NetworkState.Error(response.code)
                                .handleErrors(mContext)

                        }

                    }
                }
            }
        }

    }

    private fun realVisaNum(s: String): String = s.replace("-", "")

    private fun verify(): Boolean {

        var isNull = false

        if (!Utils.validateET(binding.etName, null)) isNull = true
        if (!Utils.validateET(binding.etPassword, null)) isNull = true
        if (!Utils.validateET(binding.etDate, null)) isNull = true

        if (!Utils.validateET(binding.etNumber, null)) isNull = true
        else if (!Utils.validateVisaNumberET(
                binding.etNumber,
                getString(R.string.visa_nu_error)
            )
        ) isNull = true

        if (!Utils.isValidDate(
                binding.etDate,
                getString(R.string.no_valid_date)
            )
        ) isNull = true

        if (!Utils.validateVisaPasswordET(
                binding.etPassword,
                getString(R.string.visa_password)
            )
        ) isNull = true

        return !isNull
    }

    private fun visProgress(progressState: Boolean) {

        if (progressState) {
            binding.tvAdd.visibility = View.INVISIBLE
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.tvAdd.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        }

    }

    private fun setUp() {

        dialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        sharedViewModel.visaAddedStateFlow.value = NetworkState.Idle

        if (arguments != null && requireArguments().containsKey(Constants.IMAGE)) {
            if (!requireArguments().getString(Constants.IMAGE, "").isNullOrEmpty()) {
                val visaTypeImage = requireArguments().getString(Constants.IMAGE, "")
                Picasso.get().load(visaTypeImage).into(binding.imgLogo)
            }
        }

        binding.tv1.text = requireArguments().getString(Constants.VISA_TYPE_NAME, "")

    }

    private val dateTextWatcher = object : TextWatcher {

        private var current = ""
        private val ddDmYyyy = "mmyyyy"
        private val cal = Calendar.getInstance(Locale.ENGLISH)

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (s.toString() != current) {
                var clean = s.toString().replace("[^\\d.]".toRegex(), "")
                val cleanC = current.replace("[^\\d.]".toRegex(), "")
                val cl = clean.length
                var sel = cl
                var i = 2
                while (i <= cl && i < 6) {
                    sel++
                    i += 2
                }
                //Fix for pressing delete next to a forward slash
                if (clean == cleanC) sel--
                if (clean.length < 6) {
                    clean += ddDmYyyy.substring(clean.length)
                } else {
                    //This part makes sure that when we finish entering numbers
                    //the date is correct, fixing it otherwise
                    var mon = clean.substring(0, 2).toInt()
                    var year = clean.substring(2, 6).toInt()
//                    var year = clean.substring(4, 8).toInt()
                    if (mon > 12) mon = 12
                    cal[Calendar.MONTH] = mon - 1
                    year = if (year < 1900 || year > 2100) Utils.currentYear else year
                    cal[Calendar.YEAR] = year
                    // ^ first set year for the line below to work correctly
                    //with leap years - otherwise, date e.g. 29/02/2012
                    //would be automatically corrected to 28/02/2012
//                    day =
//                        if (day > cal.getActualMaximum(Calendar.DATE)) cal.getActualMaximum(Calendar.DATE) else day
                    clean = String.format(Locale.ENGLISH, "%02d%02d", mon, year)
                }
                clean = String.format(
                    Locale.ENGLISH, "%s-%s", clean.substring(0, 2),
                    clean.substring(2, 6)
//                    clean.substring(4, 8)
                )
                sel = if (sel < 0) 0 else sel
                current = clean
                binding.etDate.setText(current)
                binding.etDate.setSelection(if (sel < current.length) sel else current.length)
            }
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(p0: Editable?) {}
    }

    private val visaTextWatcher = object : TextWatcher {

        private var current = ""

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            // noop
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {


        }

        override fun afterTextChanged(s: Editable) { /*noop*/
            if (s.isNotEmpty() && s.toString() != current)
                current = Utils.splitVisa(binding.etNumber)
        }
    }

    companion object {
        private val TAG = this::class.java.name
    }

}