package com.app.famcare.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.famcare.R
import com.app.famcare.view.detailpost.DetailPostActivity

class NannyAdapter(private val context: Context, private val nannyList: List<Nanny>) :
    RecyclerView.Adapter<NannyAdapter.NannyViewHolder>() {

    class NannyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewNanny: ImageView = itemView.findViewById(R.id.imageViewNanny)
        val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        val textViewCategory: TextView = itemView.findViewById(R.id.textViewCategory)
        val textViewRating: TextView = itemView.findViewById(R.id.textViewRating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NannyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_row_nanny, parent, false)
        return NannyViewHolder(view)
    }

    override fun onBindViewHolder(holder: NannyViewHolder, position: Int) {
        val nanny = nannyList[position]
        holder.imageViewNanny.setImageResource(nanny.pict)
        holder.textViewName.text = nanny.name
        holder.textViewCategory.text = nanny.type
        holder.textViewRating.text = nanny.rate.toString()

        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailPostActivity::class.java)
            intent.putExtra("nanny", nanny)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return nannyList.size
    }
}