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
import com.app.famcare.model.Nanny
import com.app.famcare.view.detailpost.DetailPostActivity
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore

class BookmarkAdapter(private val context: Context) :
    RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder>() {

    private var nannyList: MutableList<Nanny> = mutableListOf()

    init {
        fetchNannyData()
    }

    class BookmarkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewNanny: ImageView = itemView.findViewById(R.id.imageViewNanny)
        val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        val textViewCategory: TextView = itemView.findViewById(R.id.textViewCategory)
        val textViewRating: TextView = itemView.findViewById(R.id.textViewRating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_row_nanny, parent, false)
        return BookmarkViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookmarkViewHolder, position: Int) {
        val nanny = nannyList[position]
        Glide.with(context).load(nanny.pict).into(holder.imageViewNanny)
        holder.textViewName.text = nanny.name
        holder.textViewCategory.text = nanny.type
        holder.textViewRating.text = nanny.rate

        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailPostActivity::class.java)
            intent.putExtra("nanny", nanny)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return nannyList.size
    }

    private fun fetchNannyData() {
        val db = FirebaseFirestore.getInstance()
        db.collection("Nanny")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val nanny = document.toObject(Nanny::class.java)
                    nannyList.add(nanny)
                }
                notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Handle any errors here
            }
    }
}