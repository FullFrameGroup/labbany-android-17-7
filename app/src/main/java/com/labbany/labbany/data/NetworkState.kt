package com.labbany.labbany.data

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.labbany.labbany.R
import com.labbany.labbany.ui.help.HelperDialog
import com.labbany.labbany.util.Constants
import com.labbany.labbany.util.DialogsListener

sealed class NetworkState {

    //idle
    object Idle : NetworkState()

    //loading
    object Loading : NetworkState()

    //result
    data class Result<T>(var response: T) : NetworkState()

    //error
    data class Error(var errorCode: Int, var msg: String? = null) : NetworkState() {

        fun handleErrors(
            mContext: Context,
            mDialogsListener: DialogsListener? = null
        ) {

            val mFragmentManager: FragmentManager =
                (mContext as AppCompatActivity).supportFragmentManager

            Log.e(TAG, "handleErrors: msg $msg")
            Log.e(TAG, "handleErrors: error code $errorCode")

            when (errorCode) {

                Constants.Codes.EXCEPTIONS_CODE -> {
                    showHelperDialog(
                        msg = if (msg.isNullOrEmpty()) mContext.getString(R.string.internet_connection) else msg!!,
                        mFragmentManager = mFragmentManager,
                        mDialogsListener = mDialogsListener
                    )
                }
                Constants.Codes.X_API_KEY_CODE -> {
                    showHelperDialog(
                        msg = if (msg.isNullOrEmpty()) mContext.getString(R.string.x_api_key_error) else msg!!,
                        mFragmentManager = mFragmentManager,
                        mDialogsListener = mDialogsListener
                    )
                }
                Constants.Codes.AUTH_CODE -> {
                    showHelperDialog(
                        msg = if (msg.isNullOrEmpty()) mContext.getString(R.string.auth_error) else msg!!,
                        mFragmentManager = mFragmentManager,
                        mDialogsListener = mDialogsListener
                    )
                }
                Constants.Codes.UNKNOWN_CODE -> {

                    showHelperDialog(
                        msg = /*if (msg.isNullOrEmpty())*/ mContext.getString(R.string.known_error) /*else msg*/,
                        mFragmentManager = mFragmentManager,
                        mDialogsListener = mDialogsListener
                    )

                }
                else -> {
                    showHelperDialog(
                        msg = if (msg.isNullOrEmpty()) mContext.getString(R.string.known_error) else msg!!,
                        mFragmentManager = mFragmentManager,
                        mDialogsListener = mDialogsListener
                    )

                }
            }

        }

        private fun showHelperDialog(
            msg: String,
            mFragmentManager: FragmentManager,
            mDialogsListener: DialogsListener?
        ) {

            HelperDialog(
                msg = msg,
                mDialogsListener = mDialogsListener
            ).show(mFragmentManager, "")

        }

        companion object {
            private val TAG = this::class.java.name
        }

    }

}
