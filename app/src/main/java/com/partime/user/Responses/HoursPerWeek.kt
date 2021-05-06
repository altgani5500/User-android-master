package com.partime.user.Responses

import java.io.Serializable

data class HoursPerWeek(
    var hoursPerWeek: Int,
    var isClicked: Boolean

) : Serializable