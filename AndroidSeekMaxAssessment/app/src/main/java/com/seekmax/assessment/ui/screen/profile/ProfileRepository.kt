package com.seekmax.assessment.ui.screen.profile

import android.content.SharedPreferences
import com.apollographql.apollo3.ApolloCall
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.Mutation
import com.apollographql.apollo3.api.Operation
import com.apollographql.apollo3.exception.ApolloException
import com.seekmax.assessment.ERROR_MESSAGE
import com.seekmax.assessment.USER_NAME
import com.seekmax.assessment.UpdatePasswordMutation
import com.seekmax.assessment.UpdateUsernameMutation
import com.seekmax.assessment.repository.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val preferences: SharedPreferences,
    private val apolloClient: ApolloClient
) {

    fun updateUserName(name: String): Flow<NetworkResult<String>> {
        return flow {
            emit(NetworkResult.Loading())
            try {
                val response = apolloClient.mutation(UpdateUsernameMutation(name)).execute()
                response.data?.let {
                    it.updateUsername?.let {
                        if (it) {
                            preferences.edit().putString(USER_NAME, name).apply()
                            emit(NetworkResult.Success(data = name))
                        }
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

    fun updateUserPassword(password: String): Flow<NetworkResult<Boolean>> {
        return flow {
            emit(NetworkResult.Loading())
            try {
                val response = apolloClient.mutation(UpdatePasswordMutation(password)).execute()
                response.data?.let {
                    it.updatePassword?.let {
                        if (it) emit(NetworkResult.Success(data = true))
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