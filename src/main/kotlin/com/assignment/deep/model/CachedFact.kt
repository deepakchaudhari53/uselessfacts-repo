package com.assignment.deep.model

data class CachedFact(
    val shortenedUrl: String,
    val originalId: String,
    val text: String,
    val source: String,
    val sourceUrl: String?,
    val language: String,
    val permalink: String,
    var accessCount: Int = 0
)
