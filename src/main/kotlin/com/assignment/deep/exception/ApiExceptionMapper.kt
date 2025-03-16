package com.assignment.deep.exception

import com.assignment.deep.exception.model.ErrorResponse
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.ExceptionMapper
import org.slf4j.Logger

class ApiExceptionMapper : ExceptionMapper<Exception> {

    private val logger = Logger.getLogger(ApiExceptionMapper::class)

    override fun toResponse(exception: Exception): Response {
        return when (exception) {
            is ApiException -> handleApiException(exception)
            else -> handleGenericException(exception)
        }
    }

    private fun handleApiException(e: ApiException): Response {
        logger.error("API Error: ${e.message}")
        return Response.status(e.status)
            .entity(ErrorResponse(e.status.statusCode, e.message, e.details))
            .build()
    }

    private fun handleGenericException(e: Exception): Response {
        logger.error("Unexpected error: ${e.message}")
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
            .entity(ErrorResponse(
                code = 500,
                message = "Internal server error",
                details = e.message
            ))
            .build()
    }
}
