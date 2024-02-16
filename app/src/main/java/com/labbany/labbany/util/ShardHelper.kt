package com.labbany.labbany.util

import android.content.Context
import android.util.Log
import com.labbany.labbany.pojo.model.CityModel
import com.labbany.labbany.pojo.model.UserModel

class ShardHelper(mContext: Context) {

    private val sharedPreferences =
        mContext.getSharedPreferences(Constants.ShardKeys.SHARD_NAME, Context.MODE_PRIVATE)
    private val editor = sharedPreferences.edit()

    /*fun getAccessTokenAuth(): String = "${
        sharedPreferences.getString(
            Constants.ShardKeys.TOKEN_TYPE,
            "bearer"
        )
    } ${sharedPreferences.getString(Constants.ShardKeys.ACCESS_TOKEN, "")}"
*/
    fun isLogged(): Boolean =
        sharedPreferences.getInt(Constants.ShardKeys.USER_ID, 0) != 0

    val id: Int
        get() =
            sharedPreferences.getInt(Constants.ShardKeys.USER_ID, 0)


    fun setCityData(data: CityModel) {

        editor.apply {
            putInt(Constants.ShardKeys.CITY_ID, data.city_id)
            putString(Constants.ShardKeys.CITY_NAME, data.city_name_arabic)
            putString(Constants.ShardKeys.CITY_NAME, data.city_name_arabic)
        }.apply()

    }

    val cityId: Int
        get() =
            sharedPreferences.getInt(Constants.ShardKeys.CITY_ID, 0)

    val cityName: String
        get() =
            sharedPreferences.getString(Constants.ShardKeys.CITY_NAME, "")!!

    val cityNameEn: String
        get() =
            sharedPreferences.getString(Constants.ShardKeys.CITY_NAME_EN, "")!!

    val userName: String
        get() =
            sharedPreferences.getString(Constants.ShardKeys.NAME, "")!!

    val email: String
        get() =
            sharedPreferences.getString(Constants.ShardKeys.EMAIL, "")!!

    fun saveData(user: UserModel) {

        editor.apply {
            putString(Constants.ShardKeys.NAME, user.username)
            putString(Constants.ShardKeys.MOBILE, user.phone)
            putString(Constants.ShardKeys.EMAIL, user.email)
            putString(Constants.ShardKeys.IMAGE_URL, user.image ?: "")
            putInt(Constants.ShardKeys.USER_ID, user.id)
            putInt(Constants.ShardKeys.CITY_ID, user.city_id)
            putString(Constants.ShardKeys.FCM_TOKEN, user.fcm_token ?: "")
            putString(Constants.ShardKeys.CITY_NAME, user.city_name)
        }
        editor.apply()

    }

    fun logOut() {
      //  Log.e(TAG, "logOut: 1")
        editor.clear()!!.apply()
      //  Log.e(TAG, "logOut: 2")
    }

    companion object {
        private val TAG = this::class.java.name
    }

}