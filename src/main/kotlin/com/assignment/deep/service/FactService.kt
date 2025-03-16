package com.assignment.deep.service

import com.assignment.deep.exception.FactNotFoundException
import com.assignment.deep.exception.FactStorageException
import com.assignment.deep.model.CachedFact
import com.assignment.deep.model.FactStatistics
import jakarta.enterprise.context.ApplicationScoped
import java.util.concurrent.ConcurrentHashMap

@ApplicationScoped
class FactService {

    private val cache = ConcurrentHashMap<String, CachedFact>()

    fun saveFact(fact: CachedFact) {
        try {
            cache[fact.shortenedUrl] = fact
            // Verify storage
            if (!cache.containsKey(fact.shortenedUrl)) {
                throw FactStorageException()
            }
        } catch (e: Exception) {
            throw FactStorageException()
        }
    }

    fun getFact(shortenedUrl: String): CachedFact? {
        return cache[shortenedUrl] ?: throw FactNotFoundException(shortenedUrl)
    }

    fun getAllFacts(): List<CachedFact> = cache.values.toList()

    fun getStatistics(): List<FactStatistics> =
        cache.values.map { FactStatistics(it.shortenedUrl, it.accessCount) }
}