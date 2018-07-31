package uk.co.keytree.transportappkt.ui.nearesttrains

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.annotation.StringRes
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.Snackbar
import android.support.design.widget.Snackbar.LENGTH_INDEFINITE
import android.support.v4.app.ActivityCompat
import android.support.v4.content.PermissionChecker.PERMISSION_GRANTED
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import uk.co.keytree.transportappkt.BuildConfig.APPLICATION_ID
import uk.co.keytree.transportappkt.R
import uk.co.keytree.transportappkt.databinding.ActivityMainBinding
import uk.co.keytree.transportappkt.injection.ViewModelFactory
import uk.co.keytree.transportappkt.model.Station

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 29

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var nearestTrainViewModel: NearestTrainsListViewModel
    private lateinit var locationViewModel: LocationViewModel

    private var errorSnackBar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.trainsList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        nearestTrainViewModel = ViewModelProviders.of(this).get(NearestTrainsListViewModel::class.java)
        val decoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        binding.trainsList.addItemDecoration(decoration)
        nearestTrainViewModel.errorMessage.observe(this, Observer {
            errorMessage -> if(errorMessage != null) showError(errorMessage) else hideError()
        })
        binding.viewModel = nearestTrainViewModel
        nearestTrainViewModel.tapped.observe(this, Observer {
            station -> if(station != null) showTapped(station)
        })

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        locationViewModel = ViewModelProviders.of(this, ViewModelFactory(this)).get(LocationViewModel::class.java)
        locationViewModel.errorMessage.observe(this, Observer {
            errorMessage -> if(errorMessage != null) showError(errorMessage) else hideError()
        })
        locationViewModel.locationResult.observe(this, Observer {
            locationResult -> if(locationResult != null) {
                nearestTrainViewModel.loadNearestStations(locationResult.latitude, locationResult.longitude)
            }
        })

        binding.swipeContainer.setOnRefreshListener{
            binding.swipeContainer.setRefreshing(false)
            getLocation()
        }

    }

    override fun onStart() {
        super.onStart()

        getLocation()
    }

    private fun getLocation() {
        if (!checkPermissions()) {
            requestPermissions()
        } else {
            locationViewModel.getLastLocation(this)
        }
    }

    private fun showError(@StringRes errorMessage:Int){
        errorSnackBar = Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_INDEFINITE)
        errorSnackBar?.setAction(R.string.retry, nearestTrainViewModel.errorClickListener)
        errorSnackBar?.show()
    }

    private fun showTapped(station: Station){
        errorSnackBar = Snackbar.make(binding.root, "${station.name} ${station.station_code}", Snackbar.LENGTH_LONG)
        errorSnackBar?.show()
    }

    private fun hideError(){
        errorSnackBar?.dismiss()
    }

    /**
     * Return the current state of the permissions needed.
     */
    private fun checkPermissions() =
            ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED

    private fun startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(this, arrayOf(ACCESS_COARSE_LOCATION),
                REQUEST_PERMISSIONS_REQUEST_CODE)
    }

    private fun requestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_COARSE_LOCATION)) {
            // Provide an additional rationale to the user. This would happen if the user denied the
            // request previously, but didn't check the "Don't ask again" checkbox.
            Log.i(TAG, "Displaying permission rationale to provide additional context.")
            showSnackbar(R.string.permission_rationale, android.R.string.ok, View.OnClickListener {
                // Request permission
                startLocationPermissionRequest()
            })

        } else {
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            Log.i(TAG, "Requesting permission")
            startLocationPermissionRequest()
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        Log.i(TAG, "onRequestPermissionResult")
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            when {
            // If user interaction was interrupted, the permission request is cancelled and you
            // receive empty arrays.
                grantResults.isEmpty() -> Log.i(TAG, "User interaction was cancelled.")

            // Permission granted.
                (grantResults[0] == PackageManager.PERMISSION_GRANTED) -> locationViewModel.getLastLocation(this)

            // Permission denied.

            // Notify the user via a SnackBar that they have rejected a core permission for the
            // app, which makes the Activity useless. In a real app, core permissions would
            // typically be best requested during a welcome-screen flow.

            // Additionally, it is important to remember that a permission might have been
            // rejected without asking the user for permission (device policy or "Never ask
            // again" prompts). Therefore, a user interface affordance is typically implemented
            // when permissions are denied. Otherwise, your app could appear unresponsive to
            // touches or interactions which have required permissions.
                else -> {
                    showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                            View.OnClickListener {
                                // Build intent that displays the App settings screen.
                                val intent = Intent().apply {
                                    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                    data = Uri.fromParts("package", APPLICATION_ID, null)
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                }
                                startActivity(intent)
                            })
                }
            }
        }
    }

    /**
     * Shows a [Snackbar].
     *
     * @param snackStrId The id for the string resource for the Snackbar text.
     * @param actionStrId The text of the action item.
     * @param listener The listener associated with the Snackbar action.
     */
    private fun showSnackbar(
            snackStrId: Int,
            actionStrId: Int = 0,
            listener: View.OnClickListener? = null
    ) {
        val snackbar = Snackbar.make(findViewById(android.R.id.content), getString(snackStrId),
                LENGTH_INDEFINITE)
        if (actionStrId != 0 && listener != null) {
            snackbar.setAction(getString(actionStrId), listener)
        }
        snackbar.show()
    }
}
