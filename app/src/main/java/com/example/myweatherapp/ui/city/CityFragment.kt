package com.example.myweatherapp.ui.city

import android.app.Dialog
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import com.example.myweatherapp.R
import com.example.myweatherapp.databinding.FragmentCityBinding
import com.example.myweatherapp.model.domain.Hourly
import com.example.myweatherapp.ui.main.MainViewModel
import com.example.myweatherapp.ui.main.MainViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
import java.text.SimpleDateFormat

class CityFragment : DialogFragment() {

    private lateinit var mBinding: FragmentCityBinding
    private lateinit var mViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeViewModel()
        setObserversUI()
    }

    private fun setObserversUI() {
        mViewModel.mWeather!!.observe(this) { weather ->
            weather.let { data ->
                data.weather?.get(0)?.hourly?.let {
                    for (hourly in it.reversed()) {
                        val formatter = SimpleDateFormat("HHmm")
                        val curDate = System.currentTimeMillis()
                        val str: String = formatter.format(curDate)
                        if (str > hourly.time.toString()) {
                            setDataInUI(hourly, data.cityName)
                            break
                        }
                    }
                }
            }
        }
    }

    private fun setDataInUI(hourly: Hourly, cityName: String) {
        mBinding.hourly = hourly
        mBinding.cityName = cityName

        renderPictureWeather(mBinding.imageWeatherStatus, hourly.mid[0].weatherIconUrl[0].cdata)
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

    private fun initializeViewModel() {
        mViewModel = ViewModelProvider(
            this,
            MainViewModelFactory(requireActivity().application)
        ).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_city, container, false)

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.closest.setOnClickListener {
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        val dialog: Dialog? = dialog
        dialog?.let {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            it.window?.setLayout(width, height)
        }
    }
}