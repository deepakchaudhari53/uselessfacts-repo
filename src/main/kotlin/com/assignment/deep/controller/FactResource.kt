package com.assignment.deep.controller

import com.assignment.deep.client.UselessFactsClient
import com.assignment.deep.model.CachedFact
import com.assignment.deep.service.FactService
import io.quarkus.logging.Log
import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import org.eclipse.microprofile.rest.client.inject.RestClient
import java.util.*

@Path("/facts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Facts")
class FactResource {

    @Inject
    lateinit var factService: FactService

    @Inject
    @RestClient
    lateinit var uselessFactsClient: UselessFactsClient

    @POST
    @Operation(summary = "Fetch and store a new random fact")
    @APIResponse(responseCode = "200", description = "Successfully fetched and stored the fact")
    @APIResponse(responseCode = "503", description = "Failed to fetch fact from the external API")
    fun fetchAndStoreFact(): Response {
        return try {
            val apiResponse = uselessFactsClient.getRandomFact()
            val shortenedUrl = UUID.randomUUID().toString()
            val cachedFact = CachedFact(
                shortenedUrl = shortenedUrl,
                originalId = apiResponse.id,
                text = apiResponse.text,
                source = apiResponse.source,
                sourceUrl = apiResponse.sourceUrl,
                language = apiResponse.language,
                permalink = apiResponse.permalink
            )
            factService.saveFact(cachedFact)
            Log.info("Stored new fact with ID: ${cachedFact.shortenedUrl}")
            Response.ok(cachedFact).build()
        } catch (e: Exception) {
            Log.error("Failed to fetch fact: ${e.message}")
            Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .entity(mapOf("error" to "Could not fetch fact from external API"))
                .build()
        }
    }

    @GET
    @Path("/{shortenedUrl}")
    @Operation(summary = "Get a cached fact by shortened URL")
    @APIResponse(responseCode = "200", description = "Fact found and access count incremented")
    @APIResponse(responseCode = "404", description = "Fact not found")
    fun getFact(@PathParam("shortenedUrl") shortenedUrl: String): Response {
        val fact = factService.getFact(shortenedUrl)
        return if (fact != null) {
            Response.ok(fact).build()
        } else {
            Response.status(Response.Status.NOT_FOUND).build()
        }
    }

    @GET
    @Operation(summary = "Get all cached facts")
    @APIResponse(responseCode = "200", description = "List of all cached facts")
    fun getAllFacts(): Response {
        val facts = factService.getAllFacts()
        return Response.ok(facts).build()
    }

    @GET
    @Path("/admin/statistics")
    @Operation(summary = "Get access statistics for all facts")
    @APIResponse(responseCode = "200", description = "List of access statistics")
    fun getStatistics(): Response {
        val stats = factService.getStatistics()
        return Response.ok(stats).build()
    }
}