package com.labbany.labbany.util

import `in`.aabhasjindal.otptextview.OtpTextView
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.*
import android.content.Intent.*
import android.content.res.Configuration
import android.content.res.Resources
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.labbany.labbany.R
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.CompositeDateValidator
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.snackbar.Snackbar
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.*
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import com.google.android.material.datepicker.MaterialDatePicker
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.math.BigDecimal


object Utils {

    /*val currentYear: Int
        get() = Calendar.getInstance(Locale("en")).get(Calendar.YEAR)
*/
    private val mCalendar = Calendar.getInstance()
    private val currentTime: Long = mCalendar.timeInMillis
    val currentYear: Int = mCalendar.get(Calendar.YEAR)

    private fun currentTimeMinus1(): Long {
        val cal = mCalendar
        cal.add(Calendar.DATE, -1)

        return cal.timeInMillis
    }

    private fun generateRandomString(len: Int = 10): String {
        val alphanumerics = CharArray(26) { (it + 97).toChar() }.toSet()
            .union(CharArray(9) { (it + 48).toChar() }.toSet())
        return (0 until len).map {
            alphanumerics.toList().random()
        }.joinToString("")
    }

    fun toast(context: Context, msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    fun validateET(mEditText: EditText, msg: String?): Boolean {
        return if (!mEditText.text.isNullOrEmpty()) {
            true
        } else {
            mEditText.error = msg ?: mEditText.hint
            false
        }

    }

    fun validateOtp(otp: OtpTextView): Boolean {
        return if (!otp.otp.isNullOrEmpty() && otp.otp?.length == 4) {
            true
        } else {
            otp.showError()
            false
        }

    }

    fun validatePasswordET(mEditText: EditText, msg: String?): Boolean {
        return if (!mEditText.text.isNullOrEmpty() && mEditText.text.toString().length >= 8) {
            true
        } else {
            mEditText.error = msg ?: mEditText.hint
            false
        }

    }

    fun validateVisaPasswordET(mEditText: EditText, msg: String?): Boolean {
        return if (!mEditText.text.isNullOrEmpty() && mEditText.text.toString().length == 3) {
            true
        } else {
            mEditText.error = msg ?: mEditText.hint
            false
        }

    }

    fun validatePhoneET(mEditText: EditText, msg: String?): Boolean {
        return if (!mEditText.text.isNullOrEmpty() && mEditText.text.toString().length >= 9) {
            true
        } else {
            mEditText.error = msg ?: mEditText.hint
            false
        }

    }

    fun validateEmailET(mEditText: EditText, msg: String?): Boolean {
        return if (!mEditText.text.isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(mEditText.text.toString())
                .matches()
        ) {
            true
        } else {
            mEditText.error = msg ?: mEditText.hint
            false
        }

    }

    fun validateVisaNumberET(mEditText: EditText, msg: String?): Boolean =
        if (!mEditText.text.isNullOrEmpty() && clearVisaNumber(mEditText).length == 16) {
            true
        } else {
            mEditText.error = msg ?: mEditText.hint
            false
        }


    fun validateMatchPassword(mEditText1: EditText, mEditText2: EditText, msg: String?): Boolean {
        return if (!mEditText1.text.isNullOrEmpty() && !mEditText2.text.isNullOrEmpty() && mEditText1.text.toString() == mEditText2.text.toString()) {
            true
        } else {
            mEditText1.error = msg ?: mEditText1.hint
            mEditText2.error = msg ?: mEditText2.hint
            false
        }

    }

    fun isValidDate(mEditText: EditText, msg: String?): Boolean {

        if (mEditText.text.isNullOrEmpty()) return false

        val dateFormat = simpleDateFormat2
        dateFormat.isLenient = false
        try {
            dateFormat.parse(mEditText.text.toString().trim { it <= ' ' })
        } catch (pe: ParseException) {
            mEditText.error = msg ?: mEditText.hint
            return false
        }
        return true
    }

    fun toastInternet(context: Context) {
        Toast.makeText(context, context.getString(R.string.internet_connection), Toast.LENGTH_SHORT)
            .show()
    }

    fun displayVisa(number: String): String {

        val mask = number.replace("\\w(?=\\w{4})".toRegex(), "*")

        var last = ""
        for (i in mask.indices) {
            last += if (i % 4 == 0) {
                "${mask[i]} "
            } else
                "${mask[i]}"
        }


        return last.trim()


    }

    fun clearVisaNumber(mEditText: EditText): String = mEditText.text.toString().replace("-", "")

    fun splitVisa(mEditText: EditText): String {

        if (mEditText.text.isNullOrEmpty()) return ""

        val old = mEditText.text.toString()

        val clear = clearVisaNumber(mEditText)

        var new = ""

        Log.w(TAG, "splitVisa: clear.length ${clear.length}")

        for (i in clear.indices)

            new += if (i % 4 == 0 && i != 0 && i != 15)
                "-${clear[i]}"
            else
                clear[i]


        Log.w(TAG, "splitVisa: new $new")

        if (old != new && old.last() != '-') {
            mEditText.setText(new)
            mEditText.setSelection(new.length)
        }

        return new
    }

    fun decimalFormat(num: Any): String {

        val df = DecimalFormat("#,###.##")
        df.roundingMode = RoundingMode.CEILING

        return df.format(num)
    }

    fun make2FractionalDigits(number: String): Double {
        return String.format(Locale.ENGLISH, "%.2f", number.englishNumbers()).toDouble()
    }

    fun make2FractionalDigits(number: BigDecimal): BigDecimal {
        return number.setScale(2, RoundingMode.UP)
    }

    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
    private val simpleDateFormat2 = SimpleDateFormat("MM-yyyy", Locale.ENGLISH)
    private val fullDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
    private val timeOnlyFormat = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH)

    fun simpleDateFormat(calendar: Calendar): String = simpleDateFormat.format(calendar.time)
    fun fullDateFormat(calendar: Calendar): String = fullDateFormat.format(calendar.time)
    fun fullTimeFormat(): String = timeOnlyFormat.format(currentTime)

    @SuppressLint("ConstantLocale")
    private val simpleDateFullFormat = SimpleDateFormat("E,dd-MMM-yyyy", Locale.getDefault())

    fun simpleDate(d: String): String {

//        val s= simpleDateFullFormat.format(d)

//      //  Log.e(TAG, "simpleDate: ${simpleDateFormat.format(d)}")
        return d//simpleDateFormat.format(d)

    }

    private fun maxDate(amount: Int): Long {

        val a = Calendar.getInstance()
        a.add(Calendar.DATE, amount)

        return a.timeInMillis

    }

    private fun constraintsBuilder(plusAmount: Int): CalendarConstraints.Builder {

        val maxDate = maxDate(plusAmount - 1)

        val constraintsBuilder = CalendarConstraints.Builder()
        val validators: ArrayList<CalendarConstraints.DateValidator> = ArrayList()
        validators.add(DateValidatorPointForward.from(currentTimeMinus1()))
        validators.add(DateValidatorPointBackward.before(maxDate))
        constraintsBuilder.setValidator(CompositeDateValidator.allOf(validators))

        return constraintsBuilder

    }

    fun someDateAtDatePicker(
        mContext: Context,
        m: FragmentManager,
        amount: Int,
        mDateListener: DateListener
    ) {
        val builderRange: MaterialDatePicker.Builder<Long> =
            MaterialDatePicker.Builder.datePicker()


        builderRange.setCalendarConstraints(constraintsBuilder(amount).build())
        builderRange.setTitleText(mContext.getString(R.string.select_order_day))


        val pickerRange = builderRange.build()
        pickerRange.addOnPositiveButtonClickListener {

            val cal = Calendar.getInstance()
            cal.timeInMillis = it

            mDateListener.onFullTimeListener(cal)

        }
        pickerRange.addOnNegativeButtonClickListener {

            mDateListener.onCancel()

        }
        pickerRange.addOnDismissListener {

            mDateListener.onCancel()

        }
        pickerRange.show(m, pickerRange.toString())
    }

    fun datePicker(mContext: Context, mDateListener: DateListener) {

        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(
            mContext, R.style.TimePickerTheme,
            { _, year1, month1, dayOfMonth ->

//                cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
//                cal.set(Calendar.MINUTE, minute)
                cal.set(Calendar.YEAR, year1)
                cal.set(Calendar.MONTH, month1)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                mDateListener.onFullTimeListener(cal)

//                timePicker(mContext, year1, month1, dayOfMonth, mDateListener)

            }, year, month, day
        )


//        dpd.getButton(DatePickerDialog.BUTTON_NEGATIVE)
//            .setTextColor(ContextCompat.getColor(mContext, R.color.yellow_dark))

        dpd.show()

        ContextCompat.getColor(mContext, R.color.yellow_dark).also {
            dpd.getButton(DatePickerDialog.BUTTON_NEGATIVE)
                .setTextColor(it)
            dpd.getButton(DatePickerDialog.BUTTON_POSITIVE)
                .setTextColor(it)
        }

    }

    private fun timePicker(
        mContext: Context,
        year: Int,
        month: Int,
        day: Int,
        mDateListener: DateListener
    ) {

        val cal = Calendar.getInstance()
        val h = cal.get(Calendar.HOUR_OF_DAY)
        val m = cal.get(Calendar.MINUTE)


        val dpd = TimePickerDialog(
            mContext,
            { _, hourOfDay, minute ->

                cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                cal.set(Calendar.MINUTE, minute)
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, month)
                cal.set(Calendar.DAY_OF_MONTH, day)

                mDateListener.onFullTimeListener(cal)


            }, h, m, false
        )

        dpd.setCancelable(false)
        dpd.show()

    }

    fun dateFormat(date: Long): String = simpleDateFullFormat.format(Date(date))

    fun dateFormat(calendar: Calendar): String = simpleDateFullFormat.format(calendar.time)

    fun dateFormat(): String {

        val cal = Calendar.getInstance()

        cal.set(Calendar.YEAR, mCalendar[Calendar.YEAR])
        cal.set(Calendar.MONTH, mCalendar[Calendar.MONTH])

        return ""

    }

    @SuppressLint("QueryPermissionsNeeded")
    fun call(mContext: Context, phone: String) {

        val i = Intent(ACTION_DIAL)
        i.data = Uri.parse("tel:$phone")
        if (i.resolveActivity(mContext.packageManager) != null)
            mContext.startActivity(i)

    }

    fun generalAuth(): String = Constants.API_GENERAL_AUTH


    fun requestBody(txt: String): RequestBody =
        RequestBody.create("multipart/form-data".toMediaTypeOrNull(), txt)

    @SuppressLint("Recycle")
    fun imageBody(mContext: Context, uri: Uri, key: String? = null): MultipartBody.Part {
        val p: String
        val cursor = mContext.contentResolver.query(uri, null, null, null, null)

        p = if (cursor == null) {
            uri.path.toString()
        } else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            cursor.getString(idx)
        }

        val file = File(p)
        val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        return MultipartBody.Part.createFormData(
            if (key.isNullOrEmpty()) "photo" else key,
            file.name,
            requestFile
        )
    }

    @SuppressLint("Recycle")
    fun imageBody(bitmap: Bitmap, key: String? = null): MultipartBody.Part {

        val requestFile =
            RequestBody.create("image/*".toMediaTypeOrNull(), byteArrayFromBitmap(bitmap))
        return MultipartBody.Part.createFormData(
            if (key.isNullOrEmpty()) "photo" else key,
            generateRandomString(10),
            requestFile
        )
    }

    private fun byteArrayFromBitmap(bitmap: Bitmap): ByteArray {

        var imageInByte: ByteArray = byteArrayOf()
        return try {
            val mByteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, mByteArrayOutputStream)
            imageInByte = mByteArrayOutputStream.toByteArray()
            imageInByte
        } catch (e: Exception) {
            imageInByte

        }
    }


//   fun fileRequestBodyFromByteArray(
//        data: ByteArray,
//        apiKey: String,
//        mediaTye: Int
//    ): MultipartBody.Part {
//
//
//        val requestFile = RequestBody.create(
//            MediaType.parse(
//                when (mediaTye) {
//                    11*//* Constants.MediaTypes.PHOTO *//* -> "image/*"
//                    1*//* Constants.MediaTypes.VIDEO *//* -> "video/*"
//                    else -> "image/*"
//                }
//            ), data
//        )
//        return MultipartBody.Part.createFormData(
//            apiKey,
//            currentTime.toString(),
//            requestFile
//        )
//    }


    fun convertVideoToBytes(uri: Uri): ByteArray {
        var videoBytes = byteArrayOf()
        try { //  w  w w  . j ava 2s . c  o m
            val baos = ByteArrayOutputStream()
            val fis = FileInputStream(File(uri.toString()))
            val buf = ByteArray(1024)
            var n: Int
            while (-1 != fis.read(buf).also { n = it }) baos.write(buf, 0, n)
            videoBytes = baos.toByteArray()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return videoBytes
    }


    @SuppressLint("Recycle")
    fun videoBody(mContext: Context, uri: Uri, key: String? = null): MultipartBody.Part {
        val p: String
        val cursor = mContext.contentResolver.query(uri, null, null, null, null)

        p = if (cursor == null) {
            uri.path.toString()
        } else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Video.Media.DATA)
            cursor.getString(idx)
        }

        val file = File(p)
        val requestFile = RequestBody.create("video/*".toMediaTypeOrNull(), file)
        return MultipartBody.Part.createFormData(
            if (key.isNullOrEmpty()) "video" else key,
            file.name,
            requestFile
        )
    }

    fun byteArrayFromImageView(imageView: ImageView): ByteArray {

        var imageInByte: ByteArray = byteArrayOf()
        return try {
            val bitmap = (imageView.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            imageInByte = baos.toByteArray()
            imageInByte
        } catch (e: Exception) {
            imageInByte

        }
    }

    fun refreshCurrentFragment(view: View, current_dest: Int, arguments: Bundle?) {
        view.findNavController().navigate(
            current_dest,
            arguments,
            NavOptions.Builder()
                .setPopUpTo(current_dest, true)
                .build()
        )
    }

    fun showSnackbar(mView: View, txt: String) {

        val snackbar = Snackbar.make(mView, txt, Snackbar.LENGTH_SHORT)

        val snackbarView = snackbar.view

        snackbarView.setBackgroundColor(ContextCompat.getColor(mView.context, R.color.yellow_2))

       /* val snackbarTextView = snackbarView.findViewById(android.R.id.snackbar_text) as TextView

        snackbarTextView.setTextColor(ContextCompat.getColor(mView.context, R.color.white))

        snackbarTextView.text = txt
*/
        snackbar.show()

    }


    @SuppressLint("NewApi")
    fun setLocale(activity: Activity, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources: Resources = activity.resources
        val config: Configuration = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        activity.recreate()
    }

    @SuppressLint("NewApi")
    fun loadLocale(activity: Activity, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources: Resources = activity.resources
        val config: Configuration = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }


    fun startWeb(context: Context, link: String) {

        val intent = Intent(ACTION_VIEW, Uri.parse(link))
        var flags = FLAG_ACTIVITY_NO_HISTORY or FLAG_ACTIVITY_MULTIPLE_TASK
        flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            flags or FLAG_ACTIVITY_NEW_DOCUMENT or FLAG_ACTIVITY_NEW_TASK
        } else {
            flags or FLAG_ACTIVITY_NEW_TASK
        }
        intent.addFlags(flags)
        context.startActivity(intent)

    }

    @SuppressLint("Recycle")
    fun getRealPathFromURI(context: Context, uri: Uri): String? {
        val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
        return if (cursor == null) { // Source is Dropbox or other similar local file path
            uri.path
        } else {
            cursor.moveToFirst()
            val idx: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            cursor.getString(idx)
        }
    }

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

    @SuppressLint("ConstantLocale")
    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    fun getDateOnly(calendar: Calendar): String = dateFormat.format(calendar.time)

    fun getTimeOnly(calendar: Calendar): String = timeFormat.format(calendar.time)

    fun String.englishNumbers(): String =
        this.replace('٠', '0')
            .replace('١', '1')
            .replace('٢', '2')
            .replace('٣', '3')
            .replace('٤', '4')
            .replace('٥', '5')
            .replace('٦', '6')
            .replace('٧', '7')
            .replace('٨', '8')
            .replace('٩', '9')
            .replace('٫', '.')

    fun Double.englishNumbers(): String =
        this.toString().englishNumbers()

    @SuppressLint("InlinedApi")
    fun rateApp(mContext: Context) {

        val uri: Uri = Uri.parse("market://details?id=${mContext.packageName}")
        val goToMarket = Intent(ACTION_VIEW, uri)
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(
            FLAG_ACTIVITY_NO_HISTORY or
                    FLAG_ACTIVITY_NEW_DOCUMENT or
                    FLAG_ACTIVITY_MULTIPLE_TASK
        )
        try {
            mContext.startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            mContext.startActivity(
                Intent(
                    ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=${mContext.packageName}")
                )
            )
        }

    }

    /*NavOptions.Builder navBuilder =  new NavOptions.Builder();
        navBuilder.setEnterAnim(R.anim.slide_left).setExitAnim(R.anim.slide_right).setPopEnterAnim(R.anim.slide_left).setPopExitAnim(R.anim.slide_right);

        //Inside Activity
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        navController.navigate(R.id.destinationFragmentId,null,navBuilder.build());
        //Inside Fragment

        NavHostFragment.findNavController(YoutFragment.this)
                            .navigate(R.id.destinationFragmentId, null, navBuilder.build());*/


    fun copyTextToClipBoard(mContext: Context, txt: String) {
        val clipboard = mContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label", txt)
        clipboard.setPrimaryClip(clip)
    }

    private const val TAG = "UtilReference"

}