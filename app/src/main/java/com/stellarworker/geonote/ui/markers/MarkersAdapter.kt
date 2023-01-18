package com.stellarworker.geonote.ui.markers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.stellarworker.geonote.R
import com.stellarworker.geonote.domain.UserMarker
import com.stellarworker.geonote.utils.hide
import com.stellarworker.geonote.utils.show

class MarkersAdapter(
    private val onMarkerRemoved: ((position: Int, id: Long, adapter: MarkersAdapter) -> Unit)? = null,
    private val onMarkerSaved: ((
        position: Int, title: String, description: String, adapter: MarkersAdapter,
        viewHolder: RecyclerView.ViewHolder
    ) -> Unit)? = null,
) : RecyclerView.Adapter<MarkersAdapter.RecyclerItemViewHolder>() {
    private var userMarkers: MutableList<UserMarker> = mutableListOf()

    fun setData(userMarkers: List<UserMarker>) {
        this.userMarkers = userMarkers.toMutableList()
        notifyDataSetChanged()
    }

    fun removeMarker(position: Int) {
        userMarkers.removeAt(position)
        notifyItemRemoved(position)
    }

    fun editMarker(title: String, description: String, position: Int) {
        userMarkers[position].title = title
        userMarkers[position].description = description
        notifyItemChanged(position)
    }

    fun getMarker(position: Int) = userMarkers[position]

    inner class RecyclerItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val title: TextInputEditText =
            itemView.findViewById(R.id.marker_item_title)
        private val description: TextInputEditText =
            itemView.findViewById(R.id.marker_item_description)
        private val latitude: MaterialTextView =
            itemView.findViewById(R.id.marker_item_latitude)
        private val longitude: MaterialTextView =
            itemView.findViewById(R.id.marker_item_longitude)
        private val delete: AppCompatImageView =
            itemView.findViewById(R.id.marker_item_delete)
        private val save: AppCompatImageView =
            itemView.findViewById(R.id.marker_item_save)

        init {

            fun toggleButtons(view: View, hasFocus: Boolean, text: String) {
                if (hasFocus) {
                    delete.hide()
                    save.show()
                } else {
                    (view as? TextInputEditText)?.setText(text)
                    save.hide()
                    delete.show()
                }
            }

            delete.setOnClickListener {
                onMarkerRemoved?.invoke(
                    adapterPosition,
                    userMarkers[adapterPosition].id,
                    this@MarkersAdapter
                )
            }

            save.setOnClickListener {
                onMarkerSaved?.invoke(
                    adapterPosition,
                    title.text.toString(),
                    description.text.toString(),
                    this@MarkersAdapter,
                    this
                )
            }

            title.setOnFocusChangeListener { currentView, hasFocus ->
                toggleButtons(currentView, hasFocus, userMarkers[adapterPosition].title)
            }

            description.setOnFocusChangeListener { currentView, hasFocus ->
                toggleButtons(currentView, hasFocus, userMarkers[adapterPosition].description)
            }

        }

        fun bind(userMarker: UserMarker) {
            title.setText(userMarker.title)
            description.setText(userMarker.description)
            latitude.text = itemView.context.getString(
                R.string.marker_item_latitude,
                userMarker.markerOptions.position.latitude
            )
            longitude.text = itemView.context.getString(
                R.string.marker_item_longitude,
                userMarker.markerOptions.position.longitude
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerItemViewHolder {
        return RecyclerItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.marker_item, parent, false) as View
        )
    }

    override fun onBindViewHolder(holder: RecyclerItemViewHolder, position: Int) {
        holder.bind(userMarkers[position])
    }

    override fun getItemCount() = userMarkers.size
}