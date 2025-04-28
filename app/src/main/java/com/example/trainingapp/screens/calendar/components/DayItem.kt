package com.example.trainingapp.screens.calendar.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trainingapp.ui.theme.SportRed
import com.example.trainingapp.ui.theme.TextDark
import com.example.trainingapp.ui.theme.TextMedium
import com.example.trainingapp.viewmodels.DaySchedule
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun DayItem(
    daySchedule: DaySchedule,
    isToday: Boolean
) {
    val hasWorkout = daySchedule.workoutDay != null
    val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
    val dateFormat = SimpleDateFormat("d", Locale.getDefault())

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(48.dp)
    ) {
        Text(
            text = dayFormat.format(daySchedule.date),
            fontSize = 14.sp,
            color = TextMedium
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isToday && hasWorkout -> SportRed
                        isToday -> SportRed.copy(alpha = 0.3f)
                        hasWorkout -> SportRed.copy(alpha = 0.1f)
                        else -> Color.Transparent
                    }
                )
                .border(
                    width = if (isToday) 2.dp else 0.dp,
                    color = if (isToday) SportRed else Color.Transparent,
                    shape = CircleShape
                )
        ) {
            Text(
                text = dateFormat.format(daySchedule.date),
                color = when {
                    isToday && hasWorkout -> Color.White
                    isToday -> SportRed
                    else -> TextDark
                },
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
            )
        }

        if (hasWorkout) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(
                        if (isToday) Color.White else SportRed
                    )
            )
        } else {
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}