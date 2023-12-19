package com.seekmax.assessment.ui.screen.profile

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seekmax.assessment.USER_NAME
import com.seekmax.assessment.USER_TOKEN
import com.seekmax.assessment.repository.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val preferences: SharedPreferences,
    private val repository: ProfileRepository
) : ViewModel() {

    private val _userNameSateFlow = MutableSharedFlow<NetworkResult<String>>()
    val userNameSateFlow = _userNameSateFlow.asSharedFlow()

    private val _passwordSharedFlow = MutableSharedFlow<NetworkResult<Boolean>>()
    val passwordSharedFlow = _passwordSharedFlow.asSharedFlow()

    val loginSateFlow =
        MutableStateFlow(preferences.getString(USER_TOKEN, "")?.isNotEmpty() ?: false)

    fun updateUserName(name: String) = viewModelScope.launch {
        repository.updateUserName(name).collect {
            _userNameSateFlow.emit(it)
        }
    }

    fun updatePassword(password: String) = viewModelScope.launch {
        repository.updateUserPassword(password).collect { _passwordSharedFlow.emit(it) }
    }

    fun logout() = preferences.edit().clear().apply()

    fun presentUserName() = preferences.getString(USER_NAME, "") ?: ""

}