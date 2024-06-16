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

data class BookingDailyHistory(
    val bookID: String,
    val nannyName: String,
    val bookDate: String,
    val bookHours: String,
    val type: BookingType = BookingType.DAILY,
    val nannyID: String = "",
    val totalCost: String = ""
){
    // Default constructor
    constructor() : this("", "", "", "", BookingType.DAILY, "", "")
}

data class BookingMonthlyHistory(
    val bookID: String = "",
    val nannyName: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val type: BookingType = BookingType.MONTHLY,
    val nannyID: String = "",
    val totalCost: String = ""
) {
    // Default constructor
    constructor() : this("", "", "", "", BookingType.MONTHLY, "", "")
}

enum class BookingType {
    DAILY, MONTHLY
}