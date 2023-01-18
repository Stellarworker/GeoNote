package com.stellarworker.geonote.ui.maps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.stellarworker.geonote.R
import com.stellarworker.geonote.databinding.FragmentMapsBinding
import com.stellarworker.geonote.domain.messages.AppMessage
import com.stellarworker.geonote.utils.MapHelper
import com.stellarworker.geonote.utils.PermissionHelper
import com.stellarworker.geonote.utils.makeSnackbar
import com.stellarworker.geonote.utils.showDialog
import org.koin.androidx.viewmodel.ext.android.viewModel

class MapsFragment : Fragment() {
    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private lateinit var map: GoogleMap
    private lateinit var mapView: MapView
    private val mapsFragmentViewModel: MapsFragmentViewModel by viewModel()
    private val mapHelper = MapHelper()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val permissionHelper = PermissionHelper(requireActivity(), this)
        initMapsFragmentViewModel()
        mapView = view.findViewById(R.id.map)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync { googleMap ->
            map = googleMap
            mapHelper.initMap(map, mapsFragmentViewModel)
        }
        permissionHelper.startAccessToLocation(mapsFragmentViewModel)
        initButtons()
    }

    private fun initMapsFragmentViewModel() {
        with(mapsFragmentViewModel) {
            messagesLiveData.observe(viewLifecycleOwner) { message ->
                processMessages(message)
            }
            locationLiveData.observe(viewLifecycleOwner) { locationMarker ->
                mapHelper.changeLocationMarker(map, locationMarker.markerOptions)
            }
        }
    }

    private fun initButtons() {
        with(mapsFragmentViewModel) {
            binding.zoomInButton.setOnClickListener {
                onZoomInButtonClick(map.cameraPosition.target, map.cameraPosition.zoom)
            }
            binding.zoomOutButton.setOnClickListener {
                onZoomOutButtonClick(map.cameraPosition.target, map.cameraPosition.zoom)
            }
            binding.deviceLocationButton.setOnClickListener { onDeviceLocationButtonClick() }
        }
    }

    private fun processMessages(appMessage: AppMessage) {
        with(appMessage) {
            when (this) {
                is AppMessage.UserMarkers -> mapHelper.addUserMarkers(map, userMarkers)
                is AppMessage.MoveCamera -> mapHelper.moveCamera(map, animated, location, zoomLevel)
                is AppMessage.InfoDialog -> showDialog(requireContext(), title, message)
                is AppMessage.InfoSnackBar -> makeSnackbar(
                    view = binding.root,
                    text = text,
                    anchor = binding.anchor
                )
                is AppMessage.InfoToast -> Toast.makeText(context, text, length).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapsFragmentViewModel.stopLocationUpdates()
        mapsFragmentViewModel.saveCameraState(map.cameraPosition.target, map.cameraPosition.zoom)
        mapView.onPause()
    }

    override fun onStop() {
        mapView.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        mapView.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }
}