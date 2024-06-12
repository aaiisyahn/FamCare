package com.app.famcare.model

data class BookingDaily(
    val bookID: String,
    val nannyName: String,
    val bookDate: String,
    val bookHours: String,
    val type: BookingType
)

data class BookingMonthly(
    val bookID: String,
    val nannyName: String,
    val startDate: String,
    val endDate: String,
    val type: BookingType
)

enum class BookingType {
    DAILY, MONTHLY
}