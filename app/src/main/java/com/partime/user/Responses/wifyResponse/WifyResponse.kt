package com.partime.user.Responses.wifyResponse

data class WifyResponse(
    val code: Int,
    val error_message: Any,
    val message: String,
    val success: Boolean
)
