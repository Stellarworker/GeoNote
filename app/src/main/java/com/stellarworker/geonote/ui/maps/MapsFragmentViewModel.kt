package com.stellarworker.geonote.ui.maps

import android.annotation.SuppressLint
import android.app.Application
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.stellarworker.geonote.R
import com.stellarworker.geonote.data.SingleLiveEvent
import com.stellarworker.geonote.data.contracts.MapsFragmentViewModelContract
import com.stellarworker.geonote.domain.MapState
import com.stellarworker.geonote.domain.UserMarker
import com.stellarworker.geonote.domain.messages.AppMessage
import com.stellarworker.geonote.domain.messages.LocationMarker
import com.stellarworker.geonote.repository.LocalRepository
import com.stellarworker.geonote.utils.DataUtils
import com.stellarworker.geonote.utils.EMPTY
import kotlinx.coroutines.*

class MapsFragmentViewModel(
    private val application: Application,
    private val repository: LocalRepository
) : ViewModel(),
    MapsFragmentViewModelContract {

    private val _messagesLiveData = SingleLiveEvent<AppMessage>()
    val messagesLiveData: LiveData<AppMessage> by this::_messagesLiveData

    private val _locationLiveData = MutableLiveData<LocationMarker>()
    val locationLiveData: LiveData<LocationMarker> by this::_locationLiveData

    private val fusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(application)

    private var locationUpdatesStartedFlag = false
    private var rationaleFlag = false
    private var notGrantedNoAskFlag = false
    private var requestLocationShow = true
    private val defaultLocation = LatLng(DEFAULT_LOCATION_LATITUDE, DEFAULT_LOCATION_LONGITUDE)
    private var lastLocation = defaultLocation
    private val dataUtils = DataUtils()
    private val mapState = MapState(defaultLocation, DEFAULT_LOCATION_ZOOM_LEVEL)

    private val locationRequest = LocationRequest.Builder(DEVICE_LOCATION_REFRESH_PERIOD)
        .setDurationMillis(DEVICE_LOCATION_REQUEST_DURATION)
        .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
        .build()

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            locationResult.locations.forEach { location ->
                lastLocation = LatLng(location.latitude, location.longitude)
                if (requestLocationShow) {
                    _messagesLiveData.value =
                        AppMessage.MoveCamera(true, lastLocation, DEVICE_LOCATION_ZOOM_LEVEL)
                    requestLocationShow = false
                }
                val options = MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_marker))
                    .title(application.getString(R.string.location_marker_default_title))
                    .position(lastLocation)
                mapState.locationMarker = options
                _locationLiveData.value = LocationMarker(options)
            }
        }
    }

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, error ->
        error.printStackTrace()
    }
    private var dbJob: Job? = null
    private val mainScope =
        CoroutineScope(Dispatchers.IO + SupervisorJob() + coroutineExceptionHandler)

    @SuppressLint("MissingPermission")
    private fun enableLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
        locationUpdatesStartedFlag = true
    }

    private fun saveMarker(userMarker: UserMarker) {
        dbJob?.cancel()
        dbJob = mainScope.launch {
            repository.saveMarker(userMarker)
        }
    }

    private fun requestMarkers() {
        dbJob?.cancel()
        dbJob = mainScope.launch {
            val userMarkers = repository.getAllMarkers()
            if (userMarkers.isNotEmpty()) {
                _messagesLiveData.postValue(AppMessage.UserMarkers(userMarkers))
            }
        }
    }

    override fun startLocationUpdates() {
        if (locationUpdatesStartedFlag.not()) {
            if (dataUtils.isGooglePlayServicesAvailable()) {
                enableLocationUpdates()
            } else {
                _messagesLiveData.value =
                    AppMessage.InfoDialog(
                        title = application.getString(R.string.no_play_services_title),
                        message = application.getString(R.string.no_play_services_message)
                    )
            }
        }
    }

    override fun stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        locationUpdatesStartedFlag = false
    }

    override fun onZoomInButtonClick(location: LatLng, oldZoom: Float) {
        _messagesLiveData.value =
            AppMessage.MoveCamera(false, location, oldZoom + ZOOM_INCREMENT)
    }

    override fun onZoomOutButtonClick(location: LatLng, oldZoom: Float) {
        _messagesLiveData.value =
            AppMessage.MoveCamera(false, location, oldZoom - ZOOM_INCREMENT)
    }

    override fun onDeviceLocationButtonClick() {
        if (dataUtils.isGooglePlayServicesAvailable()) {
            when {
                notGrantedNoAskFlag -> requestNotGrantedNoAskDialog()
                rationaleFlag -> requestRationaleDialog()
                else -> _messagesLiveData.value =
                    AppMessage.MoveCamera(true, lastLocation, DEVICE_LOCATION_ZOOM_LEVEL)
            }
        } else {
            _messagesLiveData.value =
                AppMessage.InfoSnackBar(application.getString(R.string.message_google_play_services_not_present))
        }
    }

    override fun onMapClick(location: LatLng) {
        val markerTitle = application.getString(R.string.user_marker_default_title)
        val userMarker = UserMarker(
            markerOptions = MarkerOptions().title(markerTitle).position(location),
            title = markerTitle
        )
        saveMarker(userMarker)
        _messagesLiveData.value = AppMessage.UserMarkers(listOf(userMarker))
    }

    override fun onMarkerClick(marker: Marker) {
        _messagesLiveData.value =
            AppMessage.InfoSnackBar(marker.title ?: String.EMPTY)
    }

    override fun requestNotGrantedNoAskDialog() {
        notGrantedNoAskFlag = true
        _messagesLiveData.value = AppMessage.InfoDialog(
            title = application.getString(R.string.not_granted_no_ask_title),
            message = application.getString(R.string.not_granted_no_ask_message)
        )
    }

    override fun requestRationaleDialog() {
        rationaleFlag = true
        _messagesLiveData.value = AppMessage.InfoDialog(
            title = application.getString(R.string.rationale_title),
            message = application.getString(R.string.rationale_message)
        )
    }

    override fun restoreMapState() {
        with(mapState) {
            _messagesLiveData.value =
                AppMessage.MoveCamera(false, lastLocation, zoomLevel)
            locationMarker?.let { marker ->
                _locationLiveData.value = LocationMarker(marker)
            }
        }
        requestMarkers()
    }

    override fun saveCameraState(location: LatLng, zoomLevel: Float) {
        mapState.lastLocation = location
        mapState.zoomLevel = zoomLevel
    }

    companion object {
        private const val DEVICE_LOCATION_REFRESH_PERIOD = 2000L
        private const val DEVICE_LOCATION_REQUEST_DURATION = 60000L
        private const val DEFAULT_LOCATION_LATITUDE = 55.751513
        private const val DEFAULT_LOCATION_LONGITUDE = 37.616655
        private const val DEFAULT_LOCATION_ZOOM_LEVEL = 10.0f
        private const val DEVICE_LOCATION_ZOOM_LEVEL = 15f
        private const val ZOOM_INCREMENT = 1f
    }

}