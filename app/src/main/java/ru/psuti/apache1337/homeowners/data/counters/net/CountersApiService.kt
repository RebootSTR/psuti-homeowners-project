package ru.psuti.apache1337.homeowners.data.counters.net

import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import ru.psuti.apache1337.homeowners.data.counters.model.CounterUser
import ru.psuti.apache1337.homeowners.data.counters.model.CountersResponse
import ru.psuti.apache1337.homeowners.data.counters.model.NetCounter
import ru.psuti.apache1337.homeowners.data.counters.model.NetCounterEntry
import ru.psuti.apache1337.homeowners.data.entities.ResponseUser
import ru.psuti.apache1337.homeowners.data.entities.User
import ru.psuti.apache1337.homeowners.domain.counters.model.Counter
import ru.psuti.apache1337.homeowners.domain.counters.model.CounterEntry

interface CountersApiService {
    @GET("rest/counters")
    suspend fun getCounters(): Response<List<NetCounter>>

    @GET("rest/counters/history")
    suspend fun getCountersHistory(): Response<List<NetCounter>>

    @PUT("rest/counters")
    suspend fun putCounterValues(
        @Body counterEntries: List<NetCounterEntry>
    ): Response<String>

    @GET("rest/profile")
    suspend fun getProfile(): Response<ResponseUser>
}