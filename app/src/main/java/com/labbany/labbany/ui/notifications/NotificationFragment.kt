package com.labbany.labbany.ui.notifications

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.labbany.labbany.R
import com.labbany.labbany.data.NetworkState
import com.labbany.labbany.databinding.FragmentNotificationBinding
import com.labbany.labbany.pojo.response.NotificationsResponse
import com.labbany.labbany.ui.MainActivity
import com.labbany.labbany.ui.views.MainToolbar
import com.labbany.labbany.util.*
import org.koin.android.ext.android.inject

class NotificationFragment : Fragment(), RecyclerViewOnClickListener {

    private lateinit var binding: FragmentNotificationBinding
    private val viewModel: NotificationsViewModel by inject()
    private val shardHelper: ShardHelper by inject()
    private lateinit var notificationsAdapter: NotificationsAdapter
    private lateinit var navController: NavController
    private lateinit var mContext: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_notification, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(requireView())
        mContext = requireContext()
        notificationsAdapter = NotificationsAdapter()

        if (!auth()) return

        binding.rvNotifications.adapter = notificationsAdapter

        MainToolbar(
            binding.tb,
            mContext,
            null,
            (requireActivity() as MainActivity).basketLauncher
        )


        notifications()

    }

    private fun auth(): Boolean = if (shardHelper.isLogged())
        true
    else {
        navController.navigate(R.id.notAuthDialog)
        false
    }

    private fun notifications() {

        viewModel.notifications(shardHelper.id)

        lifecycleScope.launchWhenStarted {
            viewModel.notificationsStateFlow.collect {

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
                        val response = it.response as NotificationsResponse

                        visProgress(false)

                        when {
                            response.success -> {
                                if (response.data == null || response.data.notification.isNullOrEmpty()) {
                                    binding.tvEmptyNotifications.visibility = View.VISIBLE
                                } else {
                                    binding.tvEmptyNotifications.visibility = View.GONE
                                    notificationsAdapter.submitData(response.data.notification)

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

    private fun visProgress(s: Boolean) {

        if (s) {
            binding.incProgress.spinProgress.visibility = View.VISIBLE
        } else {
            binding.incProgress.spinProgress.visibility = View.GONE
        }

    }


    companion object {
//        private val TAG = this::class.java.name
    }

}