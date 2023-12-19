package com.seekmax.assessment.repository

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloException
import com.seekmax.assessment.ApplyMutation
import com.seekmax.assessment.ERROR_MESSAGE
import com.seekmax.assessment.JobQuery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class JobDetailRepository @Inject constructor(private val apolloClient: ApolloClient) {

    suspend fun getJobDetail(id: String): Flow<NetworkResult<JobQuery.Job>> {
        return flow {
            emit(NetworkResult.Loading())
            try {
                val response = apolloClient.query(JobQuery(id)).execute()
                response.data?.let {
                    it.job?.let { emit(NetworkResult.Success(data = it)) } ?: run {
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

    suspend fun applyJob(id: String): Flow<NetworkResult<String>> {
        return flow {
            emit(NetworkResult.Loading())
            try {
                val response = apolloClient.mutation(ApplyMutation(id)).execute()
                val result = response.data?.apply
                //{"data":{"apply":true}}
                response.data?.let {
                    result?.let {
                        if (result == true) emit(NetworkResult.Success(data = result.toString()))
                    } ?: run {
                        response.errors?.let {
                            val error = it[0].message
                            if (it.isNotEmpty()) emit(NetworkResult.Error(error))
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