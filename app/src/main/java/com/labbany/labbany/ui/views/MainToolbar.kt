package com.labbany.labbany.ui.views

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.labbany.labbany.databinding.TbMainBinding
import com.labbany.labbany.pojo.model.CityModel
import com.labbany.labbany.ui.basket.BasketActivity
import com.labbany.labbany.ui.my_city.CitiesDialog
import com.labbany.labbany.util.DialogsListener
import com.labbany.labbany.util.ShardHelper
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainToolbar(
    private val binding: TbMainBinding,
    private val mContext: Context,
    private val subDialogsListener: DialogsListener?,
    private val basketLauncher: ActivityResultLauncher<Intent>
) :
    KoinComponent {

    private val shardHelper: ShardHelper by inject()

    init {
        binding.icCity.text = shardHelper.cityName

      //  Log.e(TAG, "init: 1")

        binding.icCity.setOnClickListener {
          //  Log.e(TAG, "icCity.setOnClickListener: 1")

            val dialogsListener = object : DialogsListener {
                override fun <T> onDismiss(data: T) {

                    data as CityModel

                    binding.icCity.text = data.city_name_arabic
                }
            }

            CitiesDialog(dialogsListener, subDialogsListener).show(
                (binding.root.context as AppCompatActivity).supportFragmentManager,
                ""
            )
        }
        binding.imgShoppingCart.setOnClickListener {
          //  Log.e(TAG, "imgShoppingCart.setOnClickListener: 1")
            basketLauncher.launch(Intent(mContext, BasketActivity::class.java))
        }
    }

    companion object {
        private val TAG = this::class.java.name
    }

}