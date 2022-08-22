package com.example.myweatherapp.ui.main

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.widget.SearchView
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
            if (it) mAlertDialogError.show() else mAlertDialogError.dismiss()
        }
    }

    private fun setListenerUI() {
        cityList.add(Weather(1, "Seoul", null))
        cityList.add(Weather(2, "Paris", null))
        cityList.add(Weather(3, "Moscow", null))
        cityList.add(Weather(4, "Tokyo", null))
        cityList.add(Weather(5, "London", null))
        cityList.add(Weather(6, "New York", null))

//        mViewModel.mCity.observe(this) {
//            cityList.add(it)
//        }

        mBinding.recyclerView.setHasFixedSize(true)
        mBinding.recyclerView.layoutManager = LinearLayoutManager(this)
        cityAdapter = CityAdapter()
        cityAdapter.setCitiesList(cityList)

        mBinding.recyclerView.addItemDecoration(
            DividerItemDecoration(this, RecyclerView.VERTICAL)
        )
        mBinding.adapter = cityAdapter
        cityAdapter.onItemClick = {
            mAddress = getLocationFromAddress(this, it.cityName)
            setupLatLngCallBack(mAddress, it.cityName)
            val cityFragment = CityFragment()
            cityFragment.show(supportFragmentManager, "CityFragment")
        }

        mBinding.searchCity.run {
            this.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
                androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    this@run.clearFocus()
                    return false
                }

                override fun onQueryTextChange(query: String?): Boolean {
                    filterCity(query!!)
                    return false
                }
            })
        }
    }

    private fun filterCity(query: String) {
        val filterCity = ArrayList<Weather>()
        for (item in cityList) {
            if (item.cityName.lowercase().contains(query.lowercase())) {
                filterCity.add(item)
            }
        }

        filterCity.let {
            cityAdapter.setFilterList(it)
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