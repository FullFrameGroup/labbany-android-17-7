package com.labbany.labbany.ui.auth.edit_profile

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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
import com.labbany.labbany.databinding.FragmentEditProfileBinding
import com.labbany.labbany.pojo.model.UserModelBySerializable
import com.labbany.labbany.pojo.response.ProfileResponse
import com.labbany.labbany.ui.help.HelperDialog
import com.labbany.labbany.ui.profile.ProfileViewModel
import com.labbany.labbany.util.*
import com.squareup.picasso.Picasso
import org.koin.android.ext.android.inject

class EditProfileFragment : Fragment() {

    private lateinit var binding: FragmentEditProfileBinding
    private lateinit var navController: NavController
    private val viewModel: ProfileViewModel by inject()
    private val shardHelper: ShardHelper by inject()
    private var imageUri: Uri? = null
    private lateinit var mContext: Context

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

            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_edit_profile, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(binding.root)
        mContext=requireContext()

        fullUI()

        binding.tb.imgBack.setOnClickListener { navController.popBackStack() }

        binding.tvSignUp.setOnClickListener {


            if (!verify()) return@setOnClickListener

            callApi()

        }

        binding.civUser.setOnClickListener { checkPermissions() }
        binding.imgAddImage.setOnClickListener { checkPermissions() }

        binding.tvTerms.setOnClickListener { navController.navigate(R.id.termsAndConditionsFragment) }

    }

    private fun callApi() {


        viewModel.editProfile(
            requireContext(),
            shardHelper.id,
            binding.etName.text.toString(),
            binding.etEmail.text.toString(),
            imageUri
        )

        lifecycleScope.launchWhenStarted {
            viewModel.editProfileStateFlow.collect {

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
                        val response = it.response as ProfileResponse

                        visProgress( false)

                        when {
                            response.success -> {
//                              //  Log.e(TAG, "result: done ${response.data}")

                                shardHelper.saveData(response.data!!)

                                navController.navigate(R.id.profileFragment)
                            }
                            response.code== Constants.Auth.EMAIL_CODE -> {
                                binding.etEmail.error = getString(R.string.email_exist)
                            }
                            response.code== Constants.Auth.PASSWORD_CODE -> {
                                binding.etPassword.error = getString(R.string.error_password)

                            }  else -> NetworkState.Error(response.code)
                            .handleErrors(mContext)

                        }

                    }
                }
            }
        }
    }

    private fun fullUI() {

        val data: UserModelBySerializable =
            requireArguments().getSerializable(Constants.DATA) as UserModelBySerializable

        binding.etName.setText(data.username)
        binding.etEmail.setText(data.email)

        if (!data.image.isNullOrEmpty())
            Picasso.get().load(data.image).into(binding.civUser)

    }

  private  fun imageResultLauncher() {

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
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
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

        return Utils.validateET(binding.etEmail, null) &&
                Utils.validateET(binding.etName, null) &&
                Utils.validateEmailET(binding.etEmail, getString(R.string.not_email))

    }

    companion object {
        private val TAG = this::class.java.name
    }


}