package com.assignment.deep.exception.model

data class ErrorResponse(
    val code: Int,
    val message: String,
    val details: String? = null
)
