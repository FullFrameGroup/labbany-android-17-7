package com.labbany.labbany.ui.auth.signup

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.labbany.labbany.R
import com.labbany.labbany.data.NetworkState
import com.labbany.labbany.databinding.FragmentSignUpBinding
import com.labbany.labbany.pojo.response.RegistrationResponse
import com.labbany.labbany.ui.help.HelperDialog
import com.labbany.labbany.util.Constants
import com.labbany.labbany.util.Utils
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import org.koin.android.ext.android.inject

class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding
    private lateinit var navController: NavController
    private val viewModel: SignUpViewModel by inject()
    private lateinit var mContext: Context
    private var imageUri: Uri? = null
    private var imageBitmap: Bitmap? = null

    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->

            if (permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true) {
                imageResultLauncher()
            } else {
                HelperDialog(getString(R.string.image_per), null).show(
                    parentFragmentManager,
                    ""
                )
            }
        }


    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data

                binding.civUser.setImageURI(data?.data!!)
                imageUri = data.data!!

                if (imageBitmap != null) imageBitmap = null

            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        navController = Navigation.findNavController(binding.root)
        mContext=requireContext()

        binding.tb.imgBack.setOnClickListener { navController.popBackStack() }

        checkSocialAuth()

        binding.tvSignUp.setOnClickListener {


            if (!verify()) return@setOnClickListener

            signUp()

        }

        binding.civUser.setOnClickListener { checkPermissions() }
        binding.imgAddImage.setOnClickListener { checkPermissions() }

        binding.tvOr.setOnClickListener { navController.navigate(R.id.termsAndConditionsFragment) }

    }

    private fun signUp() {

        viewModel.signUP(
            requireContext(),
            binding.etName.text.toString(),
            binding.etPhone.text.toString(),
            binding.etPassword.text.toString(),
            imageUri, imageBitmap
        )

        lifecycleScope.launchWhenStarted {
            viewModel.signUpStateFlow.collect {

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
                        val response = it.response as RegistrationResponse

                        visProgress( false)

                        when {
                            response.success -> {
                              //  Log.e(TAG, "result: done ${response.data}")

                                val bundle = Bundle()

                                bundle.putInt(Constants.USER_ID, response.data.id)

                                navController.navigate(R.id.phoneRegisterationFragment, bundle)

                            }
                            response.code== Constants.Auth.EMAIL_CODE -> {
                                binding.etPhone.error = getString(R.string.email_exist)
                            }
                            response.code== Constants.Auth.PASSWORD_CODE -> {
                                binding.etPassword.error = getString(R.string.error_password)

                            }
                            else -> NetworkState.Error(response.code)
                                .handleErrors(mContext)

                        }

                    }
                }
            }
        }
    }

    private fun checkSocialAuth() {

        if (arguments != null) {

            if (requireArguments().containsKey(Constants.NAME) && !requireArguments().getString(
                    Constants.NAME
                ).isNullOrEmpty()
            ) {
                binding.etName.setText(requireArguments().getString(Constants.NAME))
            }

            if (requireArguments().containsKey(Constants.EMAIL) && !requireArguments().getString(
                    Constants.EMAIL
                ).isNullOrEmpty()
            ) {

                binding.etPhone.isEnabled = false
                binding.etPhone.setText(requireArguments().getString(Constants.EMAIL))
            }

            if (requireArguments().containsKey(Constants.PHOTO_URL) && !requireArguments().getString(
                    Constants.PHOTO_URL
                ).isNullOrEmpty()
            ) {

                Picasso.get().load(requireArguments().getString(Constants.PHOTO_URL))
                    .into(object : Target {
                        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                            binding.civUser.setImageBitmap(bitmap)
                            imageBitmap =bitmap
                        }

                        override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {}

                        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
                    })

            }

        }

    }

    private fun imageResultLauncher() {

        val intent = Intent(
            Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        resultLauncher.launch(intent)
    }


    private fun checkPermissions() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                Log.d(TAG, "Request Permissions")
                requestMultiplePermissions.launch(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                )
            } else {
                Log.d(TAG, "Permission Already Granted")
                imageResultLauncher()
            }
        } else {
            imageResultLauncher()

        }
    }


    private fun visProgress(s: Boolean) {

        if (s) {
            binding.incProgress.spinProgress.visibility = View.VISIBLE
            binding.tvSignUp.visibility = View.INVISIBLE
        } else {
            binding.incProgress.spinProgress.visibility = View.GONE
            binding.tvSignUp.visibility = View.VISIBLE
        }

    }

    private fun verify(): Boolean {

        var isNull = false

        if (!Utils.validateET(binding.etPhone, null)) isNull = true
        if (!Utils.validateET(binding.etPassword, null)) isNull = true
        if (!Utils.validateET(binding.etConfirmPassword, null)) isNull = true
        if (!Utils.validateET(binding.etName, null)) isNull = true
        if (!Utils.validatePasswordET(
                binding.etPassword,
                getString(R.string.password_should_be_g_then_8)
            )
        ) isNull = true
        if (!Utils.validatePasswordET(
                binding.etConfirmPassword,
                getString(R.string.password_should_be_g_then_8)
            )
        ) isNull = true
        if (!Utils.validateEmailET(binding.etPhone, getString(R.string.not_email))) isNull = true
        if (!Utils.validateMatchPassword(
                binding.etPassword,
                binding.etConfirmPassword,
                getString(R.string.password_not_matcher)
            )
        ) isNull = true

      //  Log.e(TAG, "verify: isNull $isNull")

        return !isNull
    }

    companion object {
        private val TAG = this::class.java.name
    }


}