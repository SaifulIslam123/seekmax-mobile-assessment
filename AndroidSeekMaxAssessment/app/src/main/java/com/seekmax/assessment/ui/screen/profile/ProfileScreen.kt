package com.seekmax.assessment.ui.screen.profile

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.seekmax.assessment.R
import com.seekmax.assessment.RELOAD_DATA
import com.seekmax.assessment.repository.NetworkResult
import com.seekmax.assessment.ui.component.ProgressHelper
import com.seekmax.assessment.ui.component.LoginUi
import com.seekmax.assessment.ui.theme.button

@Composable
fun ProfileScreen(navController: NavController) {

    val viewModel: ProfileViewModel = hiltViewModel()
    val loginStateFlow by viewModel.loginSateFlow.collectAsStateWithLifecycle()
    if (loginStateFlow) ProfileUi(navController, viewModel) else LoginUi(navController)
}

@Composable
fun ProfileUi(navController: NavController, viewModel: ProfileViewModel) {

    var displayNameState by remember { mutableStateOf(viewModel.presentUserName()) }
    var updateNameState by remember { mutableStateOf("") }
    var newPasswordState by remember { mutableStateOf("") }
    var confirmPasswordState by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val userNameUpdateMsg = stringResource(id = R.string.username_update_message)
    val passwordChangeMsg = stringResource(id = R.string.password_change_message)

    LaunchedEffect(viewModel.userNameSateFlow) {
        viewModel.userNameSateFlow.collect {
            when (it) {
                is NetworkResult.Loading -> {
                    ProgressHelper.showDialog(context)
                }

                is NetworkResult.Success -> {
                    ProgressHelper.dismissDialog()
                    Toast.makeText(
                        context,
                        userNameUpdateMsg,
                        Toast.LENGTH_LONG
                    ).show()
                    displayNameState = it.data.toString()
                }

                is NetworkResult.Error -> {
                    ProgressHelper.dismissDialog()
                }

                else -> {}
            }
        }
    }

    LaunchedEffect(viewModel.passwordSharedFlow) {
        viewModel.passwordSharedFlow.collect {
            when (it) {
                is NetworkResult.Loading -> {
                    ProgressHelper.showDialog(context)
                }

                is NetworkResult.Success -> {
                    ProgressHelper.dismissDialog()
                    Toast.makeText(context, passwordChangeMsg, Toast.LENGTH_LONG)
                        .show()
                }

                is NetworkResult.Error -> {
                    ProgressHelper.dismissDialog()
                }

                else -> {}
            }
        }
    }

    fun isValidPassword() =
        newPasswordState.isNotEmpty() && confirmPasswordState.isNotEmpty() &&
                newPasswordState.equals(confirmPasswordState)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 30.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.DarkGray,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                )
                Text(text = displayNameState, style = MaterialTheme.typography.h6)
            }

        }
        Text(
            text = stringResource(id = R.string.username_title),
            modifier = Modifier.padding(top = 20.dp),
            style = MaterialTheme.typography.h6
        )
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            value = updateNameState,
            onValueChange = { updateNameState = it },
            placeholder = { Text(text = stringResource(id = R.string.username_hint)) })
        Button(
            onClick = { viewModel.updateUserName(updateNameState) },
            enabled = updateNameState.isNotEmpty(),
            shape = RoundedCornerShape(5.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = button)
        ) {
            Text(stringResource(id = R.string.username_btn_text), color = Color.White)
        }
        Text(
            text = stringResource(id = R.string.update_password_title),
            modifier = Modifier.padding(top = 30.dp),
            style = MaterialTheme.typography.h6
        )
        var passwordVisibility: Boolean by remember { mutableStateOf(false) }
        TextField(
            visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = {
                    passwordVisibility = !passwordVisibility
                }) {
                    Icon(
                        if (passwordVisibility) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "",
                        tint = Color.Gray
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            value = newPasswordState,
            onValueChange = { newPasswordState = it },
            placeholder = { Text(text = stringResource(id = R.string.new_password_hint)) })
        var confirmPasswordVisibility: Boolean by remember { mutableStateOf(false) }
        TextField(
            visualTransformation = if (confirmPasswordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = {
                    confirmPasswordVisibility = !confirmPasswordVisibility
                }) {
                    Icon(
                        if (confirmPasswordVisibility) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "",
                        tint = Color.Gray
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            value = confirmPasswordState,
            onValueChange = { confirmPasswordState = it },
            placeholder = { Text(text = stringResource(id = R.string.confirm_password_hint)) })
        Button(
            onClick = { viewModel.updatePassword(newPasswordState) },
            enabled = isValidPassword(),
            shape = RoundedCornerShape(5.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = button)
        ) {
            Text(stringResource(id = R.string.update_password_btn_text), color = Color.White)
        }
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            Button(
                onClick = {
                    viewModel.logout()
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(RELOAD_DATA, true)
                    viewModel.loginSateFlow.value = false
                },
                shape = RoundedCornerShape(5.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text(stringResource(id = R.string.logout), color = Color.White)
            }
        }

    }


}