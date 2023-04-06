package ru.psuti.apache1337.homeowners.data.counters

import android.util.Log
import ru.psuti.apache1337.homeowners.base.AppSharedPreferences
import ru.psuti.apache1337.homeowners.data.auth.AuthMapper
import ru.psuti.apache1337.homeowners.data.counters.db.CountersDao
import ru.psuti.apache1337.homeowners.data.counters.net.CountersApiService
import ru.psuti.apache1337.homeowners.domain.counters.CountersRepository
import ru.psuti.apache1337.homeowners.domain.counters.model.Counter
import ru.psuti.apache1337.homeowners.domain.counters.model.CounterEntry
import ru.psuti.apache1337.homeowners.domain.counters.model.CounterHistoryEntry
import javax.inject.Inject

class CountersRepositoryImpl @Inject constructor(
    private val apiService: CountersApiService,
    private val dao: CountersDao,
    private val prefs: AppSharedPreferences
) : CountersRepository {
    override suspend fun updateCounters() {
        val response = apiService.getCounters()
        if (response.code() == 200) {
            dao.clearCounters()
            val mapped = response.body()?.map { e ->
                CountersMapper.mapNetToDB(e)
            }

            if (mapped != null) {
                dao.updateCounters(mapped)
            } else {
                Log.e("counters", "map returned null")
            }

        } else {
            throw Exception("Error: ${response.code()} ${response.message()}")
        }
    }

    override suspend fun getCountersList(): List<Counter> {
        val result = dao.getAllCounters()
        return result.map { e -> CountersMapper.mapDBToDomain(e) }
    }

    override suspend fun updateHistory() {
        val response = apiService.getCountersHistory()
        if (response.code() == 200) {
            dao.clearHistory()
            val mapped = response.body()?.map { e ->
                CountersMapper.mapNetToDBHistory(e)
            }
            if (mapped != null) {
                dao.updateHistory(mapped)
                Log.e("repo", "success")
            } else {
                Log.e("counters", "map returned null")
            }

        } else {
            throw Exception("Error: ${response.code()} ${response.message()}")
        }
    }

    override suspend fun getCountersHistory(): List<CounterHistoryEntry> {
        val result = dao.getAllHistory()
        Log.e("resdao", result.toString())
        return result.map { e -> CountersMapper.mapDbToDomainHistory(e) }
    }

    override suspend fun sendCounterEntries(values: List<CounterEntry>) {
        val mapped = values.map { e -> CountersMapper.mapDomainToNet(e) }

        val response = apiService.putCounterValues(mapped)
        if (response.code() != 200) {
            throw Exception("Error: ${response.code()} ${response.message()}")
        }
    }

    override suspend fun cacheUnsentEntries(values: List<CounterEntry>) {
        val mapped = values.map { e -> CountersMapper.mapDomainToDb(e) }
        dao.cacheUnsentEntries(mapped)
    }

    override suspend fun clearCachedEntries() {
        dao.clearPendingEntries()
    }

    override suspend fun clearHistory() {
        dao.clearHistory()
    }

    override suspend fun sendCachedEntries() {
        val cached = dao.getPendingEntries()
        val mapped = cached.map { e -> CountersMapper.mapDbToNet(e) }

        apiService.putCounterValues(mapped)
    }

    override suspend fun getAddress(): String {
        val response = apiService.getProfile()
        val dbUser = dao.getUser()
        return if (response.code() == 200) CountersMapper.mapUserToAddressString(
            AuthMapper.map(
                response.body()!!
            )
        ) else CountersMapper.mapUserToAddressString(dbUser)
    }

    override suspend fun getDemoCounters(): List<Counter> {
        var res = dao.getDemoCounters()
        if (res.isEmpty()) {
            setDemoData()
        }
        res = dao.getDemoCounters()
        return res.map { e -> CountersMapper.demoToDomain(e) }
    }

    override suspend fun getDemoHistory(): List<CounterHistoryEntry> {
        val res = dao.getDemoHistory()
        return res.map { e -> CountersMapper.demoToDomainHistory(e) }
    }

    override suspend fun setDemoData() {
        dao.setDemoCounters(CountersDemoData.counters)
        dao.setDemoHistory(CountersDemoData.history)
    }

    override fun getUserId(): Int {
        return prefs.userId.get()
    }

}