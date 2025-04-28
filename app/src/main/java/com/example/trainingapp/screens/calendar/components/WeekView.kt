package com.example.trainingapp.screens.calendar.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.trainingapp.screens.calendar.isSameDay
import com.example.trainingapp.viewmodels.DaySchedule
import java.util.Calendar
import java.util.Date

@Composable
fun WeekView(
    weekSchedule: List<DaySchedule>,
    today: Date = Calendar.getInstance().time
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(weekSchedule) { daySchedule ->
            DayItem(
                daySchedule = daySchedule,
                isToday = isSameDay(daySchedule.date, today)
            )
        }
    }
}