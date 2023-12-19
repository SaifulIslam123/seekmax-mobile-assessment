package com.seekmax.assessment.ui.screen.login

import android.content.SharedPreferences
import android.util.Log
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.exception.ApolloException
import com.apollographql.apollo3.network.http.HttpInfo
import com.seekmax.assessment.AuthMutation
import com.seekmax.assessment.ERROR_MESSAGE
import com.seekmax.assessment.USER_NAME
import com.seekmax.assessment.USER_TOKEN
import com.seekmax.assessment.repository.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val preferences: SharedPreferences,
    private val apolloClient: ApolloClient
) {
    suspend fun login(userName: String, password: String): Flow<NetworkResult<String>> {

        return flow {
            emit(NetworkResult.Loading())
            try {
                val response = apolloClient.mutation(AuthMutation(userName, password)).execute()
                response.data?.let {
                    val token = it.auth
                    if (!it.auth.isNullOrEmpty()) {
                        preferences.edit().putString(USER_TOKEN, token).apply()
                        preferences.edit().putString(USER_NAME, userName).apply()
                        emit(NetworkResult.Success(data = token))
                    } else {
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