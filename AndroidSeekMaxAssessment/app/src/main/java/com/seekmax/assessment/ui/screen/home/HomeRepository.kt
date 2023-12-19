package com.seekmax.assessment.ui.screen.home

import android.app.appsearch.exceptions.AppSearchException
import android.util.Log
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Error
import com.apollographql.apollo3.api.Optional
import com.apollographql.apollo3.exception.ApolloException
import com.seekmax.assessment.ActiveQuery
import com.seekmax.assessment.ERROR_MESSAGE
import com.seekmax.assessment.SearchQuery
import com.seekmax.assessment.fragment.JobInfo
import com.seekmax.assessment.repository.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class HomeRepository @Inject constructor(private val apolloClient: ApolloClient) {


    fun getJobList(search: String? = null): Flow<NetworkResult<List<JobInfo>>> =
        search?.let { getSearchJobList(it) } ?: getActiveJobList()

    private fun getActiveJobList(): Flow<NetworkResult<List<JobInfo>>> {

        return flow {
            emit(NetworkResult.Loading())
            try {
                val response = apolloClient.query(
                    ActiveQuery(
                        Optional.present(100),
                        Optional.present(1)
                    )
                ).execute()
                val jobInfoList = ArrayList<JobInfo>()
                response.data?.let {
                    it.active?.jobs?.let {
                        it.forEach { jobInfoList.add(it.jobInfo) }
                        emit(NetworkResult.Success(data = jobInfoList.toList()))
                    } ?: run {
                        response.errors?.let {
                            if (it.isNotEmpty()) emit(NetworkResult.Error(it[0].message))
                        }
                    }
                } ?: emit(NetworkResult.Error(ERROR_MESSAGE))
            } catch (e: ApolloException) {
                emit(NetworkResult.Error(e.message.toString()))
            }

        }.catch {
            emit(NetworkResult.Error(it.message.toString()))
        }.flowOn(Dispatchers.IO)
    }

    private fun getSearchJobList(search: String): Flow<NetworkResult<List<JobInfo>>> {

        return flow {
            emit(NetworkResult.Loading())
            try {
                val response = apolloClient.query(
                    SearchQuery(
                        search,
                        Optional.present(100),
                        Optional.present(1)
                    )
                ).execute()
                val jobInfoList = ArrayList<JobInfo>()

                response.data?.let {
                    it.search?.jobs?.let {
                        it.forEach { jobInfoList.add(it.jobInfo) }
                        emit(NetworkResult.Success(data = jobInfoList.toList()))
                    } ?: run {
                        response.errors?.let {
                            if (it.isNotEmpty()) emit(NetworkResult.Error(it[0].message))
                        }
                    }
                } ?: emit(NetworkResult.Error(ERROR_MESSAGE))
            } catch (e: ApolloException) {
                emit(NetworkResult.Error(e.message.toString()))
            }
        }.catch {
            emit(NetworkResult.Error(it.message.toString()))
        }.flowOn(Dispatchers.IO)

    }
}

