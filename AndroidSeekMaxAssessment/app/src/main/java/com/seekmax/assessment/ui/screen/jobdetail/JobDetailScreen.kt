package com.seekmax.assessment.ui.screen.jobdetail

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.seekmax.assessment.JobQuery
import com.seekmax.assessment.R
import com.seekmax.assessment.RELOAD_DATA
import com.seekmax.assessment.repository.NetworkResult
import com.seekmax.assessment.ui.ProgressHelper
import com.seekmax.assessment.ui.component.MessageText
import com.seekmax.assessment.ui.screen.BottomNavigationScreens
import com.seekmax.assessment.ui.theme.button
import com.seekmax.assessment.ui.theme.textPrimary


@Composable
fun JobDetailScreen(navController: NavController, jobId: String) {

    val viewModel: JobDetailViewModel = hiltViewModel()
    val jobDetailStateFlow by viewModel.jobDetailStateFlow.collectAsStateWithLifecycle()

    LaunchedEffect(true) {
        viewModel.getJobDetail(jobId)
    }

    Scaffold(content = {
        Surface(modifier = Modifier.padding(it)) {
            when (jobDetailStateFlow) {
                is NetworkResult.Success -> {
                    ProgressHelper.dismissDialog()
                    ShowJobDetail(navController, jobDetailStateFlow.data!!, viewModel)
                }

                is NetworkResult.Loading -> ProgressHelper.showDialog(LocalContext.current)
                is NetworkResult.Error -> {
                    ProgressHelper.dismissDialog()
                    MessageText(text = jobDetailStateFlow.message.toString())
                }

                else -> {}
            }
        }
    })

}

@Composable
fun ShowJobDetail(navController: NavController, job: JobQuery.Job, viewModel: JobDetailViewModel) {

    var jobAppliedState by remember { mutableStateOf(job.haveIApplied) }
    val context = LocalContext.current

    LaunchedEffect(viewModel.applyJobSharedFlow) {
        viewModel.applyJobSharedFlow.collect {
            when (it) {
                is NetworkResult.Success -> {
                    ProgressHelper.dismissDialog()
                    jobAppliedState = true
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set(RELOAD_DATA, true)
                }

                is NetworkResult.Loading -> ProgressHelper.showDialog(context)
                is NetworkResult.Error -> {
                    Toast.makeText(context, it.message.toString(), Toast.LENGTH_LONG).show()
                    ProgressHelper.dismissDialog()
                }

                else -> {}
            }
        }
    }

    Scaffold(
        content = {
            Surface(modifier = Modifier.padding(it)) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 70.dp)
                    ) {
                        Text(
                            job.positionTitle,
                            color = textPrimary,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (jobAppliedState) {
                            Image(
                                painterResource(R.drawable.ic_check),
                                contentDescription = "",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.size(20.dp))
                    TextWithLeftImage(R.drawable.ic_company, "Company Name: ${job.company.name}")
                    TextWithLeftImage(R.drawable.ic_location, "Location: ${job.location}")
                    TextWithLeftImage(
                        R.drawable.ic_salary,
                        "Salary: ${job.salaryRange.min} - ${job.salaryRange.max}"
                    )
                    TextWithLeftImage(R.drawable.ic_info, job.description)
                    //if (!jobAppliedState) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Button(
                            onClick = {
                                if (!jobAppliedState) {
                                    if (viewModel.isUserLoggedIn()) {
                                        viewModel.applyJob(job._id)
                                    } else {
                                        navController.navigate(BottomNavigationScreens.Profile.route)
                                    }
                                }

                            },
                            shape = RoundedCornerShape(5.dp),
                            modifier = Modifier
                                .height(50.dp)
                                .fillMaxWidth(),
                            colors = if (jobAppliedState) ButtonDefaults.buttonColors(
                                containerColor = Color.LightGray
                            ) else ButtonDefaults.buttonColors(containerColor = button)
                        ) {
                            if (jobAppliedState)
                                Text("ALREADY APPLIED", color = Color.Black)
                            else
                                Text("APPLY", color = Color.White)
                        }
                    }
                    // }
                }
            }
        }
    )
}

@Composable
fun TextWithLeftImage(icon: Int, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp)
    ) {
        // Left-aligned image
        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
            colorFilter = ColorFilter.tint(Color.DarkGray),
            modifier = Modifier
                .size(24.dp)
        )

        Text(
            text,
            color = textPrimary,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 12.dp)
        )

    }
}