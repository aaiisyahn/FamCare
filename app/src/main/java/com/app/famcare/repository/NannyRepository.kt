package com.app.famcare.repository

import com.app.famcare.model.Nanny
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class NannyRepository {

    private val db = FirebaseFirestore.getInstance()

    fun getNannies(
        onSuccess: (List<Nanny>) -> Unit, onFailure: (Exception) -> Unit
    ) {
        db.collection("Nanny").get().addOnSuccessListener { querySnapshot ->
                val nannyList = mutableListOf<Nanny>()
                for (document in querySnapshot) {
                    val nanny = document.toObject(Nanny::class.java).apply {
                        id = document.id  // Set the id property
                    }
                    nannyList.add(nanny)
                }
                onSuccess(nannyList)
            }.addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun getNannies(
        filterCriteria: Map<String, Any>,
        onSuccess: (List<Nanny>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        var query: Query = db.collection("Nanny")

        for ((key, value) in filterCriteria) {
            when (key) {
                "gender" -> query = query.whereEqualTo("gender", value)
                "type" -> query = query.whereEqualTo("type", value)
                "location" -> query = query.whereIn("location", value as List<String>)
                "skills" -> query = query.whereArrayContains("skills", value)
                "experience" -> query = query.whereGreaterThanOrEqualTo("experience", value as Int)
            }
        }

        query.get().addOnSuccessListener { querySnapshot ->
                val nannyList = mutableListOf<Nanny>()
                for (document in querySnapshot) {
                    val nanny = document.toObject(Nanny::class.java).apply {
                        id = document.id  // Set the id property
                    }
                    nannyList.add(nanny)
                }
                onSuccess(nannyList)
            }.addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}