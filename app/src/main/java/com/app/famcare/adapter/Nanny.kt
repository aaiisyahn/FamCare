package com.app.famcare.adapter

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Nanny(
    var name: String = "",
    var gender: String = "",
    var age: Int = 0,
    var experience: String = "",
    var salary: String = "",
    var skills: List<String> = ArrayList(),
    var type: String = "",
    var rate: Double = 0.0,
    var contact: String = "",
    var pict: Int = 0,
    var location: String = ""
) : Parcelable {
    constructor() : this("", "", 0, "", "", ArrayList(), "", 0.0, "", 0, "")

    companion object {
        val listData: ArrayList<Nanny>
            get() {
                val list = ArrayList<Nanny>()
                val dataNanny = DataNanny.getListNanny()
                list.addAll(dataNanny)
                return list
            }
    }
}