package com.stellarworker.geonote.ui.markers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.stellarworker.geonote.R
import com.stellarworker.geonote.databinding.FragmentMarkersBinding
import com.stellarworker.geonote.domain.UserMarker
import com.stellarworker.geonote.domain.messages.MarkerMessage
import com.stellarworker.geonote.utils.hideKeyboard
import com.stellarworker.geonote.utils.makeSnackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class MarkersFragment : Fragment() {
    private var _binding: FragmentMarkersBinding? = null
    private val binding get() = _binding!!
    private val markersFragmentViewModel: MarkersFragmentViewModel by viewModel()
    private val adapter = MarkersAdapter(
        onMarkerRemoved = { position, id, adapter ->
            markersFragmentViewModel.removeMarker(id)
            adapter.removeMarker(position)
            markersFragmentViewModel.requestSnackbar(getString(R.string.message_marker_removed))
        },
        onMarkerSaved = { position, title, description, adapter, viewHolder ->
            markersFragmentViewModel.updateMarker(adapter.getMarker(position))
            adapter.editMarker(title, description, position)
            requireView().hideKeyboard()
            viewHolder.itemView.clearFocus()
            markersFragmentViewModel.requestSnackbar(getString(R.string.message_marker_saved))
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMarkersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMarkersFragmentViewModel()
        binding.fragmentMarkersList.adapter = adapter
        markersFragmentViewModel.requestMarkers()
    }

    private fun initMarkersFragmentViewModel() {
        markersFragmentViewModel.messagesLiveData.observe(viewLifecycleOwner) { message ->
            processMessages(message)
        }
    }

    private fun processMessages(markerMessage: MarkerMessage) {
        with(markerMessage) {
            when (this) {
                is MarkerMessage.UserMarkers -> showUserMarkers(userMarkers)
                is MarkerMessage.InfoSnackBar -> makeSnackbar(
                    view = binding.root,
                    text = text,
                    anchor = binding.fragmentMarkersAnchor
                )
                is MarkerMessage.InfoToast -> Toast.makeText(context, text, length).show()
            }
        }
    }

    private fun showUserMarkers(userMarkers: List<UserMarker>) {
        adapter.setData(userMarkers)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}