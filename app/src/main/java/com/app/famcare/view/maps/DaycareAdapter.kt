package com.app.famcare.view.maps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.famcare.R
import com.bumptech.glide.Glide
import com.google.firebase.firestore.GeoPoint

data class Daycare(
    val name: String,
    val location: String,
    val photoURL: String,
    val coordinates: GeoPoint,
    val distanceFromUser: Double
)

class DaycareAdapter(private var daycares: List<Daycare>) : RecyclerView.Adapter<DaycareAdapter.DaycareViewHolder>() {

    inner class DaycareViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val daycareImage: ImageView = itemView.findViewById(R.id.daycare_image)
        val daycareName: TextView = itemView.findViewById(R.id.daycare_name)
        val daycareLocation: TextView = itemView.findViewById(R.id.daycare_location)
        val daycareDistance: TextView = itemView.findViewById(R.id.tvDistancetoUser)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DaycareViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_daycare, parent, false)
        return DaycareViewHolder(view)
    }

    override fun onBindViewHolder(holder: DaycareViewHolder, position: Int) {
        val daycare = daycares[position]
        holder.daycareName.text = daycare.name
        holder.daycareLocation.text = daycare.location
        holder.daycareDistance.text = "${daycare.distanceFromUser} km"
        Glide.with(holder.itemView.context)
            .load(daycare.photoURL)
            .into(holder.daycareImage)
    }

    override fun getItemCount(): Int {
        return daycares.size
    }

    fun updateDaycares(newDaycares: List<Daycare>) {
        daycares = newDaycares
        notifyDataSetChanged()
    }
}
