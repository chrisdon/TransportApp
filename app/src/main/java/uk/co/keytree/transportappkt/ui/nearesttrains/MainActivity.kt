package uk.co.keytree.transportappkt.ui.nearesttrains

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.annotation.StringRes
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.Snackbar
import android.support.design.widget.Snackbar.LENGTH_INDEFINITE
import android.support.v4.app.ActivityCompat
import android.support.v4.content.PermissionChecker.PERMISSION_GRANTED
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import uk.co.keytree.transportappkt.BuildConfig.APPLICATION_ID
import uk.co.keytree.transportappkt.R
import uk.co.keytree.transportappkt.base.BaseActivity
import uk.co.keytree.transportappkt.databinding.ActivityMainBinding
import uk.co.keytree.transportappkt.injection.NearestTrainsListViewModelFactory
import uk.co.keytree.transportappkt.injection.ViewModelFactory
import uk.co.keytree.transportappkt.model.Member
import uk.co.keytree.transportappkt.network.TransportApi
import javax.inject.Inject

class MainActivity : BaseActivity() {

    private val TAG = "MainActivity"
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 29
    private val TYPE_TRAIN_STATION = "train_station"

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_departures -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_directions -> {
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var nearestTrainViewModel: NearestTrainsListViewModel
    private lateinit var locationViewModel: LocationViewModel

    @Inject
    lateinit var transportApi: TransportApi

    private var errorSnackBar: Snackbar? = null
    private val errorMessageObserver = Observer<Int> {
        errorMessage -> if(errorMessage != null) showError(errorMessage) else hideError()
    }
    private val tappedObserver = Observer<Member> {
        member -> if(member != null) showTapped(member)
    }
    private val locationResultObserver = Observer<Location> {
        locationResult -> if(locationResult != null) {
            nearestTrainViewModel.loadNearestStations(locationResult.latitude, locationResult.longitude)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.trainsList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        nearestTrainViewModel = ViewModelProviders.of(this, NearestTrainsListViewModelFactory(transportApi)).get(NearestTrainsListViewModel::class.java)
        val decoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        binding.trainsList.addItemDecoration(decoration)
        nearestTrainViewModel.errorMessage.observe(this, errorMessageObserver)
        binding.viewModel = nearestTrainViewModel
        nearestTrainViewModel.tapped.observe(this, tappedObserver)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        locationViewModel = ViewModelProviders.of(this, ViewModelFactory(this)).get(LocationViewModel::class.java)
        locationViewModel.errorMessage.observe(this, errorMessageObserver)
        locationViewModel.locationResult.observe(this, locationResultObserver)

        binding.swipeContainer.setOnRefreshListener{
            binding.swipeContainer.setRefreshing(false)
            getLocation()
        }

    }

    override fun onStart() {
        super.onStart()

        getLocation()
    }

    override fun onDestroy() {
        super.onDestroy()
        nearestTrainViewModel.errorMessage.removeObserver(errorMessageObserver)
        nearestTrainViewModel.tapped.removeObserver(tappedObserver)
        locationViewModel.errorMessage.removeObserver(errorMessageObserver)
        locationViewModel.locationResult.removeObserver(locationResultObserver)
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

    private fun showTapped(member: Member){
        errorSnackBar = Snackbar.make(binding.root, makeMessage(member), Snackbar.LENGTH_LONG)
        errorSnackBar?.show()
    }

    private fun hideError(){
        errorSnackBar?.dismiss()
    }

    private fun makeMessage(member: Member) : String {
        return if(member.type == TYPE_TRAIN_STATION)  {
            "${member.name} ${member.station_code}"
        } else {
            "${member.name} ${member.atcocode}"
        }
    }

    /**
     * Return the current state of the permissions needed.
     */
    private fun checkPermissions() =
            ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED

    private fun startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(this, arrayOf(ACCESS_FINE_LOCATION),
                REQUEST_PERMISSIONS_REQUEST_CODE)
    }

    private fun requestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_FINE_LOCATION)) {
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
