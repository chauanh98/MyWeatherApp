package com.example.myweatherapp.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.myweatherapp.R
import com.example.myweatherapp.databinding.ActivityMainBinding
import com.example.myweatherapp.extensions.getAlertDialog
import com.example.myweatherapp.model.domain.Hourly
import com.example.myweatherapp.ui.viewmodel.MainViewModel
import com.example.myweatherapp.ui.viewmodel.MainViewModelFactory
import com.example.myweatherapp.utils.Constants
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URL
import java.text.SimpleDateFormat

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mViewModel: MainViewModel
    private var mAddress: GeoPoint? = null

    private val mAlertDialogLoading: AlertDialog by lazy {
        getAlertDialog(this, Constants.DIALOGS.DIALOG_LOADING)
    }
    private val mAlertDialogError: AlertDialog by lazy {
        getAlertDialog(this, Constants.DIALOGS.DIALOG_ERROR)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        initializeViewModel()
        setObserversUI()
        setListenerUI()
    }

    private fun setupLatLngCallBack(mAddress: GeoPoint?) {
        mAddress?.let {
            lifecycleScope.launch {
                mViewModel.getWeather(it.latitude, it.longitude)
            }
        }
    }

    private fun initializeViewModel() {
        mViewModel = ViewModelProvider(
            this,
            MainViewModelFactory(application)
        ).get(MainViewModel::class.java)
    }

    private fun setObserversUI() {
        mViewModel.mWeather.observe(this) { weather ->
            weather?.let { data ->
                data.weather[0].hourly.let {
                    for (hourly in it.reversed()) {
                        val formatter = SimpleDateFormat("HHmm")
                        val curDate = System.currentTimeMillis()
                        val str: String = formatter.format(curDate)
                        if (str > hourly.time.toString()) {
                            setDataInUI(hourly)
                            break
                        }
                    }
                }
            }
        }
        mViewModel.isViewLoading.observe(this) {
            if (it) mAlertDialogLoading.show()
            else mAlertDialogLoading.dismiss()
        }
        mViewModel.anErrorOccurred.observe(this) {
            if (it) mAlertDialogError.show() else mAlertDialogError.dismiss()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setDataInUI(hourly: Hourly) {
        lifecycleScope.launch {
            with(mBinding) {
                this.textHumidity.text = hourly.humidity.toString()
                this.textFeelsLikeTemp.text = hourly.mid[0].tempC.toString()
                this.textLocationStatus.text = this.autoComplete.text
                this.textTemperatureStatus.text =
                    hourly.mid[0].tempF.toString() + " ℉"
                this.textMainStatus.text = hourly.mid[0].weatherDesc[0].cdata
                renderPictureWeather(
                    this.imageWeatherStatus,
                    hourly.mid[0].weatherIconUrl[0].cdata
                )
            }
        }
    }

    private fun renderPictureWeather(imageView: ImageView, url: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val imagePath = URL(url)
            val bmp = BitmapFactory.decodeStream(imagePath.openConnection().getInputStream())
            withContext(Dispatchers.Main) {
                imageView.setImageBitmap(bmp)
            }
        }
    }

    private fun setListenerUI() {
        val cities = resources.getStringArray(R.array.cities)
        val arrAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, cities)
        mBinding.autoComplete.setAdapter(arrAdapter)
        mBinding.autoComplete.setOnItemClickListener { adapterView, _, i, _ ->
            mAddress = getLocationFromAddress(this, adapterView.getItemAtPosition(i).toString())
            setupLatLngCallBack(mAddress)
            hideKeyBoard()
        }
    }

    private fun hideKeyBoard() {
        val view: View? = this.currentFocus
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun getLocationFromAddress(context: Context?, strAddress: String?): GeoPoint? {
        val coder = Geocoder(context)
        val address: List<Address>?
        var latLng: GeoPoint? = null
        try {
            address = coder.getFromLocationName(strAddress, 5)
            if (address == null) {
                return null
            }
            val location = address[0]
            latLng = GeoPoint(location.latitude, location.longitude)

        } catch (ex: IOException) {
            ex.printStackTrace()
        }
        return latLng
    }
}