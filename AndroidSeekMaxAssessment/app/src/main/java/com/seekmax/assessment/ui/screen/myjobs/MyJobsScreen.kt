package com.seekmax.assessment.ui.screen.myjobs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.seekmax.assessment.R
import com.seekmax.assessment.repository.NetworkResult
import com.seekmax.assessment.ui.component.ProgressHelper
import com.seekmax.assessment.ui.component.MessageText
import com.seekmax.assessment.ui.component.JobList
import com.seekmax.assessment.ui.component.LoginUi

@Composable
fun MyJobsScreen(navController: NavController) {
    val viewModel: MyJobsViewModel = hiltViewModel()
    if (viewModel.isUserLoggedIn()) {
        ShowMyJobList(navController, viewModel)
    } else LoginUi(
        navController
    )
}

@Composable
fun ShowMyJobList(navController: NavController, viewModel: MyJobsViewModel) {

    LaunchedEffect(true) {
        viewModel.getMyJobList()
    }
    val myJobList by viewModel.myJobListStateFlow.collectAsStateWithLifecycle()
    when (myJobList) {
        is NetworkResult.Success -> {
            ProgressHelper.dismissDialog()
            myJobList.data?.let {
                if (it.isNotEmpty())
                    JobList(navController = navController, jobList = it, false)
                else
                    MessageText(text = stringResource(id = R.string.no_job_found))
            }
        }

        is NetworkResult.Loading -> ProgressHelper.showDialog(LocalContext.current)
        is NetworkResult.Error -> {
            ProgressHelper.dismissDialog()
            MessageText(text = myJobList.message.toString())
        }

        else -> {
        }
    }

}

