package uk.co.keytree.transportappkt.ui.nearesttrains

import android.annotation.SuppressLint
import android.arch.lifecycle.MutableLiveData
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.android.gms.location.FusedLocationProviderClient
import uk.co.keytree.transportappkt.R
import uk.co.keytree.transportappkt.base.BaseViewModel

class LocationViewModel(private val fusedLocationClient: FusedLocationProviderClient): BaseViewModel() {
    val loadingVisibility: MutableLiveData<Int> = MutableLiveData()
    val errorMessage: MutableLiveData<Int> = MutableLiveData()
    val locationResult: MutableLiveData<Location> = MutableLiveData()
    @SuppressLint("MissingPermission")
    fun getLastLocation(activity: AppCompatActivity) {
        onStartLocationRequest()
        fusedLocationClient.lastLocation
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful && task.result != null) {
                    locationResult.value = task.result
                } else {
                    errorMessage.value = R.string.location_error
                }
                onEndLocationRequest()
            }
    }

    private fun onStartLocationRequest() {
        loadingVisibility.value = View.VISIBLE
        errorMessage.value = null
    }

    private fun onEndLocationRequest() {
        loadingVisibility.value = View.GONE
    }
}