package com.labbany.labbany.di

import com.labbany.labbany.ui.add_visa.VisasViewModel
import com.labbany.labbany.ui.addresses.AddressViewModel
import com.labbany.labbany.ui.auth.change_password.ChangePasswordViewModel
import com.labbany.labbany.ui.auth.login.LoginViewModel
import com.labbany.labbany.ui.auth.logout.LogoutViewModel
import com.labbany.labbany.ui.auth.otp.OtpViewModel
import com.labbany.labbany.ui.auth.phone.PhoneRegistrationViewModel
import com.labbany.labbany.ui.auth.reset_password.RestPasswordViewModel
import com.labbany.labbany.ui.auth.signup.SignUpViewModel
import com.labbany.labbany.ui.basket.BasketViewModel
import com.labbany.labbany.ui.basket.delete_cart_item.DeleteCartItemViewModel
import com.labbany.labbany.ui.basket.times.TimesViewModel
import com.labbany.labbany.ui.complaints.ComplaintsViewModel
import com.labbany.labbany.ui.coupons.CouponExchangeViewModel
import com.labbany.labbany.ui.home.HomeViewModel
import com.labbany.labbany.ui.my_city.CitiesViewModel
import com.labbany.labbany.ui.my_orders.MyOrdersViewModel
import com.labbany.labbany.ui.notifications.NotificationsViewModel
import com.labbany.labbany.ui.order_details.OrderDetailsViewModel
import com.labbany.labbany.ui.product_details.ProductDetailsViewModel
import com.labbany.labbany.ui.profile.ProfileViewModel
import com.labbany.labbany.ui.rate_order.RateOrderViewModel
import com.labbany.labbany.ui.terms_and_conditions.TermsViewModel
import com.labbany.labbany.ui.views.SharedViewModel
import com.labbany.labbany.ui.wallet.WalletViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object ViewModelsModules {

    val modules = module {

        viewModel { HomeViewModel(get()) }
        viewModel { NotificationsViewModel(get()) }
        viewModel { WalletViewModel(get()) }
        viewModel { CitiesViewModel(get()) }
        viewModel { PhoneRegistrationViewModel(get()) }
        viewModel { BasketViewModel(get(),get()) }
//        viewModel { PaymentViewModel() }
        viewModel { ComplaintsViewModel(get()) }
        viewModel { AddressViewModel(get()) }
        viewModel { LoginViewModel(get()) }
        viewModel { OtpViewModel(get()) }
        viewModel { RestPasswordViewModel(get()) }
        viewModel { ChangePasswordViewModel(get()) }
        viewModel { ProfileViewModel(get()) }
        viewModel { LogoutViewModel(get()) }
        viewModel { TermsViewModel(get()) }
        viewModel { SignUpViewModel(get()) }
        viewModel { ProductDetailsViewModel(get()) }
        viewModel { VisasViewModel(get()) }
        viewModel { RateOrderViewModel(get()) }
        viewModel { CouponExchangeViewModel(get()) }
        viewModel { OrderDetailsViewModel(get()) }
        viewModel { MyOrdersViewModel(get()) }
        viewModel { DeleteCartItemViewModel(get()) }
        viewModel { SharedViewModel() }
        viewModel { TimesViewModel(get()) }

    }

}