package com.seekmax.assessment.ui.screen.myjobs

import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Optional
import com.seekmax.assessment.ERROR_MESSAGE
import com.seekmax.assessment.MyJobsQuery
import com.seekmax.assessment.fragment.JobInfo
import com.seekmax.assessment.repository.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class MyJobsRepository @Inject constructor(private val apolloClient: ApolloClient) {

    fun getMyApplyJobList(): Flow<NetworkResult<List<JobInfo>>> {

        return flow {
            emit(NetworkResult.Loading())
            val response = apolloClient.query(
                MyJobsQuery(
                    Optional.present(100),
                    Optional.present(1)
                )
            ).execute()
            val jobInfoList = ArrayList<JobInfo>()
            response.data?.let {
                it.myJobs?.jobs?.let { jobs ->
                    jobs.forEach {
                        jobInfoList.add(it.jobInfo)
                    }
                    if (jobInfoList.isNotEmpty()) emit(NetworkResult.Success(data = jobInfoList.toList()))
                } ?: run {
                    response.errors?.let {
                        if (it.isNotEmpty()) emit(NetworkResult.Error(it[0].message))
                    }
                }
            } ?: emit(NetworkResult.Error(ERROR_MESSAGE))

        }.catch {
            emit(NetworkResult.Error(it.message.toString()))
        }.flowOn(Dispatchers.IO)
    }
}
