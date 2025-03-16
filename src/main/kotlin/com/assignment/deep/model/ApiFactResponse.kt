package com.assignment.deep.model

import com.fasterxml.jackson.annotation.JsonProperty

data class ApiFactResponse (
    val id: String,
    val text: String,
    val source: String,
    @JsonProperty("source_url") val sourceUrl: String?,
    val language: String,
    val permalink: String
)
