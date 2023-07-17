package com.labbany.labbany.ui.select_location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import com.labbany.labbany.databinding.ActivitySelectLocationBinding
import com.labbany.labbany.pojo.model.SimpleLocationModel
import com.labbany.labbany.ui.help.HelperDialog
import com.labbany.labbany.util.Constants
import com.labbany.labbany.util.Utils
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import java.io.Serializable
import java.util.*
import com.labbany.labbany.R

class SelectLocationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivitySelectLocationBinding
    private lateinit var mMap: GoogleMap
    private var myLocation: LatLng? = null
    private var myLatLng: LatLng? = null
    private lateinit var geocode: Geocoder
    private lateinit var mFragmentManager: FragmentManager

    /*  private val bicycleIcon: BitmapDescriptor by lazy {
          val color = ContextCompat.getColor(this, R.color.yellow_dark)
          BitmapHelper.vectorToBitmap(this, R.drawable.ic_pointer_yellow_dark, color)
      }
  */
    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->

            if (permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
                goToMyLocation()
            } else {
                HelperDialog(getString(R.string.location_per), null).show(
                    mFragmentManager,
                    ""
                )
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        try {

        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_location)
        mFragmentManager = supportFragmentManager

        geocode = Geocoder(applicationContext, Locale("ar"))//.getDefault())

        val mapFragment = //(context!! as AppCompatActivity).supportFragmentManager
            mFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(this)


        binding.tvAdd.setOnClickListener {

            if (myLocation == null) {
                Utils.toast(
                    applicationContext,
                    getString(R.string.select_location_first)
                )
                return@setOnClickListener
            }

            val addresses: List<Address>? =
                geocode.getFromLocation(myLocation!!.latitude, myLocation!!.longitude, 1)

            if (addresses != null && addresses.isNotEmpty()) {

                val address = addresses[0]

                Log.e(TAG, "onCreate: address ${addresses[0]}")

                val locationName =
//                    address.adminArea + " " + address.subAdminArea + " " + address.thoroughfare
                    addresses[0].getAddressLine(0)

                val location = SimpleLocationModel(
                    district_name = address.locality,
                    latitude = myLocation!!.latitude,
                    longitude = myLocation!!.longitude,
                    location = locationName
                )

                val intent = Intent()
                intent.putExtra(Constants.DATA, location as Serializable)

                setResult(RESULT_OK, intent)
                finish()

            }


        }


        binding.tb.imgBack.setOnClickListener { finish() }

//        }catch (e:Exception){}

    }


    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        checkLocationPermissions()

//        mMap.resetMinMaxZoomPreference()

        /*   mMap.setOnCameraIdleListener { //get lat-lng at the center by calling
               myLocation = mMap.cameraPosition.target

               Log.e(TAG, "Camera OnCameraIdleListener: 2")

               setLocationUI(false)

           }
   */
        mMap.setOnMapClickListener {

            Log.e(TAG, "onMapReady: ma")
            myLocation = it
            setLocationUI(false)

        }


        mMap.setOnCameraMoveStartedListener { reason: Int ->

            Log.e(TAG, "onMapReady: a12")

            when (reason) {
                GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE -> {
                    Log.e(TAG, "The user gestured on the map 0.")

                    if (myLocation?.longitude != mMap.cameraPosition.target.longitude && myLocation?.latitude != mMap.cameraPosition.target.latitude) {

                        Log.e(TAG, "The user gestured on the map 1.")
                        myLocation = mMap.cameraPosition.target
                        setLocationUI(false)

                    }
                    else
                        Log.e(TAG, "The user gestured on the map 2.")

                }
                GoogleMap.OnCameraMoveStartedListener
                    .REASON_API_ANIMATION -> {
                    Log.e(TAG, "The user tapped something on the map.")
                }
                GoogleMap.OnCameraMoveStartedListener
                    .REASON_DEVELOPER_ANIMATION -> {
                    Log.e(TAG, "The app moved the camera.")
                }
            }
        }

    }

    private fun setLocationUI(zoomCamera: Boolean) {
        val addresses: List<Address>? =
            geocode.getFromLocation(myLocation!!.latitude, myLocation!!.longitude, 1)

        if (addresses != null && addresses.isNotEmpty()) {

//            mMap.clear()
//            mMap.addMarker(MarkerOptions().position(myLocation!!).icon(null))//.icon(bicycleIcon))
            val myPosition = CameraPosition.Builder()
                .target(myLocation!!)

//             if (zoomCamera)
            myPosition.zoom(28f)


            mMap.moveCamera(
                CameraUpdateFactory.newCameraPosition(myPosition.build())
            )

//            moveCamera(myLocation!!, moveCamera)

            binding.tvAddress.text = addresses[0].getAddressLine(0)

        }

    }

    /*   private fun moveCamera(latLng: LatLng, zooming: Boolean) {

           val myPosition = CameraPosition.Builder()
               .target(latLng)

           /* if (zooming)
                myPosition.zoom(14f)
    */
           mMap.moveCamera(
               CameraUpdateFactory.newCameraPosition(myPosition.build())
           )


       }

       private fun getViewBitmapSmall(): Bitmap? {

           val normalBinding: LayoutPinBinding = DataBindingUtil.inflate(
               LayoutInflater.from(this),
               R.layout.layout_pin,
               null,
               false
           )
           val mView = normalBinding.root

           mView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
           mView.layout(0, 0, mView.measuredWidth, mView.measuredHeight)
           mView.buildDrawingCache()
           val returnedBitmap = Bitmap.createBitmap(
               mView.measuredWidth, mView.measuredHeight,
               Bitmap.Config.ARGB_8888
           )
           val canvas = Canvas(returnedBitmap)
           canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN)
           val drawable = mView.background
           drawable?.draw(canvas)
           mView.draw(canvas)
           return returnedBitmap
       }*/

    private fun checkLocationPermissions() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                requestMultiplePermissions.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                )
                Log.e(TAG, "checkLocationPermissions: 1")
            } else {
                Log.e(TAG, "checkLocationPermissions: 2")
                goToMyLocation()

            }

        } else {
            goToMyLocation()
        }

    }


    @SuppressLint("MissingPermission")
    private fun goToMyLocation() {

//        mMap.isMyLocationEnabled = true

        if (haveExtraLocation()) {

            val location = intent.extras!!.getSerializable(Constants.DATA) as SimpleLocationModel

            myLatLng = LatLng(
                location.latitude,
                location.longitude
            )

            myLocation = myLatLng
            setLocationUI(true)

            return
        }
        val locationManager =
            this.getSystemService(Context.LOCATION_SERVICE) as LocationManager


        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps()
        }

        val locationListener: LocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {

                if (myLatLng != null) return

                Log.e(TAG, "Camera onLocationChanged: 1")

                myLatLng = LatLng(
                    location.latitude,
                    location.longitude
                )

                myLocation = myLatLng
                setLocationUI(true)


            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

        locationManager.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            0,
            0f,
            locationListener
        )
    }

    private fun haveExtraLocation(): Boolean =
        intent.extras != null && intent.extras!!.containsKey(Constants.DATA)


    private fun buildAlertMessageNoGps() {
        val d = HelperDialog(getString(R.string.open_gps), null)
        d.isCancelable = false
        d.show(supportFragmentManager, "")
    }

    companion object {
        private var TAG = this::class.java.name
    }

}