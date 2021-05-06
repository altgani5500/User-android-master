package com.partime.user.Responses

import java.io.Serializable

data class HoursRate(
    var hoursRate: String,
    var isClicked: Boolean

) : Serializable