package com.assignment.deep.config

import jakarta.annotation.Priority
import jakarta.inject.Inject
import jakarta.ws.rs.Priorities
import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.container.ContainerRequestFilter
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.Provider
import org.eclipse.microprofile.config.inject.ConfigProperty

@Provider
@Priority(Priorities.AUTHENTICATION)
class AdminApiKeyFilter : ContainerRequestFilter {

    @Inject
    @ConfigProperty(name = "admin.api-key")
    lateinit var apiKey: String

    override fun filter(requestContext: ContainerRequestContext) {
        if (requestContext.uriInfo.path.contains("/admin")) {
            val providedKey = requestContext.getHeaderString("X-API-Key")

            if (providedKey == null || providedKey != apiKey) {
                requestContext.abortWith(
                    Response.status(Response.Status.UNAUTHORIZED)
                        .entity(mapOf("error" to "Invalid or missing API key"))
                        .build()
                )
            }
        }
    }
}
