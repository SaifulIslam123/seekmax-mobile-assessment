package com.seekmax.assessment.ui.screen.jobdetail

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seekmax.assessment.JobQuery
import com.seekmax.assessment.USER_TOKEN
import com.seekmax.assessment.repository.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JobDetailViewModel @Inject constructor(
    val preferences: SharedPreferences,
    val repository: JobDetailRepository
) : ViewModel() {

    private val _jobDetailStateFlow =
        MutableStateFlow<NetworkResult<JobQuery.Job>>(NetworkResult.Empty())
    val jobDetailStateFlow = _jobDetailStateFlow.asStateFlow()
    private val _applyJobSharedFlow = MutableSharedFlow<NetworkResult<String>>()
    val applyJobSharedFlow = _applyJobSharedFlow.asSharedFlow()

    fun getJobDetail(id: String) = viewModelScope.launch {
        repository.getJobDetail(id).collect {
            _jobDetailStateFlow.value = it
        }
    }

    fun isUserLoggedIn() = preferences.getString(USER_TOKEN, "")?.isNotEmpty() ?: false

    fun applyJob(id: String) = viewModelScope.launch {
        repository.applyJob(id).collect {
            _applyJobSharedFlow.emit(it)
        }
    }

}
