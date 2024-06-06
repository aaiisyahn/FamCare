package com.app.famcare.model

import android.os.Parcel
import android.os.Parcelable

data class Nanny(
    val age: String = "",
    val experience: String = "",
    val gender: String = "",
    val location: String = "",
    val name: String = "",
    val rate: String = "",
    val salary: String = "",
    val skills: List<String> = listOf(),
    val type: String = "",
    val pict: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createStringArrayList() ?: listOf(),
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
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
