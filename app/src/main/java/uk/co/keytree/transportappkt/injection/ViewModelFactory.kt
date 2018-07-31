package uk.co.keytree.transportappkt.injection

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.location.LocationServices
import uk.co.keytree.transportappkt.ui.nearesttrains.LocationViewModel

class ViewModelFactory(private val activity: AppCompatActivity): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(LocationViewModel::class.java)) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
            return LocationViewModel(fusedLocationClient) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}