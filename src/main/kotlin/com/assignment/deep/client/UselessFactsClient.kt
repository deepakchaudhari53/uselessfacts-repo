package com.assignment.deep.client

import com.assignment.deep.model.ApiFactResponse
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.QueryParam
import jakarta.ws.rs.core.MediaType
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient

@RegisterRestClient(configKey = "useless-facts-api")
@Path("/api/v2")
interface UselessFactsClient {

    @GET
    @Path("/random.json")
    @Produces(MediaType.APPLICATION_JSON)
    fun getRandomFact(@QueryParam("language") language: String = "en"): ApiFactResponse
}