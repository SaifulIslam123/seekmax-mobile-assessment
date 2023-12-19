package com.seekmax.assessment.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.seekmax.assessment.JOB_DETAIL
import com.seekmax.assessment.R
import com.seekmax.assessment.fragment.JobInfo
import com.seekmax.assessment.ui.screen.BottomNavigationScreens
import com.seekmax.assessment.ui.theme.textPrimary
import com.seekmax.assessment.ui.theme.textSecondary

@Composable
fun JobList(
    navController: NavController,
    jobList: List<JobInfo>,
    isShowApplyIcon: Boolean = true
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(jobList) {
            JobItemView(navController, it, isShowApplyIcon)
        }
    }
}

@Composable
fun JobItemView(navController: NavController, it: JobInfo, isShowApplyIcon: Boolean) {

    Card(
        shape = RoundedCornerShape(5.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate("$JOB_DETAIL/${it._id}")
            }, colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    it.positionTitle,
                    color = textPrimary,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (isShowApplyIcon && it.haveIApplied) {
                    Image(
                        painterResource(R.drawable.ic_check),
                        contentDescription = "",
                        modifier = Modifier.size(20.dp)

                    )
                }
            }
            Text(
                it.industry,
                color = textSecondary,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(26.dp))
            Text(
                it.description,
                color = textPrimary,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }

}