package com.labbany.labbany.ui.auth.login

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.labbany.labbany.data.NetworkState
import com.labbany.labbany.databinding.FragmentLoginBinding
import com.labbany.labbany.pojo.FacebookTest
import com.labbany.labbany.pojo.response.CheckCodeResponse
import com.labbany.labbany.pojo.response.LoginResponse
import com.labbany.labbany.util.*
import com.facebook.*
import com.facebook.FacebookSdk.getApplicationContext
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.gson.Gson
import com.labbany.labbany.R
import org.json.JSONException
import org.koin.android.ext.android.inject


class LoginFragment : Fragment() {

    private lateinit var navController: NavController
    private lateinit var binding: FragmentLoginBinding
    private val shardHelper: ShardHelper by inject()
    private val viewModel: LoginViewModel by inject()
    private lateinit var mContext: Context

    private lateinit var mGoogleSignInClient: GoogleSignInClient

    private lateinit var callbackManager: CallbackManager

    private var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .build()

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data

                val task: Task<GoogleSignInAccount> =
                    GoogleSignIn.getSignedInAccountFromIntent(data)

                handleGoogleSignInResult(task)

            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(binding.root)
        mContext=requireContext()

        FacebookSdk.sdkInitialize(getApplicationContext())

        callbackManager = CallbackManager.Factory.create()

        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        onBack()

        /*  if (BuildConfig.DEBUG) {
              binding.etPhone.setText("500081832")
              binding.etPassword.setText("123456789")
          }
  */

        binding.tvLogin.setOnClickListener {

            if (!verify()) return@setOnClickListener

            callApi()

        }


        binding.tvForgetPassword.setOnClickListener {

            val bundle = Bundle()

            bundle.putString(Constants.ACTION, Constants.FORGET_PASSWORD)

            navController.navigate(R.id.forgetPasswordFragment, bundle)

        }

        binding.tvSignUp.setOnClickListener { navController.navigate(R.id.signUpFragment) }

        binding.imgGoogle.setOnClickListener { signInWithGoogle() }

        binding.imgFacebook.setOnClickListener {

          //  Log.e(TAG, "facebook code : 1")
            LoginManager.getInstance()
                .logInWithReadPermissions(this, listOf("email", "public_profile", "user_friends"))

          //  Log.e(TAG, "facebook code : 2")

            LoginManager.getInstance().registerCallback(callbackManager,
                object : FacebookCallback<LoginResult?> {
                    override fun onSuccess(loginResult: LoginResult?) {
                        // App code
                      //  Log.e(TAG, "facebook code onSuccess: 1")
                        getFacebookUserProfile(loginResult!!.accessToken)
                    }

                    override fun onCancel() {
                        // App code
                      //  Log.e(TAG, "facebook code onCancel: 1")
                    }

                    override fun onError(exception: FacebookException) {
                        // App code
//                      //  Log.e(TAG, "facebook code onError: ${exception.message} \n $exception")
                    }
                })

        }

    }

    private fun getFacebookUserProfile(currentAccessToken: AccessToken) {
        val request = GraphRequest.newMeRequest(
            currentAccessToken
        ) { object1, _ ->
            try {
                //You can fetch user info like this…
                //object.getJSONObject(“picture”).
                //object.getString(“name”);
                //object.getString(“email”));

              //  Log.e(TAG, "getUserProfile: true")
//              //  Log.e(TAG, "getUserProfile: data $object1")

                val response: FacebookTest =
                    Gson().fromJson(object1.toString(), FacebookTest::class.java)

                socialAuth(response.name, response.email, response.picture.data.url)

            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        val parameters = Bundle()
        parameters.putString("fields", "id,name,email,picture.type(normal)")
        request.parameters = parameters
        request.executeAsync()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
//        super.onActivityResult(requestCode, resultCode, data)
      //  Log.e(TAG, "onActivityResult: facebook code 1")
    }

    private fun signInWithGoogle() {

        val signInIntent = mGoogleSignInClient.signInIntent
        resultLauncher.launch(signInIntent)

    }

    private fun handleGoogleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.

          //  Log.e(TAG, "handleSignInResult: true")
//          //  Log.e(TAG, "handleSignInResult: true a1 ${account.photoUrl}")

            socialAuth(account.displayName!!, account.email!!, account.photoUrl?.toString())
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.

          //  Log.e(TAG, "handleSignInResult: false")

//          //  Log.e(TAG, "signInResult:failed code=" + e.statusCode)
//            socialAuth(null)
        }
    }

    private fun socialAuth(name: String, email: String, photoUrl: String?) {

        viewModel.login(
            email,
            null,
            Constants.AuthTypes.SOCIAL
        )

        lifecycleScope.launchWhenStarted {
            viewModel.socialLoginStateFlow.collect {

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
                        val response = it.response as CheckCodeResponse

                        visProgress(false)

                        when {
                            response.success -> {
//                              //  Log.e(TAG, "result: done ${response.data}")

                                shardHelper.saveData(response.data!!)
                                navController.navigate(R.id.homeFragment)

                            }
                            response.code == Constants.Auth.PHONE_CODE || response.code == Constants.Auth.EMAIL_CODE -> {

                                val bundle = Bundle()
                                bundle.putString(Constants.PHOTO_URL, photoUrl)
                                bundle.putString(Constants.EMAIL, email)
                                bundle.putString(Constants.NAME, name)

                                navController.navigate(R.id.signUpFragment, bundle)
                            }
                            else -> NetworkState.Error(response.code)
                                .handleErrors(mContext)

                        }

                    }
                }
            }
        }

    }


    private fun onBack() {

        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    navController.navigate(R.id.splashFragment)
                }
            })

    }

    private fun callApi() {

        viewModel.login(
            binding.etPhone.text.toString(),
            binding.etPassword.text.toString(),
            Constants.AuthTypes.NORMAL
        )

        lifecycleScope.launchWhenStarted {
            viewModel.normalLoginStateFlow.collect {

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
                        val response = it.response as LoginResponse

                        visProgress(false)

                        when {
                            response.success -> {

//                              //  Log.e(TAG, "result: done ${response.data}")

                                shardHelper.saveData(response.data!!)
                                navController.navigate(R.id.homeFragment)

                            }
                            response.code == Constants.Auth.PHONE_CODE -> {
                                binding.etPhone.error = getString(R.string.error_phone)
                            }
                            response.code == Constants.Auth.EMAIL_CODE -> {
                                binding.etPhone.error = getString(R.string.error_email)
                            }
                            response.code == Constants.Auth.PASSWORD_CODE -> {
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

    private fun verify(): Boolean {

        return Utils.validateET(binding.etPhone, null) &&
                Utils.validateET(binding.etPassword, null)
    }

    private fun visProgress(s: Boolean) {

        if (s) {
            binding.incProgress.spinProgress.visibility = View.VISIBLE
            binding.tvLogin.visibility = View.INVISIBLE
            binding.imgFacebook.isEnabled = false
            binding.imgGoogle.isEnabled = false
        } else {
            binding.incProgress.spinProgress.visibility = View.GONE
            binding.tvLogin.visibility = View.VISIBLE
            binding.imgFacebook.isEnabled = true
            binding.imgGoogle.isEnabled = true
        }

    }

    companion object {
        private val TAG = this::class.java.name
    }


}