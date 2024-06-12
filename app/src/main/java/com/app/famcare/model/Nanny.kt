package com.app.famcare.model

import android.os.Parcel
import android.os.Parcelable

data class Nanny(
    var id: String = "",
    val age: String = "",
    val experience: String = "",
    val gender: String = "",
    val location: String = "",
    val name: String = "",
    val rate: String = "",
    val salary: String = "",
    val skills: List<String> = listOf(),
    val type: String = "",
    val pict: String = "",
    val contact: String = "", // Tambah properti contact
    val imageUrl: String = "" // Tambah properti imageUrl
) : Parcelable {
    constructor(parcel: Parcel) : this(
        id = parcel.readString() ?: "",
        age = parcel.readString() ?: "",
        experience = parcel.readString() ?: "",
        gender = parcel.readString() ?: "",
        location = parcel.readString() ?: "",
        name = parcel.readString() ?: "",
        rate = parcel.readString() ?: "",
        salary = parcel.readString() ?: "",
        skills = parcel.createStringArrayList() ?: listOf(),
        type = parcel.readString() ?: "",
        pict = parcel.readString() ?: "",
        contact = parcel.readString() ?: "", // Baca properti contact dari parcel
        imageUrl = parcel.readString() ?: "" // Baca properti imageUrl dari parcel
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(age)
        parcel.writeString(experience)
        parcel.writeString(gender)
        parcel.writeString(location)
        parcel.writeString(name)
        parcel.writeString(rate)
        parcel.writeString(salary)
        parcel.writeStringList(skills)
        parcel.writeString(type)
        parcel.writeString(pict)
        parcel.writeString(contact) // Tulis properti contact ke parcel
        parcel.writeString(imageUrl) // Tulis properti imageUrl ke parcel
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Nanny> {
        override fun createFromParcel(parcel: Parcel): Nanny {
            return Nanny(parcel)
        }

        override fun newArray(size: Int): Array<Nanny?> {
            return arrayOfNulls(size)
        }
    }
}