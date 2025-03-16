package com.assignment.deep.exception

import jakarta.ws.rs.core.Response

sealed class ApiException(
    val status: Response.Status,
    override val message: String,
    val details: String? = null
) : RuntimeException(message)

class FactNotFoundException(id: String) :
    ApiException(Response.Status.NOT_FOUND, "Fact not found", "ID: $id")

class FactStorageException :
    ApiException(Response.Status.INTERNAL_SERVER_ERROR, "Failed to store fact")

class ExternalApiException(cause: Throwable) :
    ApiException(Response.Status.SERVICE_UNAVAILABLE, "External service unavailable", cause.message)
