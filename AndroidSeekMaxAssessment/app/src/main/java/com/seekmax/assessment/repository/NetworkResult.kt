package com.seekmax.assessment.repository

sealed class NetworkResult<T>(
    val data: T? = null,
    val message: String? = null,
    val code: Int = 0
) {
    class Success<T>(code: Int = 0, data: T?) : NetworkResult<T>(data, code = code)
    class Error<T>(message: String, data: T? = null, code: Int = 0) :
        NetworkResult<T>(data, message, code)

    class Loading<T> : NetworkResult<T>()
    class Empty<T> : NetworkResult<T>()
}