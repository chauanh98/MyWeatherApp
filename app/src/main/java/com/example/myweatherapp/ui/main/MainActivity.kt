package com.example.myweatherapp.ui.main

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myweatherapp.R
import com.example.myweatherapp.databinding.ActivityMainBinding
import com.example.myweatherapp.extensions.getAlertDialog
import com.example.myweatherapp.model.domain.Weather
import com.example.myweatherapp.ui.city.CityAdapter
import com.example.myweatherapp.ui.city.CityFragment
import com.example.myweatherapp.utils.Constants
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.launch
import java.io.IOException
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mViewModel: MainViewModel
    private var mAddress: GeoPoint? = null
    private lateinit var cityAdapter: CityAdapter
    private val cityList = ArrayList<Weather>()

    private val mAlertDialogLoading: AlertDialog by lazy {
        getAlertDialog(this, Constants.DIALOGS.DIALOG_LOADING)
    }
    private val mAlertDialogError: AlertDialog by lazy {
        getAlertDialog(this, Constants.DIALOGS.DIALOG_ERROR)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        initializeViewModel()
        setObserversLoadingUI()
        setListenerUI()
    }

    private fun setupLatLngCallBack(mAddress: GeoPoint?, cityName: String) {
        mAddress?.let {
            lifecycleScope.launch {
                mViewModel.getWeather(
                    it.latitude,
                    it.longitude,
                    cityName
                )
            }
        }
    }

    private fun initializeViewModel() {
        mViewModel = ViewModelProvider(
            this,
            MainViewModelFactory(application)
        ).get(MainViewModel::class.java)
    }

    private fun setObserversLoadingUI() {
        mViewModel.isViewLoading.observe(this) {
            if (it) mAlertDialogLoading.show()
            else mAlertDialogLoading.dismiss()
        }
        mViewModel.anErrorOccurred.observe(this) {
            if (it) mAlertDialogError.show()
            else mAlertDialogError.dismiss()
        }
    }

    private fun setListenerUI() {
        mBinding.run {
            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
            cityAdapter = CityAdapter()
            mViewModel.mCity.observe(this@MainActivity) {
                if (it.isNotEmpty()) {
                    cityList.clear()
                }
                cityList.addAll(it)
                cityAdapter.setCitiesList(cityList)
            }
            recyclerView.addItemDecoration(
                DividerItemDecoration(this@MainActivity, RecyclerView.VERTICAL)
            )
            adapter = cityAdapter

            val cities = resources.getStringArray(R.array.cities)
            val adapter =
                ArrayAdapter(this@MainActivity, android.R.layout.simple_list_item_1, cities)
            autoComplete.setAdapter(adapter)
            autoComplete.threshold = 1
            autoComplete.setOnItemClickListener { adapterView, _, i, _ ->
                mAddress = getLocationFromAddress(
                    this@MainActivity,
                    adapterView.getItemAtPosition(i).toString()
                )
                setupLatLngCallBack(mAddress, adapterView.getItemAtPosition(i).toString())
                val cityFragment = CityFragment()
                cityFragment.show(supportFragmentManager, "CityFragment")
            }
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