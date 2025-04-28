package com.example.trainingapp.screens.progress.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trainingapp.ui.theme.CardColor
import com.example.trainingapp.ui.theme.TextDark
import com.example.trainingapp.ui.theme.TextMedium
import com.example.trainingapp.viewmodels.WorkoutSummary
import com.example.trainingapp.viewmodels.formatTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutHistoryItem(
    workout: WorkoutSummary,
    onClick: (Long) -> Unit
) {
    Card(
        onClick = { onClick(workout.id) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = workout.date,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Text(
                    text = "${workout.exercises} exercises • ${formatTime(workout.duration)}",
                    color = TextMedium,
                    fontSize = 14.sp
                )
            }

            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = "View Details",
                tint = TextMedium
            )
        }
    }
}
